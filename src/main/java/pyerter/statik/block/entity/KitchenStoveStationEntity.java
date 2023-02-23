package pyerter.statik.block.entity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.block.custom.KitchenStoveStationBlock;
import pyerter.statik.item.custom.engineering.AbstractPowerCore;
import pyerter.statik.item.inventory.ImplementedInventory;
import pyerter.statik.recipe.KitchenStoveRecipe;
import pyerter.statik.screen.handlers.KitchenStoveStationScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Kitchen Stove!
public class KitchenStoveStationEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private static final String DISPLAY_NAME = "Kitchen Stove";
    private static final List<Item> acceptedQuickTransfersTopLevel = new ArrayList<>();
    private static final List<Item> acceptedQuickTransfersBottomLevel = new ArrayList<>();
    public static boolean tryRegisterQuickTransfer(Item item, int level) {
        if (level == 0 && !acceptedQuickTransfersBottomLevel.contains(item)) {
            acceptedQuickTransfersBottomLevel.add(item);
            return true;
        } else if (level == 1 && !acceptedQuickTransfersTopLevel.contains(item)) {
            acceptedQuickTransfersTopLevel.add(item);
            return true;
        }
        return false;
    }

    public static final Integer KITCHEN_STOVE_STATION_INVENTORY_SIZE = 9;
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(KITCHEN_STOVE_STATION_INVENTORY_SIZE, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progressBottomLeft = 0;
    private int progressBottomRight = 0;
    private int progressTopLeft = 0;
    private int progressTopRight = 0;
    private int maxProgressBottomLeft = 0;
    private int maxProgressBottomRight = 0;
    private int maxProgressTopLeft = 0;
    private int maxProgressTopRight = 0;
    private int maxProgress = 72;
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    public KitchenStoveStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KITCHEN_STOVE_STATION, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0: return KitchenStoveStationEntity.this.progressBottomLeft;
                    case 1: return KitchenStoveStationEntity.this.progressBottomRight;
                    case 2: return KitchenStoveStationEntity.this.progressTopLeft;
                    case 3: return KitchenStoveStationEntity.this.progressTopRight;
                    case 4: return KitchenStoveStationEntity.this.maxProgress;
                    case 5: return KitchenStoveStationEntity.this.fuelTime;
                    case 6: return KitchenStoveStationEntity.this.maxFuelTime;
                    case 7: return KitchenStoveStationEntity.this.maxProgressBottomLeft;
                    case 8: return KitchenStoveStationEntity.this.maxProgressBottomRight;
                    case 9: return KitchenStoveStationEntity.this.maxProgressTopLeft;
                    case 10: return KitchenStoveStationEntity.this.maxProgressTopRight;
                    default: return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: KitchenStoveStationEntity.this.progressBottomLeft = value; break;
                    case 1: KitchenStoveStationEntity.this.progressBottomRight = value; break;
                    case 2: KitchenStoveStationEntity.this.progressTopLeft = value; break;
                    case 3: KitchenStoveStationEntity.this.progressTopRight = value; break;
                    case 4: KitchenStoveStationEntity.this.maxProgress = value; break;
                    case 5: KitchenStoveStationEntity.this.fuelTime = value; break;
                    case 6: KitchenStoveStationEntity.this.maxFuelTime = value; break;
                    case 7: KitchenStoveStationEntity.this.maxProgressBottomLeft = value; break;
                    case 8: KitchenStoveStationEntity.this.maxProgressBottomRight = value; break;
                    case 9: KitchenStoveStationEntity.this.maxProgressTopLeft = value; break;
                    case 10: KitchenStoveStationEntity.this.maxProgressTopRight = value; break;
                }
            }

            @Override
            public int size() {
                return 11;
            }
        };
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
        return new KitchenStoveStationScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("kitchen_stove_station.progress_bottom_left", progressBottomLeft);
        nbt.putInt("kitchen_stove_station.progress_bottom_right", progressBottomRight);
        nbt.putInt("kitchen_stove_station.progress_top_left", progressTopLeft);
        nbt.putInt("kitchen_stove_station.progress_top_right", progressTopRight);
        nbt.putInt("kitchen_stove_station.fuelTime", fuelTime);
        nbt.putInt("kitchen_stove_station.maxFuelTime", maxFuelTime);
        nbt.putInt("kitchen_stove_station.max_progress_bottom_left", maxProgressBottomLeft);
        nbt.putInt("kitchen_stove_station.max_progress_bottom_right", maxProgressBottomRight);
        nbt.putInt("kitchen_stove_station.max_progress_top_left", maxProgressTopLeft);
        nbt.putInt("kitchen_stove_station.max_progress_top_right", maxProgressTopRight);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        progressBottomLeft = nbt.getInt("kitchen_stove_station.progress_bottom_left");
        progressBottomRight = nbt.getInt("kitchen_stove_station.progress_bottom_right");
        progressTopLeft = nbt.getInt("kitchen_stove_station.progress_top_left");
        progressTopRight = nbt.getInt("kitchen_stove_station.progress_top_right");
        fuelTime = nbt.getInt("kitchen_stove_station.fuelTime");
        maxFuelTime = nbt.getInt("kitchen_stove_station.maxFuelTime");
        nbt.putInt("kitchen_stove_station.max_progress_bottom_left", maxProgressBottomLeft);
        nbt.putInt("kitchen_stove_station.max_progress_bottom_right", maxProgressBottomRight);
        nbt.putInt("kitchen_stove_station.max_progress_top_left", maxProgressTopLeft);
        nbt.putInt("kitchen_stove_station.max_progress_top_right", maxProgressTopRight);
    }

    public static void tick(World world, BlockPos pos, BlockState state, KitchenStoveStationEntity entity) {
        if (isConsumingFuel(entity))
            entity.fuelTime--;

        KitchenStoveRecipe[] recipes = hasRecipe(entity);
        boolean shouldLightStove = false;
        boolean shouldLightOven = false;
        for (int i = 0; i < recipes.length; i++) {
            if (recipes[i] == null) {
                entity.resetProgress(i);
                continue;
            }

            boolean canContinue = isConsumingFuel(entity) || (!isConsumingFuel(entity) && consumeFuel(entity)) || hasAvailablePowerInSlot(entity, recipes[i]);
            if (!canContinue) {
                entity.resetProgress(i);
                continue;
            }

            if (i == 0 || i == 1) {
                shouldLightOven = true;
            } else if (i == 2 || i == 3) {
                shouldLightStove = true;
            }

            entity.propertyDelegate.set(i, entity.propertyDelegate.get(i) + 1);
            if (entity.propertyDelegate.get(i) > entity.propertyDelegate.get(i + 7)) {
                craftItem(entity, recipes[i], i);
                entity.resetProgress(i);
            }
        }
        if (KitchenStoveStationBlock.getOvenLit(entity.getCachedState()) != shouldLightOven) {
            KitchenStoveStationBlock.setOvenLit(entity.getCachedState(), entity.world, entity.pos, shouldLightOven);
        }
        if (KitchenStoveStationBlock.getStoveLit(entity.getCachedState()) != shouldLightStove) {
            KitchenStoveStationBlock.setStoveLit(entity.getCachedState(), entity.world, entity.pos, shouldLightStove);
        }
    }

    private static void consumePowerFuel(KitchenStoveStationEntity entity, Integer cost) {
        if (entity.inventory.get(0).getItem() instanceof AbstractPowerCore) {
            AbstractPowerCore core = (AbstractPowerCore) entity.inventory.get(0).getItem();
            core.addCharge(entity.inventory.get(0), -cost);
        }
    }

    private static boolean consumeFuel(KitchenStoveStationEntity entity) {
        if (acceptsBurnableFuel(entity.inventory.get(0))) {
            entity.fuelTime = FuelRegistry.INSTANCE.get(entity.removeStack(0, 1).getItem());
            entity.maxFuelTime = entity.fuelTime;
            return true;
        }
        return false;
    }

    private static boolean hasAvailablePowerInSlot(KitchenStoveStationEntity entity, KitchenStoveRecipe recipe) {
        if (entity.inventory.get(0).isEmpty())
            return false;

        if(acceptsQuickTransferFuel(entity.inventory.get(0)) && entity.inventory.get(0).getItem() instanceof AbstractPowerCore) {
            AbstractPowerCore core = (AbstractPowerCore) entity.inventory.get(0).getItem();
            return core.getCharge(entity.inventory.get(0)) >= recipe.getEnergyCost();
        }

        return false;
    }

    private static boolean hasAvailableFuel(KitchenStoveStationEntity entity, KitchenStoveRecipe recipe) {
        if (acceptsBurnableFuel(entity.inventory.get(0)))
            return true;

        return hasAvailablePowerInSlot(entity, recipe);
    }

    private static boolean isConsumingFuel(KitchenStoveStationEntity entity) {
        return entity.fuelTime > 0;
    }

    private static void craftItem(KitchenStoveStationEntity entity, KitchenStoveRecipe recipe, int slot) {
        entity.removeStack(slot + 1, 1);

        if (!isConsumingFuel(entity))
            consumePowerFuel(entity, recipe.getEnergyCost());

        entity.setStack(slot + 5, new ItemStack(recipe.getOutput().getItem(),
                entity.getStack(slot + 5).getCount() + recipe.getOutput().getCount()));
        entity.resetProgress(slot);
    }

    private static KitchenStoveRecipe[] hasRecipe(KitchenStoveStationEntity entity) {
        World world = entity.world;
        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < inventory.size(); i++) { inventory.setStack(i, entity.getStack(i)); }

        List<KitchenStoveRecipe> matches = world.getRecipeManager().getAllMatches(KitchenStoveRecipe.Type.INSTANCE, inventory, world);
        KitchenStoveRecipe[] recipes = new KitchenStoveRecipe[]{ null, null, null, null };
        if (matches.size() == 0)
            return recipes;

        for (int i = 0; i < recipes.length; i++) {
            int slotNumb = i + 1;
            Optional<KitchenStoveRecipe> recipeMatch = matches.stream().filter((KitchenStoveRecipe r) -> r.matchesSlot(inventory, world, slotNumb)).findFirst();
            if (recipeMatch.isPresent() &&
                    canInsertAmountIntoOutputSlot(inventory, recipeMatch.get().getOutput().getCount(), slotNumb + 4) &&
                    canInsertItemIntoOutputSlot(inventory, recipeMatch.get().getOutput(), slotNumb + 4) &&
                    (isConsumingFuel(entity) || hasAvailableFuel(entity, recipeMatch.get()))) {
                recipes[i] = recipeMatch.get();
                entity.propertyDelegate.set(i + 7, recipeMatch.get().getCookTime());
            }
        }

        return recipes;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack output, int slot) {
        return inventory.getStack(slot).getItem() == output.getItem() || inventory.getStack(slot).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory, int amount, int slot) {
        return inventory.getStack(slot).getMaxCount() > inventory.getStack(slot).getCount();
    }

    private static boolean hasNotReachedStackLimit(KitchenStoveStationEntity entity, int slot) {
        return entity.getStack(slot).getCount() < entity.getStack(slot).getMaxCount();
    }

    private void resetProgress(int slot) {
        this.propertyDelegate.set(slot, 0);
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack) {
        return acceptedQuickTransfersBottomLevel.contains(itemStack.getItem()) || acceptedQuickTransfersTopLevel.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack, int slot) {
        return slot == 1 || slot == 2 ? acceptedQuickTransfersBottomLevel.contains(itemStack.getItem()) : acceptedQuickTransfersTopLevel.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransferFuel(ItemStack itemStack) {
        return acceptsBurnableFuel(itemStack) || acceptsChargableFuel(itemStack);
    }

    public static boolean acceptsBurnableFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
    }

    public static boolean acceptsChargableFuel(ItemStack stack) {
        return AbstractPowerCore.REGISTERED_CORES.contains(stack.getItem());
    }
}
