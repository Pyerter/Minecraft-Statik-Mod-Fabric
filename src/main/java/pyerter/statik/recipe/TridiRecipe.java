package pyerter.statik.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import pyerter.statik.Statik;
import pyerter.statik.block.entity.TridiBlockEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TridiRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> ingredients;
    private final Integer cooktime;
    private final Integer energyCost;

    public TridiRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Integer cookTime, Integer energyCost) {
        this.id = id;
        this.output = output;
        this.ingredients = recipeItems;
        this.cooktime = cookTime;
        this.energyCost = energyCost;

        for (Ingredient ing: ingredients) {
            Arrays.stream(ing.getMatchingStacks()).forEach(stack -> TridiBlockEntity.tryRegisterQuickTransfer(stack.getItem()));
        }
    }

    @Override
    // This recipe type will not be shaped, it relies on only the presence
    // of the required ingredients in any slots.
    public boolean matches(SimpleInventory inventory, World world) {
        // end short if client, due to recipeItems not being populated on client
        if (world.isClient())
            return false;

        boolean failedTest = false;
        List<Ingredient> requiredIngredients = new ArrayList<>(ingredients.size());
        for (Ingredient ing: ingredients) { requiredIngredients.add(ing); }

        // loop over the 5 non-fuel slots of the inventory
        int i = 0;
        while (i < 5 && !failedTest) {
            if (inventory.getStack(i).isEmpty()) {
                i++;
                continue;
            }
            if (requiredIngredients.size() == 0)
                failedTest = true;
            for (int ing = 0; ing < requiredIngredients.size(); ing++) {
                if (requiredIngredients.get(ing).test(inventory.getStack(i))) {
                    requiredIngredients.remove(ing);
                    break;
                } else if (ing == requiredIngredients.size() - 1) {
                    failedTest = true;
                }
            }
            i++;
        }

        boolean doesMatch = !failedTest && requiredIngredients.size() == 0;
        return doesMatch;
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
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Integer getCookTime() { return cooktime; }

    public Integer getEnergyCost() { return energyCost; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<TridiRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "tridi";
    }

    public static class Serializer implements RecipeSerializer<TridiRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "tridi";

        @Override
        public TridiRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Integer cookTime = JsonHelper.getInt(json, "cookTime");
            Integer energyCost = JsonHelper.getInt(json, "energyCost");

            try {
                JsonArray nbtInts = JsonHelper.getArray(json, "nbt_output_values");
                try {
                    for (int i = 0; i < nbtInts.size(); i++) {
                        JsonObject jsonObj = nbtInts.get(i).getAsJsonObject();
                        String nbtId = JsonHelper.getString(jsonObj, "nbt_id");
                        int nbtValue = JsonHelper.getInt(jsonObj, "nbt_value");
                        output.getOrCreateNbt().putInt(nbtId, nbtValue);
                    }
                } catch (JsonSyntaxException e) {
                    Statik.logDebug("Json syntax exception while reading recipe " + ID + ", error while reading nbt integer values : " + id.toString());
                }
            } catch (JsonSyntaxException e) {
                Statik.logDebug("Json syntax exception while reading recipe " + ID + ", assuming empty nbt list for : " + id.toString());
            }

            return new TridiRecipe(id, output, inputs, cookTime, energyCost);
        }

        @Override
        public TridiRecipe read(Identifier id, PacketByteBuf buf) {
            Integer energyCost = buf.readInt();
            Integer cookTime = buf.readInt();

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new TridiRecipe(id, output, inputs, cookTime, energyCost);
        }

        @Override
        public void write(PacketByteBuf buf, TridiRecipe recipe) {
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
