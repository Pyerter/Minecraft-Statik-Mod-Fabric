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
import pyerter.statik.block.entity.EngineeringStationEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EngineeringStationRefineRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> ingredients;
    private final Integer hammerCost;
    private final Ingredient engineerIngredient;

    public EngineeringStationRefineRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Integer hammerCost, Ingredient engineerIngredient) {
        this.id = id;
        this.output = output;
        this.ingredients = recipeItems;
        this.hammerCost = hammerCost;
        this.engineerIngredient = engineerIngredient;

        if (ingredients.size() == 1) {
            Arrays.stream(ingredients.get(0).getMatchingStacks()).forEach(stack -> EngineeringStationEntity.acceptedRefiningMaterials.add(stack.getItem()));
        }
        if (!engineerIngredient.isEmpty())
            Arrays.stream(engineerIngredient.getMatchingStacks()).forEach(stack -> EngineeringStationEntity.ENGINEERS_ITEMS.add(stack.getItem()));
    }

    @Override
    // This recipe type will not be shaped, it relies on only the presence
    // of the required ingredients in any slots.
    public boolean matches(SimpleInventory inventory, World world) {
        // end short if client, due to recipeItems not being populated on client
        if (world.isClient())
            return false;

        if (ingredients.size() != 1) {
            Statik.logDebug("Error while crafting EngineeringStationRefineRecipe - ingredients list too large for recipe ID " + id);
            return false;
        }

        List<Ingredient> requiredIngredients = new ArrayList<>(ingredients.size());
        for (Ingredient ing: ingredients) { requiredIngredients.add(ing); }

        if (inventory.getStack(4).isEmpty() && hammerCost > 0)
            return false;

        boolean hasNormalIngredient = requiredIngredients.get(0).test(inventory.getStack(3));
        if (!hasNormalIngredient)
            return false;

        if (engineerIngredient.isEmpty())
            return true;

        boolean hasOptionalIngredient = engineerIngredient.test(inventory.getStack(2));
        return hasOptionalIngredient;
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
        DefaultedList<Ingredient> list = DefaultedList.ofSize(2, Ingredient.EMPTY);
        if (ingredients.size() > 0)
            list.set(0, ingredients.get(0));
        if (!engineerIngredient.isEmpty())
            list.set(1, engineerIngredient);
        return list;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Integer getHammerCost() { return hammerCost; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public Ingredient getEngineerIngredient() { return engineerIngredient; }

    public static class Type implements RecipeType<EngineeringStationRefineRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "engineering_station_refine";
    }

    public static class Serializer implements RecipeSerializer<EngineeringStationRefineRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "engineering_station_refine";

        @Override
        public EngineeringStationRefineRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Integer hammerCost = JsonHelper.getInt(json, "hammer_cost");

            Ingredient engineerIngredient = Ingredient.EMPTY;
            try {
                engineerIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "engineer_ingredient"));
            } catch (JsonSyntaxException e) {
                Statik.logDebug("Json syntax exception while reading recipe " + ID + ", assuming empty stack for engineer_ingredient: " + id.toString());
            }

            return new EngineeringStationRefineRecipe(id, output, inputs, hammerCost, engineerIngredient);
        }

        @Override
        public EngineeringStationRefineRecipe read(Identifier id, PacketByteBuf buf) {
            Integer hammerCost = buf.readInt();

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            boolean hasEngineerIng = buf.readBoolean();
            Ingredient engineerIngredient = Ingredient.EMPTY;
            if (hasEngineerIng)
                engineerIngredient = Ingredient.fromPacket(buf);

            return new EngineeringStationRefineRecipe(id, output, inputs, hammerCost, engineerIngredient);
        }

        @Override
        public void write(PacketByteBuf buf, EngineeringStationRefineRecipe recipe) {
            buf.writeInt(recipe.getHammerCost());
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing: recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput());

            boolean hasEngineerIng = !recipe.engineerIngredient.isEmpty();
            buf.writeBoolean(hasEngineerIng);
            if (hasEngineerIng)
                recipe.engineerIngredient.write(buf);
        }
    }


}
