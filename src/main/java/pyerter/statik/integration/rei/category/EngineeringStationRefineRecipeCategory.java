package pyerter.statik.integration.rei.category;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import pyerter.statik.block.ModBlocks;
import pyerter.statik.integration.REIStatikPlugin;
import pyerter.statik.integration.rei.display.EngineeringStationRefineRecipeDisplay;
import pyerter.statik.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class EngineeringStationRefineRecipeCategory implements DisplayCategory<EngineeringStationRefineRecipeDisplay> {
    public EngineeringStationRefineRecipeCategory() {

    }

    @Override
    public CategoryIdentifier<? extends EngineeringStationRefineRecipeDisplay> getCategoryIdentifier() {
        return REIStatikPlugin.REFINING;
    }

    @Override
    public Text getTitle() {
        return Text.of("Engineering Station Refining");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.ENGINEERING_STATION);
    }

    @Override
    public List<Widget> setupDisplay(EngineeringStationRefineRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 20, bounds.getCenterY() - 9);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        Point current = startPoint.clone();
        current.y -= 14;
        widgets.add(Widgets.createSlot(current)
                .entries(display.getInputEntries().size() > 0 ? display.getInputEntries().get(0) : EntryIngredients.of(ItemStack.EMPTY))
                .markInput());

        current.y += 28;
        widgets.add(Widgets.createSlot(current)
                .entries(display.getInputEntries().size() > 1 ? display.getInputEntries().get(1) : EntryIngredients.of(ItemStack.EMPTY))
                .markInput());

        current.y -= 14;
        current.x += 24;
        widgets.add(Widgets.createArrow(current));

        current.x += 36;
        widgets.add(Widgets.createSlot(current)
                .entries(display.getOutputEntries().size() > 0 ? display.getOutputEntries().get(0) : EntryIngredients.of(ItemStack.EMPTY))
                .markOutput());

        current.x = bounds.getCenterX() - 60;
        widgets.add(Widgets.createSlot(current)
                .entries(EntryIngredients.of(new ItemStack(ModItems.ENGINEERS_TRUSTY_HAMMER)))
                .markInput());

        return widgets;
    }
}
