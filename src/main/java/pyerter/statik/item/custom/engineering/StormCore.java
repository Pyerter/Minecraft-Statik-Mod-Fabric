package pyerter.statik.item.custom.engineering;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import pyerter.statik.util.IItemWithVariantItemGroupStacks;

import java.util.ArrayList;
import java.util.List;

public class StormCore extends AbstractPowerCore {
    public static final int MAX_CHARGE = 12100;

    public StormCore(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxCharge() {
        return MAX_CHARGE;
    }

    // This is borked after 1.19.3
    /*
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));
            ItemStack chargedStack = new ItemStack(this);
            tryCharge(chargedStack, getMaxCharge());
            stacks.add(chargedStack);
        }
    }
     */
}
