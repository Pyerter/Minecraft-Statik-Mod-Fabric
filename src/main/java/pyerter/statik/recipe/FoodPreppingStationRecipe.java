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
import pyerter.statik.block.entity.FoodPreppingStationEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodPreppingStationRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> ingredients;
    private final Ingredient leftLayerIngredient;
    private final Ingredient rightLayerIngredient;
    private final Ingredient toolIngredient;

    public FoodPreppingStationRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Ingredient leftLayerIngredient, Ingredient rightLayerIngredient, Ingredient toolIngredient) {
        this.id = id;
        this.output = output;
        this.ingredients = recipeItems;
        this.leftLayerIngredient = leftLayerIngredient;
        this.rightLayerIngredient = rightLayerIngredient;
        this.toolIngredient = toolIngredient;

        if (!toolIngredient.isEmpty())
            Arrays.stream(toolIngredient.getMatchingStacks()).forEach(stack -> FoodPreppingStationEntity.tryRegisterTool(stack.getItem()));
        if (!leftLayerIngredient.isEmpty())
            Arrays.stream(leftLayerIngredient.getMatchingStacks()).forEach(stack -> FoodPreppingStationEntity.tryRegisterLayerIngredient(stack.getItem()));
        if (!rightLayerIngredient.isEmpty())
            Arrays.stream(rightLayerIngredient.getMatchingStacks()).forEach(stack -> FoodPreppingStationEntity.tryRegisterLayerIngredient(stack.getItem()));

        for (Ingredient ing: ingredients) {
            Arrays.stream(ing.getMatchingStacks()).forEach(stack -> FoodPreppingStationEntity.tryRegisterIngredient(stack.getItem()));
        }
    }

    @Override
    // This recipe type will not be shaped, it relies on only the presence
    // of the required ingredients in any slots.
    public boolean matches(SimpleInventory inventory, World world) {
        // end short if client, due to recipeItems not being populated on client
        if (world.isClient())
            return false;

        if (ingredients.size() > 6) {
            Statik.logDebug("Error while crafting FoodPreppingStationRecipe - ingredients list too large for recipe ID " + id);
            return false;
        }

        boolean failedTest = false;
        List<Ingredient> requiredIngredients = new ArrayList<>(ingredients.size());
        for (Ingredient ing: ingredients) { requiredIngredients.add(ing); }

        int toolSlot = 0;
        int leftSlot = 1;
        int rightSlot = 8;

        // test tool
        if (!toolIngredient.isEmpty() && !toolIngredient.test(inventory.getStack(toolSlot)))
            return false;

        // test left and right ingredients
        if (!leftLayerIngredient.isEmpty() && !leftLayerIngredient.test(inventory.getStack(leftSlot)))
            return false;

        if (!rightLayerIngredient.isEmpty() && !rightLayerIngredient.test(inventory.getStack(rightSlot)))
            return false;

        // loop over the 6 center slots of the inventory to find ingredients
        int i = leftSlot + 1;
        while (i < rightSlot && !failedTest) {
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
                } else if (ing == requiredIngredients.size()) {
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
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FoodPreppingStationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "food_prepping_station";
    }

    public static class Serializer implements RecipeSerializer<FoodPreppingStationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "food_prepping_station";

        @Override
        public FoodPreppingStationRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Ingredient leftLayerIngredient = Ingredient.EMPTY;
            try {
                leftLayerIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "left_layer_ingredient"));
            } catch (JsonSyntaxException e) {
                Statik.logDebug("Json syntax exception while reading recipe " + ID + ", assuming empty stack for left_layer_ingredient: " + id.toString());
            }

            Ingredient rightLayerIngredient = Ingredient.EMPTY;
            try {
                rightLayerIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "right_layer_ingredient"));
            } catch (JsonSyntaxException e) {
                Statik.logDebug("Json syntax exception while reading recipe " + ID + ", assuming empty stack for right_layer_ingredient: " + id.toString());
            }

            Ingredient toolIngredient = Ingredient.EMPTY;
            try {
                toolIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "tool_ingredient"));
            } catch (JsonSyntaxException e) {
                Statik.logDebug("Json syntax exception while reading recipe " + ID + ", assuming empty stack for tool_ingredient: " + id.toString());
            }

            return new FoodPreppingStationRecipe(id, output, inputs, leftLayerIngredient, rightLayerIngredient, toolIngredient);
        }

        @Override
        public FoodPreppingStationRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();

            boolean hasLeft = buf.readBoolean();
            Ingredient leftLayerIngredient = Ingredient.EMPTY;
            if (hasLeft)
                leftLayerIngredient = Ingredient.fromPacket(buf);

            boolean hasRight = buf.readBoolean();
            Ingredient rightLayerIngredient = Ingredient.EMPTY;
            if (hasRight)
                rightLayerIngredient = Ingredient.fromPacket(buf);

            boolean hasTool = buf.readBoolean();
            Ingredient toolIngredient = Ingredient.EMPTY;
            if (hasTool)
                toolIngredient = Ingredient.fromPacket(buf);

            return new FoodPreppingStationRecipe(id, output, inputs, leftLayerIngredient, rightLayerIngredient, toolIngredient);
        }

        @Override
        public void write(PacketByteBuf buf, FoodPreppingStationRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing: recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());

            boolean hasLeft = !recipe.leftLayerIngredient.isEmpty();
            buf.writeBoolean(hasLeft);
            if (hasLeft)
                recipe.leftLayerIngredient.write(buf);

            boolean hasRight = !recipe.rightLayerIngredient.isEmpty();
            buf.writeBoolean(hasRight);
            if (hasRight)
                recipe.rightLayerIngredient.write(buf);

            boolean hasTool = !recipe.toolIngredient.isEmpty();
            buf.writeBoolean(hasTool);
            if (hasTool)
                recipe.toolIngredient.write(buf);
        }
    }


}
