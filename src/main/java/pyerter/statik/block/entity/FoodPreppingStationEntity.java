package pyerter.statik.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.Statik;
import pyerter.statik.item.inventory.ImplementedInventory;
import pyerter.statik.recipe.FoodPreppingStationRecipe;
import pyerter.statik.screen.handlers.FoodPreppingStationScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FoodPreppingStationEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private static final String DISPLAY_NAME = "Food Prepping Station";
    public static final List<Item> acceptedTools = new ArrayList<>();
    public static boolean tryRegisterTool(Item item) {
        if (!acceptedTools.contains(item)) {
            acceptedTools.add(item);
            return true;
        }
        return false;
    }
    public static final List<Item> acceptedIngredients = new ArrayList<>();
    public static boolean tryRegisterIngredient(Item item) {
        if (!acceptedIngredients.contains(item)) {
            acceptedIngredients.add(item);
            return true;
        }
        return false;
    }
    public static final List<Item> acceptedLayerIngredients = new ArrayList<>();
    public static boolean tryRegisterLayerIngredient(Item item) {
        if (!acceptedLayerIngredients.contains(item)) {
            acceptedLayerIngredients.add(item);
            return true;
        }
        return false;
    }
    public static final int FOOD_PREPPING_STATION_INVENTORY_SIZE = 13;
    public static final int SLOT_LEFT_LAYER = 1;
    public static final int SLOT_RIGHT_LAYER = 8;
    public static final int SLOT_TOOL_ACTIVE = 0;
    public static final int SLOT_TOOL_STORED_1 = 9;
    public static final int SLOT_TOOL_STORED_2 = 10;
    public static final int SLOT_TOOL_STORED_3 = 11;
    public static final int SLOT_RESULT = 12;

    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(FOOD_PREPPING_STATION_INVENTORY_SIZE, ItemStack.EMPTY);

    public FoodPreppingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FOOD_PREPPING_STATION, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return MutableText.of(new LiteralTextContent(DISPLAY_NAME));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FoodPreppingStationScreenHandler(syncId, inv, this, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    public Boolean tryCraft(boolean stackCraft) {
        Optional<ItemStack> craftingResult = getCraftingResult();
        if (craftingResult.isEmpty())
            return false;

        Statik.logInfo("Prepping!");
        if (stackCraft) {
            craftResultMax(craftingResult.get());
        } else {
            craftResult(craftingResult.get());
        }
        return true;
    }

    public boolean craftResult(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;

        for (int i = SLOT_LEFT_LAYER; i <= SLOT_RIGHT_LAYER; i++) {
            inventory.get(i).decrement(1);
        }

        if (inventory.get(SLOT_RESULT).isEmpty())
            inventory.set(SLOT_RESULT, stack);
        else if (inventory.get(SLOT_RESULT).isItemEqual(stack))
            inventory.get(SLOT_RESULT).increment(stack.getCount());
        return true;
    }

    public boolean craftResultMax(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;

        int maxStackCount = stack.getMaxCount();
        int openSpace = (maxStackCount - (!inventory.get(SLOT_RESULT).isEmpty() ? inventory.get(SLOT_RESULT).getCount() : 0));
        maxStackCount = openSpace;
        int repeatTimes = maxStackCount / stack.getCount();
        for (int i = SLOT_LEFT_LAYER; i <= SLOT_RIGHT_LAYER; i++) {
            if (!inventory.get(i).isEmpty())
                repeatTimes = Math.min(repeatTimes, inventory.get(i).getCount());
        }
        stack.setCount(stack.getCount() * repeatTimes);

        for (int i = SLOT_LEFT_LAYER; i <= SLOT_RIGHT_LAYER; i++) {
            if (!inventory.get(i).isEmpty())
                inventory.get(i).decrement(repeatTimes);
        }

        if (inventory.get(SLOT_RESULT).isEmpty())
            inventory.set(SLOT_RESULT, stack);
        else if (inventory.get(SLOT_RESULT).isItemEqual(stack))
            inventory.get(SLOT_RESULT).increment(stack.getCount());

        return true;
    }

    public Optional<ItemStack> getCraftingResult() {
        Optional<FoodPreppingStationRecipe> foodPreppingRecipe = hasRecipe(this);
        if (foodPreppingRecipe.isPresent())
            return Optional.of(foodPreppingRecipe.get().getOutput());

        return Optional.empty();
    }

    private static Optional<FoodPreppingStationRecipe> hasRecipe(FoodPreppingStationEntity entity) {
        World world = entity.world;
        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < inventory.size(); i++) { inventory.setStack(i, entity.getStack(i)); }

        Optional<FoodPreppingStationRecipe> match = world.getRecipeManager()
                .getFirstMatch(FoodPreppingStationRecipe.Type.INSTANCE, inventory, world);

        if (match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getOutput())) {
            return match;
        }
        return Optional.empty();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack output) {
        return inventory.getStack(SLOT_RESULT).getItem() == output.getItem() || inventory.getStack(SLOT_RESULT).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory) {
        return inventory.getStack(SLOT_RESULT).getMaxCount() > inventory.getStack(SLOT_RESULT).getCount();
    }

    public static boolean acceptsQuickTransferTool(ItemStack itemStack) {
        return acceptedTools.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransferIngredient(ItemStack itemStack) {
        return acceptedIngredients.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransferLayerIngredient(ItemStack itemStack) {
        return acceptedLayerIngredients.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack, int slot) {
        if (slot == SLOT_LEFT_LAYER || slot == SLOT_RIGHT_LAYER)
            return acceptsQuickTransferLayerIngredient(itemStack);
        if (slot > SLOT_LEFT_LAYER && slot < SLOT_RIGHT_LAYER)
            return acceptsQuickTransferIngredient(itemStack);
        if (slot == SLOT_TOOL_ACTIVE || slot == SLOT_TOOL_STORED_1 || slot == SLOT_TOOL_STORED_2 || slot == SLOT_TOOL_STORED_3)
            return acceptsQuickTransferTool(itemStack);
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, FoodPreppingStationEntity entity) {
        // nada
    }
}
