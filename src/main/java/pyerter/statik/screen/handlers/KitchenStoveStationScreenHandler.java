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
import pyerter.statik.block.entity.KitchenStoveStationEntity;
import pyerter.statik.screen.slot.ModResultSlot;
import pyerter.statik.screen.slot.PickySlot;

import java.util.List;
import java.util.function.BiPredicate;

public class KitchenStoveStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public KitchenStoveStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(KitchenStoveStationEntity.KITCHEN_STOVE_STATION_INVENTORY_SIZE), new ArrayPropertyDelegate(11));
    }

    public KitchenStoveStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.KITCHEN_STOVE_STATION_SCREEN_HANDLER, syncId);
        checkSize(inventory, KitchenStoveStationEntity.KITCHEN_STOVE_STATION_INVENTORY_SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;

        // add slots
        BiPredicate<ItemStack, Integer> acceptanceInput = (stack, slot) -> KitchenStoveStationEntity.acceptsQuickTransfer(stack, slot);
        BiPredicate<ItemStack, Integer> acceptanceFuel = (stack, slot) -> KitchenStoveStationEntity.acceptsQuickTransferFuel(stack);
        this.addSlot(new PickySlot(inventory, 0, 80, 29, acceptanceFuel));
        this.addSlot(new PickySlot(inventory, 1, 68, 50, acceptanceInput, 1));
        this.addSlot(new PickySlot(inventory, 2, 92, 50, acceptanceInput, 2));
        this.addSlot(new PickySlot(inventory, 3, 68, 8, acceptanceInput, 3));
        this.addSlot(new PickySlot(inventory, 4, 92, 8, acceptanceInput, 4));
        this.addSlot(new ModResultSlot(inventory, 5, 22, 50));
        this.addSlot(new ModResultSlot(inventory, 6, 138, 50));
        this.addSlot(new ModResultSlot(inventory, 7, 22, 8));
        this.addSlot(new ModResultSlot(inventory, 8, 138, 8));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(propertyDelegate);
    }

    public boolean isCrafting() {
        for (int i = 0; i < 4; i++)
            if (propertyDelegate.get(i) > 0)
                return true;
        return false;
    }

    public boolean isCrafting(int slot) {
        return propertyDelegate.get(slot) > 0;
    }

    public boolean hasFuel() {
        return propertyDelegate.get(5) > 0;
    }

    // bottom arrow: x = [1, 100], y = [169, 177]
    // x = [125, 176], y = [169, 177]
    public Integer getScaledProgress(int slot) {
        int progress = this.propertyDelegate.get(slot);
        int maxProgress = this.propertyDelegate.get(slot + 7);
        int progressArrowSize = 26;

        int totalScaledProgress = maxProgress != 0 ? progress * progressArrowSize / maxProgress : 0;
        return totalScaledProgress;
    }

    public int getScaledFuelProgress() {
        int fuelProgress = this.propertyDelegate.get(5);
        int maxFuelProgress = this.propertyDelegate.get(6);
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
            } else if (index >= 9) {
                if (KitchenStoveStationEntity.acceptsQuickTransfer(itemStack2)) {
                    if (!this.insertItem(itemStack2, 1, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (KitchenStoveStationEntity.acceptsQuickTransferFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 9 && index < 36) {
                    if (!this.insertItem(itemStack2, 36, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 36 && index < 45 && !this.insertItem(itemStack2, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
                // finally, try to quick transfer from the primary slots of machine to inventory
            } else if (!this.insertItem(itemStack2, 9, 45, false)) {
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
