package pyerter.statik.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import pyerter.statik.item.custom.PautschItem;

import java.util.Optional;

public class PautschSlot extends Slot {

    public PautschSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return PautschItem.acceptsQuickTransfer(stack);
    }
}
