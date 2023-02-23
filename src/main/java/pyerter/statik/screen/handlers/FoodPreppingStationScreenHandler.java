package pyerter.statik.screen.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import pyerter.statik.Statik;
import pyerter.statik.block.entity.FoodPreppingStationEntity;
import pyerter.statik.screen.slot.ModResultSlot;
import pyerter.statik.screen.slot.PickySlot;

import java.util.function.BiPredicate;

public class FoodPreppingStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final FoodPreppingStationEntity station;

    public FoodPreppingStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(FoodPreppingStationEntity.FOOD_PREPPING_STATION_INVENTORY_SIZE), null);
    }

    public FoodPreppingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, FoodPreppingStationEntity station) {
        super(ModScreenHandlers.FOOD_PREPPING_STATION_SCREEN_HANDLER, syncId);
        checkSize(inventory, FoodPreppingStationEntity.FOOD_PREPPING_STATION_INVENTORY_SIZE);
        this.inventory = inventory;
        this.station = station;
        inventory.onOpen(playerInventory.player);

        BiPredicate<ItemStack, Integer> pickyPredicate = FoodPreppingStationEntity::acceptsQuickTransfer;

        // add slots
        this.addSlot(new PickySlot(inventory, 0, 141, 53, pickyPredicate, 0));
        this.addSlot(new PickySlot(inventory, 1, 42, 32, pickyPredicate, 1));
        this.addSlot(new PickySlot(inventory, 2, 63, 11, pickyPredicate, 2));
        this.addSlot(new PickySlot(inventory, 3, 84, 11, pickyPredicate, 3));
        this.addSlot(new PickySlot(inventory, 4, 63, 32, pickyPredicate, 4));
        this.addSlot(new PickySlot(inventory, 5, 84, 32, pickyPredicate, 5));
        this.addSlot(new PickySlot(inventory, 6, 63, 53, pickyPredicate, 6));
        this.addSlot(new PickySlot(inventory, 7, 84, 53, pickyPredicate, 7));
        this.addSlot(new PickySlot(inventory, 8, 105, 32, pickyPredicate, 8));
        this.addSlot(new PickySlot(inventory, 9, 8, 11, pickyPredicate, 9));
        this.addSlot(new PickySlot(inventory, 10, 8, 32, pickyPredicate, 10));
        this.addSlot(new PickySlot(inventory, 11, 8, 53, pickyPredicate, 11));
        this.addSlot(new ModResultSlot(inventory, 12, 141, 32));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    // This method handles quick transfers (shift clicks).
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            // if index corresponds to player inventory
            int invSize = FoodPreppingStationEntity.FOOD_PREPPING_STATION_INVENTORY_SIZE;
            if (index >= invSize) {
                // try inserting into the last slot (hammer) first, then the first 4 slots
                if (this.insertItem(itemStack2, FoodPreppingStationEntity.SLOT_TOOL_ACTIVE, FoodPreppingStationEntity.SLOT_RESULT, false)) {
                    // :)
                } else if (index >= invSize && index < invSize + 27) {
                    if (!this.insertItem(itemStack2, invSize + 27, invSize + 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= invSize + 27 &&
                        index < invSize + 36 &&
                        !this.insertItem(itemStack2, invSize, invSize + 27, false)) {
                    return ItemStack.EMPTY;
                }
                // finally, try to quick transfer from the primary slots of machine to inventory
            } else if (index == FoodPreppingStationEntity.SLOT_TOOL_ACTIVE && this.insertItem(itemStack2, FoodPreppingStationEntity.SLOT_TOOL_STORED_1, FoodPreppingStationEntity.SLOT_TOOL_STORED_3 + 1, false)) {
                // :)
            } else if ((index == FoodPreppingStationEntity.SLOT_TOOL_STORED_1 || index == FoodPreppingStationEntity.SLOT_TOOL_STORED_2 || index == FoodPreppingStationEntity.SLOT_TOOL_STORED_3) &&
                    this.insertItem(itemStack2, FoodPreppingStationEntity.SLOT_TOOL_ACTIVE, FoodPreppingStationEntity.SLOT_TOOL_ACTIVE + 1, false)) {
                // :)
            } else if (!this.insertItem(itemStack2, invSize, invSize + 36, false)) {
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

    public Boolean tryCraft() {
        return tryCraft(false);
    }

    public Boolean tryCraft(boolean stackCraft) {
        Statik.logInfo("Trying prep?");
        if (station != null) {
            return station.tryCraft(stackCraft);
        } else {
            Statik.logInfo("It's null!");
            return false;
        }
    }

    public boolean toolSlotOccupied(int slot) {
        switch (slot) {
            default: case 0: return !inventory.getStack(FoodPreppingStationEntity.SLOT_TOOL_ACTIVE).isEmpty();
            case 1: return !inventory.getStack(FoodPreppingStationEntity.SLOT_TOOL_STORED_1).isEmpty();
            case 2: return !inventory.getStack(FoodPreppingStationEntity.SLOT_TOOL_STORED_2).isEmpty();
            case 3: return !inventory.getStack(FoodPreppingStationEntity.SLOT_TOOL_STORED_3).isEmpty();
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == 0 && tryCraft())
            return true;
        else if (id == 1 && tryCraft(true))
            return true;
        return super.onButtonClick(player, id);
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
