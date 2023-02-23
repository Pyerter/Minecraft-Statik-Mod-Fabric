package pyerter.statik.item.custom.engineering;

import net.minecraft.item.ItemStack;
import pyerter.statik.util.IItemWithVariantItemGroupStacks;

import java.util.ArrayList;
import java.util.List;

public class MakeshiftCore extends AbstractPowerCore {
    public static final int MAX_CHARGE = 1210;

    public MakeshiftCore(Settings settings) {
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
