package pyerter.statik.block.entity;

import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.item.custom.engineering.AbstractPowerCore;
import pyerter.statik.item.inventory.ImplementedInventory;
import pyerter.statik.recipe.TridiRecipe;
import pyerter.statik.screen.handlers.TridiScreenHandler;
import pyerter.statik.util.InventoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Tridiminiumobulator!
public class TridiBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private static final String DISPLAY_NAME = "Tridiminiumobulator";
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
    //private static final List<Item> acceptedQuickTransfersFuel = List.of(ModItems.MAKESHIFT_CORE, ModItems.STORM_CORE);
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(7, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private static final int DEFAULT_MAX_PROGRESS = 72;
    private int maxProgress = DEFAULT_MAX_PROGRESS;
    private int fuelTime = 0;
    private int maxFuelTime = 0;
    private int transferCooldown = 0;
    private static final int MAX_TRANSFER_COOLDOWN = 2;

    public TridiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIDI, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0: return TridiBlockEntity.this.progress;
                    case 1: return TridiBlockEntity.this.maxProgress;
                    case 2: return TridiBlockEntity.this.fuelTime;
                    case 3: return TridiBlockEntity.this.maxFuelTime;
                    default: return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: TridiBlockEntity.this.progress = value; break;
                    case 1: TridiBlockEntity.this.maxProgress = value; break;
                    case 2: TridiBlockEntity.this.fuelTime = value; break;
                    case 3: TridiBlockEntity.this.maxFuelTime = value; break;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        if (slot == 5) {
            world.updateComparators(pos, getCachedState().getBlock());
            transferCooldown = MAX_TRANSFER_COOLDOWN;
        }
    }

    @Override
    public Text getDisplayName() {
        return MutableText.of(new LiteralTextContent(DISPLAY_NAME));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new TridiScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("tridi.progress", progress);
        nbt.putInt("tridi.fuelTime", fuelTime);
        nbt.putInt("tridi.maxFuelTime", maxFuelTime);
        nbt.putInt("tridi.transferCooldown", transferCooldown);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        progress = nbt.getInt("tridi.progress");
        fuelTime = nbt.getInt("tridi.fuelTime");
        maxFuelTime = nbt.getInt("tridi.maxFuelTime");
        transferCooldown = nbt.getInt("tridi.transferCooldown");
    }

    public static void tick(World world, BlockPos pos, BlockState state, TridiBlockEntity entity) {
        entity.transferCooldown = --entity.transferCooldown < 0 ? 0 : entity.transferCooldown;

        Optional<TridiRecipe> recipe = hasRecipe(entity);
        if (recipe.isPresent()) {
            entity.maxProgress = recipe.get().getCookTime();
            entity.progress++;
            if (entity.progress > entity.maxProgress) {
                craftItem(entity, recipe.get());
            }
        } else {
            entity.maxProgress = DEFAULT_MAX_PROGRESS;
            entity.resetProgress();
        }
    }

    private static void consumeFuel(TridiBlockEntity entity, Integer cost) {
        if (entity.inventory.get(5).getItem() instanceof AbstractPowerCore) {
            AbstractPowerCore core = (AbstractPowerCore) entity.inventory.get(5).getItem();
            core.addCharge(entity.inventory.get(5), -cost);
        }
    }

    private static boolean hasAvailablePowerInSlot(TridiBlockEntity entity, TridiRecipe recipe) {
        if (entity.inventory.get(5).isEmpty())
            return false;

        if(acceptsQuickTransferFuel(entity.inventory.get(5)) && entity.inventory.get(5).getItem() instanceof AbstractPowerCore) {
            AbstractPowerCore core = (AbstractPowerCore) entity.inventory.get(5).getItem();
            return core.getCharge(entity.inventory.get(5)) >= recipe.getEnergyCost();
        }

        return false;
    }



    private static boolean isConsumingFuel(TridiBlockEntity entity) {
        return entity.fuelTime > 0;
    }

    private static void craftItem(TridiBlockEntity entity, TridiRecipe recipe) {
        boolean[] usedStacks = InventoryUtil.filterContainerItems(entity.inventory, new boolean[]{ true, true, true, true, true });
        if (usedStacks[0])
            entity.removeStack(0, 1);
        if (usedStacks[1])
            entity.removeStack(1,1);
        if (usedStacks[2])
            entity.removeStack(2,1);
        if (usedStacks[3])
            entity.removeStack(3, 1);
        if (usedStacks[4])
            entity.removeStack(4, 1);

        consumeFuel(entity, recipe.getEnergyCost());

        if (entity.getStack(6).getCount() == 0)
            entity.setStack(6, recipe.getOutput());
        else
            entity.setStack(6, new ItemStack(recipe.getOutput().getItem(),
                    entity.getStack(6).getCount() + recipe.getOutput().getCount()));

        entity.resetProgress();

        entity.world.updateComparators(entity.pos, entity.getCachedState().getBlock());
    }

    private static Optional<TridiRecipe> hasRecipe(TridiBlockEntity entity) {
        World world = entity.world;
        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < inventory.size(); i++) { inventory.setStack(i, entity.getStack(i)); }

        Optional<TridiRecipe> match = world.getRecipeManager()
                .getFirstMatch(TridiRecipe.Type.INSTANCE, inventory, world);

        if (match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getOutput())
                && hasAvailablePowerInSlot(entity, match.get())) {
            return match;
        }
        return Optional.empty();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack output) {
        return inventory.getStack(6).getItem() == output.getItem() || inventory.getStack(6).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory) {
        return inventory.getStack(6).getMaxCount() > inventory.getStack(6).getCount();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static boolean checkSlotsForItem(TridiBlockEntity entity, Item item) {
        for (int i = 0; i < 5; i++) {
            if (entity.getStack(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkSlotsExclusiveToList(TridiBlockEntity entity, List<Item> items) {
        for (int i = 0; i < 5; i++) {
            ItemStack itemStack = entity.getStack(i);
            // if slot is not empty and filled with something outside of the provided list
            if (itemStack != ItemStack.EMPTY && !items.contains(itemStack.getItem())) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasNotReachedStackLimit(TridiBlockEntity entity) {
        return entity.getStack(6).getCount() < entity.getStack(6).getMaxCount();
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack) {
        return acceptedQuickTransfers.contains(itemStack.getItem());
    }

    public static boolean acceptsQuickTransferFuel(ItemStack itemStack) {
        return AbstractPowerCore.isCoreFuelItem(itemStack);
    }

    // only allow extraction from bottom and top sides
    // only allow extraction from the fuel and output slots
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        if (!(side == Direction.DOWN || side == Direction.UP))
            return false;

        return (slot == 5 || slot == 6) && transferCooldown <= 0;

        /*
        if (slot == 5 && !stack.isEmpty()) {
            return true;
            if (stack.getItem() instanceof AbstractPowerCore) {
                AbstractPowerCore core = (AbstractPowerCore) stack.getItem();
                return core.getCharge(stack) < 100;
            }
            return false;
        }

        if (slot == 6 && !stack.isEmpty()) {
            return true;
        }

        return false;*/
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN || side == Direction.UP ? new int[]{6, 5} : new int[0];
    }
}
