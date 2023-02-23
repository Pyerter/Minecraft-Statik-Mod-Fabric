package pyerter.statik.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import pyerter.statik.block.entity.KitchenStoveStationEntity;

import java.util.ArrayList;
import java.util.List;

public class KitchenStoveRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> ingredients;
    private final Integer cookTime;
    private final Integer energyCost;
    private final Integer cookLevel;

    public KitchenStoveRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Integer cookTime, Integer energyCost, Integer cookLevel) {
        this.id = id;
        this.output = output;
        this.ingredients = recipeItems;
        this.cookTime = cookTime;
        this.energyCost = energyCost;
        this.cookLevel = cookLevel;

        List<Item> acceptedItems = new ArrayList<>();
        for (Ingredient ing: recipeItems) {
            ItemStack[] stacks = ing.getMatchingStacks();
            for (int i = 0; i < stacks.length; i++) {
                if (!acceptedItems.contains(stacks[i].getItem())) {
                    acceptedItems.add(stacks[i].getItem());
                }
            }
        }
        for (Item item: acceptedItems) {
            KitchenStoveStationEntity.tryRegisterQuickTransfer(item, cookLevel);
        }
    }

    @Override
    // This recipe type will not be shaped, it relies on only the presence
    // of the required ingredients in any slots.
    public boolean matches(SimpleInventory inventory, World world) {
        // end short if client, due to recipeItems not being populated on client
        if (world.isClient())
            return false;

        // if cook level is 0, check the bottom two slots
        if (cookLevel == 0)
            return (matchesIngredients(inventory.getStack(2)) || matchesIngredients(inventory.getStack(1)));

        // if cook level is 1, check the top two slots
        if (cookLevel == 1)
            return (matchesIngredients(inventory.getStack(4)) || matchesIngredients(inventory.getStack(3)));

        return false;
    }



    // Slot: 1
    public boolean matchesBottomLeft(SimpleInventory inventory, World world) {
        return matchesSlot(inventory, world, 1);
    }
    // Slot: 2
    public boolean matchesBottomRight(SimpleInventory inventory, World world) {
        return matchesSlot(inventory, world, 2);
    }
    // Slot: 3
    public boolean matchesTopLeft(SimpleInventory inventory, World world) {
        return matchesSlot(inventory, world, 3);
    }
    // Slot: 4
    public boolean matchesTopRight(SimpleInventory inventory, World world) {
        return matchesSlot(inventory, world, 4);
    }

    public boolean matchesSlot(SimpleInventory inventory, World world, int slot) {
        int slotLevel = -1;
        if (slot == 2 || slot == 1)
            slotLevel = 0;
        else if (slot == 4 || slot == 3)
            slotLevel = 1;
        if (cookLevel != slotLevel)
            return false;

        for (Ingredient ing: ingredients) {
            if (ing.test(inventory.getStack(slot)))
                return true;
        }
        return false;
    }

    public boolean matchesIngredients(ItemStack stack) {
        for (Ingredient ing: ingredients)
            if (ing.test(stack))
                return true;

        return false;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true; // ?????
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Integer getCookTime() { return cookTime; }

    public Integer getEnergyCost() { return energyCost; }

    public Integer getCookLevel() { return cookLevel; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<KitchenStoveRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "kitchen_stove_station";
    }

    public static class Serializer implements RecipeSerializer<KitchenStoveRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "kitchen_stove_station";

        @Override
        public KitchenStoveRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Integer cookTime = JsonHelper.getInt(json, "cookTime");
            Integer energyCost = JsonHelper.getInt(json, "energyCost");
            Integer cookLevel = JsonHelper.getInt(json, "cookLevel");

            return new KitchenStoveRecipe(id, output, inputs, cookTime, energyCost, cookLevel);
        }

        @Override
        public KitchenStoveRecipe read(Identifier id, PacketByteBuf buf) {
            Integer cookLevel = buf.readInt();
            Integer energyCost = buf.readInt();
            Integer cookTime = buf.readInt();

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new KitchenStoveRecipe(id, output, inputs, cookTime, energyCost, cookLevel);
        }

        @Override
        public void write(PacketByteBuf buf, KitchenStoveRecipe recipe) {
            buf.writeInt(recipe.getCookLevel());
            buf.writeInt(recipe.getEnergyCost());
            buf.writeInt(recipe.getCookTime());
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing: recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput());
        }
    }


}
