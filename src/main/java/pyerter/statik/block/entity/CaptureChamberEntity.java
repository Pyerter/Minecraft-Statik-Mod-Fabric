package pyerter.statik.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
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
import pyerter.statik.screen.handlers.CaptureChamberScreenHandler;
import pyerter.statik.util.Util;

import java.util.List;

public class CaptureChamberEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private static final String DISPLAY_NAME = "Capture Chamber";
    //private static final List<Item> acceptedQuickTransfers = List.of(ModItems.MAKESHIFT_CORE, ModItems.STORM_CORE);
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(3, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int storedCharge;
    private int passiveChargeTicks;
    private int receivingPriority = -1;
    private int directionTransferring = -1;
    private int transferStrength = 0;

    public static final int MAX_STORED_CHARGE = 12100;
    public static final int MAX_CHARGE_TRANSFER_RATE = 50;
    public static final int MAX_CORE_CHARGE_TRANSFER_RATE = MAX_STORED_CHARGE;
    public static final int TICKS_PER_PASSIVE_CHARGE = 20;
    public static final int TICKS_PER_PASSIVE_TRANSFER = 2;
    public static final int DEFAULT_PRIORITY = -1;

    public CaptureChamberEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAPTURE_CHAMBER, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0: return CaptureChamberEntity.this.storedCharge;
                    case 1: return CaptureChamberEntity.this.passiveChargeTicks;
                    default: return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: CaptureChamberEntity.this.storedCharge = value; break;
                    case 1: CaptureChamberEntity.this.passiveChargeTicks = value; break;
                }
            }

            @Override
            public int size() {
                return 2;
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
        return new CaptureChamberScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("capture_chamber.storedCharge", storedCharge);
        nbt.putInt("capture_chamber.passiveChargeTicks", passiveChargeTicks);
        nbt.putInt("capture_chamber.receivingPriority", receivingPriority);
        nbt.putInt("capture_chamber.directionTransferring", directionTransferring);
        nbt.putInt("capture_chamber.transferStrength", transferStrength);

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        storedCharge = nbt.getInt("capture_chamber.storedCharge");
        passiveChargeTicks = nbt.getInt("capture_chamber.passiveChargeTicks");
        receivingPriority = nbt.getInt("capture_chamber.receivingPriority");
        directionTransferring = nbt.getInt("capture_chamber.directionTransferring");
        transferStrength = nbt.getInt("capture_chamber.transferStrength");
    }

    public static void tick(World world, BlockPos pos, BlockState state, CaptureChamberEntity entity) {
        if (world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock() == Blocks.LIGHTNING_ROD)
            entity.passiveChargeTicks++;
        if (entity.passiveChargeTicks % TICKS_PER_PASSIVE_CHARGE == 0) {
            addCharge(entity, entity.passiveChargeTicks / TICKS_PER_PASSIVE_CHARGE);
            entity.passiveChargeTicks = 0;
            if (entity.transferStrength == CaptureChamberProviderEntity.MAX_STRENGTH)
                assertStillHaveStrengthPriority(world, pos, entity);
        }
        if (entity.passiveChargeTicks % TICKS_PER_PASSIVE_TRANSFER == 0)
            //tryProvideTo(world, entity);
            tryProvideDirection(world, entity);

        tryAddCoreCharges(entity);
        entity.markDirty();
    }

    public static boolean tryProvideDirection(World world, CaptureChamberEntity entity) {
        if (entity.directionTransferring == -1)
            return false;

        Direction dir = Util.numbToDirection(entity.directionTransferring);
        BlockPos targetPos = Util.addBlockPos(entity.getPos(), dir);
        BlockEntity targetEntity = world.getBlockEntity(targetPos);
        if (targetEntity == null || !(targetEntity instanceof CaptureChamberEntity)) {
            pulseRemovedStrength(world, targetPos, null, entity.transferStrength + 1);
            entity.resetPriorityAndDirection();
            return false;
        }

        CaptureChamberEntity targetChamber = (CaptureChamberEntity) targetEntity;
        if (targetChamber.receivingPriority == DEFAULT_PRIORITY || targetChamber.transferStrength == 0 || targetChamber.receivingPriority <= entity.receivingPriority) {
            //pulseRemovedStrength(world, targetPos, targetChamber, entity.transferStrength + 1);
            // don't do the above ^, assume that capture chambers will reset their priority if they exist
            entity.resetPriorityAndDirection();
            return false;
        }

        int canTransfer = Math.min(MAX_CHARGE_TRANSFER_RATE, entity.storedCharge);
        int transferred = addCharge(targetChamber, canTransfer);
        if (transferred <= 0) {
            return false;
        } else {
            entity.storedCharge -= transferred;
            return true;
        }
    }

    public static void pulseRemovedStrength(World world, BlockPos removalPoint, @Nullable CaptureChamberEntity chamber, int previousStrength) {
        // Base case: stop if strength should be 0
        if (previousStrength == 0)
            return;

        // Check chamber properties
        if (chamber != null) {
            // Base case: if chamber strength is greater than what should be, don't reset, otherwise do
            if (chamber.transferStrength <= previousStrength) {
                chamber.transferStrength = 0;
                chamber.receivingPriority = 0;
            } else
                return;
        }

        // Base case will be reached in loop if strength is 1
        if (previousStrength == 1)
            return;

        for (Direction dir: Util.HORIZONTAL_DIRECTIONS) {
            BlockPos nextPos = Util.addBlockPos(removalPoint, dir);
            BlockEntity nextEntity = world.getBlockEntity(nextPos);
            if (nextEntity instanceof CaptureChamberEntity) {
                CaptureChamberEntity nextChamber = (CaptureChamberEntity) nextEntity;
                pulseRemovedStrength(world, Util.addBlockPos(removalPoint, dir), nextChamber, previousStrength - 1);
            }
        }
    }

    public static void pulseAddStrength(World world, BlockPos addPoint, Direction from, int strength, int priority, boolean forcePriority) {
        BlockEntity targetEntity = world.getBlockEntity(addPoint);
        if (targetEntity instanceof CaptureChamberEntity) {
            CaptureChamberEntity targetChamber = (CaptureChamberEntity) targetEntity;
            if (!forcePriority && (targetChamber.receivingPriority > priority || (targetChamber.receivingPriority == priority && targetChamber.transferStrength >= strength)))
                return;

            targetChamber.transferStrength = strength;
            targetChamber.receivingPriority = priority;
            if (from.getHorizontal() != -1)
                targetChamber.directionTransferring = from.getHorizontal();

            if (forcePriority && shouldStopTransferring(world, targetChamber)) {
                targetChamber.directionTransferring = -1;
            }

            if (strength > 1) {
                for (Direction dir: Util.HORIZONTAL_DIRECTIONS) {
                    BlockPos nextPos = Util.addBlockPos(addPoint, dir);
                    pulseAddStrength(world, nextPos, Util.mirrorDirection(dir), strength - 1, priority - 1, false);
                }
            }
        }
    }

    /**
     * Returns true if and only if the passed entity is transferring power and it should stop
     * transferring power to that direction.
     * @param world
     * @param entity
     * @return
     */
    public static boolean shouldStopTransferring(World world, CaptureChamberEntity entity) {
        if (entity.directionTransferring == -1)
            return false;

        Direction dir = Util.numbToDirection(entity.directionTransferring);
        BlockPos targetPos = Util.addBlockPos(entity.getPos(), dir);
        BlockEntity targetEntity = world.getBlockEntity(targetPos);
        if (targetEntity == null || !(targetEntity instanceof CaptureChamberEntity)) {
            return true;
        }

        CaptureChamberEntity targetChamber = (CaptureChamberEntity) targetEntity;
        if (targetChamber.receivingPriority == DEFAULT_PRIORITY || targetChamber.transferStrength == 0 || targetChamber.receivingPriority <= entity.receivingPriority) {
            return true;
        }

        return false;
    }

    public static void assertStillHaveStrengthPriority(World world, BlockPos pos, CaptureChamberEntity entity) {
        BlockPos upPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos downPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        if (!((world.getBlockEntity(upPos) instanceof CaptureChamberProviderEntity) || (world.getBlockEntity(downPos) instanceof CaptureChamberProviderEntity))) {
            pulseRemovedStrength(world, pos, entity, entity.transferStrength);
            entity.resetPriorityAndDirection();
        }
    }

    public static int getCharge(CaptureChamberEntity entity) {
        return entity.storedCharge;
    }

    public static float getPercentChargeFull(CaptureChamberEntity entity) {
        return ((float)entity.storedCharge) / ((float)MAX_STORED_CHARGE);
    }

    public static int addCharge(CaptureChamberEntity entity, Integer amount) {
        int oldCharge = entity.storedCharge;
        entity.storedCharge = Util.clamp(entity.storedCharge + amount, 0, CaptureChamberEntity.MAX_STORED_CHARGE);
        return entity.storedCharge - oldCharge;
    }

    private static int addCoreCharge(CaptureChamberEntity entity, Integer amount, int index) {
        ItemStack core = entity.inventory.get(index);
        if (core.isEmpty() || !(core.getItem() instanceof AbstractPowerCore))
            return 0;

        AbstractPowerCore coreItem = (AbstractPowerCore) core.getItem();
        int originalAmount = coreItem.getCharge(core);
        int newAmount = coreItem.addCharge(core, amount);
        return newAmount - originalAmount;
    }

    private static void tryAddCoreCharges(CaptureChamberEntity entity) {
        int i = 0;
        while (entity.storedCharge > 0 && i < 3) {
            if (hasAvailableCoreInSlot(entity,  i)) {
                addCharge(entity, -addCoreCharge(entity, Math.min(entity.storedCharge, MAX_CORE_CHARGE_TRANSFER_RATE), i));
            }
            i++;
        }
    }

    public int getReceivingPriority() {
        return receivingPriority;
    }

    public void setReceivingPriority(int val) {
        receivingPriority = val;
        markDirty();
    }

    public void resetPriority() {
        receivingPriority = DEFAULT_PRIORITY;
        transferStrength = 0;
        markDirty();
    }

    public void resetPriorityAndDirection() {
        resetPriority();
        directionTransferring = -1;
    }

    public int getTransferStrength() { return transferStrength; }
    public void setTransferStrength(int transferStrength) { this.transferStrength = transferStrength; }

    private static boolean hasAvailableCoreInSlot(CaptureChamberEntity entity, int index) {
        if (entity.inventory.get(index).isEmpty())
            return false;

        return true;
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack) {
        return AbstractPowerCore.isCoreFuelItem(itemStack);
    }

    public static boolean acceptsQuickTransferFuel(ItemStack itemStack) {
        return false;
    }
}
