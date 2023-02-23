package pyerter.statik.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import pyerter.statik.integration.rei.category.EngineeringStationRefineRecipeCategory;
import pyerter.statik.integration.rei.category.TridiRecipeCategory;
import pyerter.statik.integration.rei.display.EngineeringStationRefineRecipeDisplay;
import pyerter.statik.integration.rei.display.TridiRecipeDisplay;
import pyerter.statik.recipe.EngineeringStationRefineRecipe;
import pyerter.statik.recipe.TridiRecipe;

public class REIStatikClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {

        registry.add(new TridiRecipeCategory());
        registry.add(new EngineeringStationRefineRecipeCategory());

    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {

        registry.registerRecipeFiller(TridiRecipe.class, TridiRecipe.Type.INSTANCE, TridiRecipeDisplay::new);
        registry.registerRecipeFiller(EngineeringStationRefineRecipe.class, EngineeringStationRefineRecipe.Type.INSTANCE, EngineeringStationRefineRecipeDisplay::new);

    }
}
