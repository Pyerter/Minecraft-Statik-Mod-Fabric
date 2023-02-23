package pyerter.statik.screen.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import pyerter.statik.block.entity.TridiBlockEntity;
import pyerter.statik.screen.slot.ModFuelSlot;
import pyerter.statik.screen.slot.ModResultSlot;

import java.util.List;

public class TridiScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public TridiScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(7), new ArrayPropertyDelegate(4));
    }

    public TridiScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.TRIDI_SCREEN_HANDLER, syncId);
        checkSize(inventory, 7);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;

        // add slots
        this.addSlot(new Slot(inventory, 0, 18, 50));
        this.addSlot(new Slot(inventory, 1, 42, 50));
        this.addSlot(new Slot(inventory, 2, 66, 50));
        this.addSlot(new Slot(inventory, 3, 90, 50));
        this.addSlot(new Slot(inventory, 4, 114, 50));
        this.addSlot(new ModFuelSlot(inventory, 5, 66, 22, true));
        this.addSlot(new ModResultSlot(inventory, 6, 138, 22));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public boolean hasFuel() {
        return propertyDelegate.get(2) > 0;
    }

    // bottom arrow: x = [1, 100], y = [169, 177]
    // x = [125, 176], y = [169, 177]
    public List<Integer> getScaledProgress() {
        int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1);
        int progressArrowSize1 = 9;
        int progressArrowSize2 = 52;
        int totalProgressSize = progressArrowSize1 + progressArrowSize2;

        int scaledProgress1 = 0;
        int scaledProgress2 = 0;
        int totalScaledProgress = maxProgress != 0 ? progress * totalProgressSize / maxProgress : 0;
        scaledProgress1 = totalScaledProgress >= progressArrowSize1 ? progressArrowSize1 : totalScaledProgress;
        scaledProgress2 = totalScaledProgress >= progressArrowSize1 ? totalScaledProgress - progressArrowSize1 : 0;
        return List.of(scaledProgress1, scaledProgress2);
    }

    public int getScaledFuelProgress() {
        int fuelProgress = this.propertyDelegate.get(2);
        int maxFuelProgress = this.propertyDelegate.get(3);
        int fuelProgressSize = 14;

        return maxFuelProgress != 0 ? (int)(((float)fuelProgress / (float)maxFuelProgress) * fuelProgressSize) : 0;
    }

    // This method handles quick transfers (shift clicks).
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            // if index corresponds to result slot
            if (index == 6) {
                // try storing in player inventory
                if (!this.insertItem(itemStack2, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(itemStack2, itemStack);
                // if index corresponds to player inventory
            } else if (index != 5 && index != 4 && index != 3 && index != 2 && index != 1 && index != 0) {
                if (TridiBlockEntity.acceptsQuickTransfer(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (TridiBlockEntity.acceptsQuickTransferFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 5, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 7 && index < 34) {
                    if (!this.insertItem(itemStack2, 34, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 34 && index < 43 && !this.insertItem(itemStack2, 7, 34, false)) {
                    return ItemStack.EMPTY;
                }
                // finally, try to quick transfer from the primary slots of machine to inventory
            } else if (!this.insertItem(itemStack2, 7, 43, false)) {
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
