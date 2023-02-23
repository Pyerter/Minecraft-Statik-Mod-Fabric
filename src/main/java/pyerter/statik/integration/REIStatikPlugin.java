package pyerter.statik.integration;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.plugins.REIPlugin;
import org.jetbrains.annotations.NotNull;
import pyerter.statik.Statik;
import pyerter.statik.integration.rei.REIStatikPluginProvider;
import pyerter.statik.integration.rei.display.EngineeringStationRefineRecipeDisplay;
import pyerter.statik.integration.rei.display.TridiRecipeDisplay;

public class REIStatikPlugin implements REIPlugin {
    public static final CategoryIdentifier<EngineeringStationRefineRecipeDisplay> REFINING = CategoryIdentifier.of(Statik.MOD_ID, "refining");
    public static final CategoryIdentifier<TridiRecipeDisplay> TRIDI = CategoryIdentifier.of(Statik.MOD_ID, "tridi_display");


    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    @Override
    public Class getPluginProviderClass() {
        return REIStatikPluginProvider.class;
    }
}
