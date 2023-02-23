package pyerter.statik.integration.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.Identifier;
import pyerter.statik.integration.REIStatikPlugin;
import pyerter.statik.recipe.TridiRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TridiRecipeDisplay extends BasicDisplay {
    public TridiRecipeDisplay(TridiRecipe recipe) {
        super(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.ofNullable(recipe.getId()));
    }

    public TridiRecipeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        super(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIStatikPlugin.TRIDI;
    }

    public static DisplaySerializer<TridiRecipeDisplay> getSerializer() {
        return BasicDisplay.Serializer.ofSimple(TridiRecipeDisplay::new);
    }
}
