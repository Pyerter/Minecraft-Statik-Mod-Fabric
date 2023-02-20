package pyerter.statik.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import pyerter.statik.util.IItemSpecialRecipeRemainder;

public class BreadKnifeItem extends SwordItem implements IItemSpecialRecipeRemainder {

    public BreadKnifeItem(Settings settings) {
        super(ToolMaterials.IRON, 0, 10, settings);
    }

    @Override
    public Item getSpecialRecipeRemainder() {
        return this;
    }
}
