package pyerter.statik.screen.slot;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import pyerter.statik.block.entity.ChargeableFuelHelper;

public class ModFuelSlot extends Slot {
    private boolean tridiFuel = false;

    public ModFuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public ModFuelSlot(Inventory inventory, int index, int x, int y, boolean tridiFuel) {
        super(inventory, index, x, y);
        this.tridiFuel = tridiFuel;
    }

    public boolean canInsertTridiFuel(ItemStack stack) {
        return ChargeableFuelHelper.acceptsQuickTransferFuel(stack);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (tridiFuel)
            return canInsertTridiFuel(stack);
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || ModFuelSlot.isBucket(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return ModFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount();
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.isOf(Items.BUCKET);
    }


}
