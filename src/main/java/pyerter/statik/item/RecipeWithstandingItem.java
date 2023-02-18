package pyerter.statik.item;

import net.minecraft.item.Item;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import pyerter.statik.util.IItemSpecialRecipeRemainder;

import java.util.List;
import java.util.Optional;

public class RecipeWithstandingItem extends DescriptiveItem implements IItemSpecialRecipeRemainder {

    public RecipeWithstandingItem(Settings settings) {
        super(settings);
    }

    public RecipeWithstandingItem(Settings settings, List<Pair<String, Optional<Formatting[]>>> description) {
        super(settings, description);
    }

    @Override
    public Item getSpecialRecipeRemainder() {
        return this;
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }
}
