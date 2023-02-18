package pyerter.statik.screen.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import pyerter.statik.block.entity.CaptureChamberEntity;
import pyerter.statik.block.entity.ChargeableFuelHelper;
import pyerter.statik.screen.slot.ModFuelSlot;

import java.util.List;

public class CaptureChamberScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public CaptureChamberScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(2));
    }

    public CaptureChamberScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.CAPTURE_CHAMBER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 3);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;

        // add slots
        this.addSlot(new ModFuelSlot(inventory, 0, 78, 10, true));
        this.addSlot(new ModFuelSlot(inventory, 1, 78, 32, true));
        this.addSlot(new ModFuelSlot(inventory, 2, 78, 54, true));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    public boolean hasCharge() {
        return propertyDelegate.get(0) > 0;
    }

    public int getCharge() {
        return propertyDelegate.get(0);
    }

    public int getScaledChargeProgress() {
        int chargeProgress = this.propertyDelegate.get(0);
        int maxChargeProgress = CaptureChamberEntity.MAX_STORED_CHARGE;
        int chargeProgressSize = 60;

        return maxChargeProgress != 0 ? (int)(((float)chargeProgress / (float)maxChargeProgress) * chargeProgressSize) : 0;
    }

    // This method handles quick transfers (shift clicks).
    // Used to be transferSlot(PlayerEntity player, int index)
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            // if index corresponds to player inventory
            if (index != 2 && index != 1 && index != 0) {
                if (ChargeableFuelHelper.acceptsQuickTransferFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } if (index >= 3 && index < 30) {
                    if (!this.insertItem(itemStack2, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
                // finally, try to quick transfer from the primary slots of machine to inventory
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, 9 + col + row * 9, 8 + col * 18, 86 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }
}
