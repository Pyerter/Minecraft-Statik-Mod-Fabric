package pyerter.statik.block.entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pyerter.statik.item.custom.engineering.AbstractPowerCore;

import java.util.ArrayList;
import java.util.List;

public class ChargeableFuelHelper {
    private static final List<Item> acceptedQuickTransfers = initalizeAcceptedQuickTransfers();
    private static List<Item> initalizeAcceptedQuickTransfers() {
        List<Item> items = new ArrayList<>();
        return items;
    }
    public static boolean tryRegisterQuickTransfer(Item item) {
        if (!acceptedQuickTransfers.contains(item)) {
            acceptedQuickTransfers.add(item);
            return true;
        }
        return false;
    }

    public static boolean acceptsQuickTransferFuel(ItemStack itemStack) {
        return AbstractPowerCore.isCoreFuelItem(itemStack);
    }
}
