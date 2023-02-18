package pyerter.statik.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CaptureChamberProviderEntity extends BlockEntity {

    public static int UPDATE_PERIOD = 50;
    public static int MAX_LINKS = 4;
    public static int MAX_STRENGTH = 5;
    public static int MAX_PRIORITY = 5;

    private int priority = 0;
    private int updateTimer = 0;
    private boolean initiallyUpdated = false;

    public CaptureChamberProviderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAPTURE_CHAMBER_PROVIDER, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("capture_chamber_provider.priority", priority);
        nbt.putInt("capture_chamber_provider.update_timer", updateTimer);
        nbt.putBoolean("capture_chamber_provider.initial_update", initiallyUpdated);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        priority = nbt.getInt("capture_chamber_provider.priority");
        updateTimer = nbt.getInt("capture_chamber_provider.update_timer");
        initiallyUpdated = nbt.getBoolean("capture_chamber_provider.initial_update");
    }

    public int getPriority() {
        return priority;
    }

    public int incrementPriority() {
        this.priority++;
        if (priority > MAX_PRIORITY)
            this.priority = 0;
        initiallyUpdated = false;
        this.markDirty();
        return priority;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CaptureChamberProviderEntity entity) {
        entity.updateTimer += 1;
        if (entity.updateTimer >= UPDATE_PERIOD) {
            entity.updateTimer = 0;
            updateChamberPriority(world, pos, state, entity);
        }

        entity.markDirty();
    }

    public static void updateChamberPriority(World world, BlockPos pos, BlockState state, CaptureChamberProviderEntity entity) {
        BlockPos originalPosUp = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos originalPosDown = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        CaptureChamberEntity.pulseAddStrength(world, originalPosUp, Direction.DOWN, MAX_STRENGTH, MAX_STRENGTH * entity.priority, true);
        CaptureChamberEntity.pulseAddStrength(world, originalPosDown, Direction.UP, MAX_STRENGTH, MAX_STRENGTH * entity.priority, true);
    }
}
