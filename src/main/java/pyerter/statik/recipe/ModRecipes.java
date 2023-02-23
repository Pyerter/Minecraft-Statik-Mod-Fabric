package pyerter.statik.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;

public class ModRecipes {
    public static void registerRecipes() {
        /*
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(PootsAdditions.MOD_ID, TridiRecipe.Serializer.ID),
                TridiRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(PootsAdditions.MOD_ID, TridiRecipe.Type.ID),
                TridiRecipe.Type.INSTANCE);
         */

        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(Statik.MOD_ID, EngineeringStationRefineRecipe.Serializer.ID),
                EngineeringStationRefineRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(Statik.MOD_ID, EngineeringStationRefineRecipe.Type.ID),
                EngineeringStationRefineRecipe.Type.INSTANCE);

        /*
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(PootsAdditions.MOD_ID, KitchenStoveRecipe.Serializer.ID),
                KitchenStoveRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(PootsAdditions.MOD_ID, KitchenStoveRecipe.Type.ID),
                KitchenStoveRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(PootsAdditions.MOD_ID, FoodPreppingStationRecipe.Serializer.ID),
                FoodPreppingStationRecipe.Serializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(PootsAdditions.MOD_ID, FoodPreppingStationRecipe.Type.ID),
                FoodPreppingStationRecipe.Type.INSTANCE);
         */
    }
}
