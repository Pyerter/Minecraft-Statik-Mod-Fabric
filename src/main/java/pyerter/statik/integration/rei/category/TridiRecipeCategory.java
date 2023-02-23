package pyerter.statik.integration.rei.category;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
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
import pyerter.statik.integration.rei.display.TridiRecipeDisplay;
import pyerter.statik.item.ModItems;
import pyerter.statik.item.custom.engineering.MakeshiftCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TridiRecipeCategory implements DisplayCategory<TridiRecipeDisplay> {
    public TridiRecipeCategory() {

    }

    @Override
    public CategoryIdentifier<? extends TridiRecipeDisplay> getCategoryIdentifier() {
        return REIStatikPlugin.TRIDI;
    }

    @Override
    public Text getTitle() {
        return Text.of("Tridiminiumobulator");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.TRIDI);
    }

    // NOTE width x height: 150 x 66
    @Override
    public List<Widget> setupDisplay(TridiRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 68, bounds.getCenterY() + 12);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        Point current = startPoint.clone();

        int currentSlot = 0;
        for (int i = 0; i < 5; i++) {
            //if (i < display.getInputEntries().size())
            widgets.add(Widgets.createSlot(current)
                    .entries(display.getInputEntries().size() > i ? display.getInputEntries().get(i) : EntryIngredients.of(ItemStack.EMPTY))
                    .markInput());
            current.x += 24;
        }

        current.x = bounds.getCenterX() - 68 + (24 * 4) + 8;
        current.y = bounds.getCenterY() - 18;
        widgets.add(Widgets.createSlot(current)
                .entries(display.getOutputEntries().size() > 0 ? display.getOutputEntries().get(0) : EntryIngredients.of(ItemStack.EMPTY))
                .markOutput());

        current.x = bounds.getCenterX() - 68 + (24 * 2);
        ItemStack coreStack = new ItemStack(ModItems.MAKESHIFT_CORE);
        MakeshiftCore coreItem = (MakeshiftCore) ModItems.MAKESHIFT_CORE;
        coreItem.tryCharge(coreStack, coreItem.getMaxCharge());
        widgets.add(Widgets.createSlot(current).entries(EntryIngredients.of(coreStack)));

        current.x += 24;
        widgets.add(Widgets.createArrow(current));

        return widgets;
    }
}
