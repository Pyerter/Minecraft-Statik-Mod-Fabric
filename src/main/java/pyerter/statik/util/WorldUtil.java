package pyerter.statik.util;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class WorldUtil {

    public static boolean mineBlock(World world, BlockPos pos, boolean drop, @Nullable Entity breakingEntity, @Nullable ItemStack stack) {
        return mineBlock(world, pos, drop, breakingEntity, stack, 512);
    }

    public static boolean mineBlock(World world, BlockPos pos, boolean drop, @Nullable Entity breakingEntity, @Nullable ItemStack stack, int maxUpdateDepth) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        } else {
            FluidState fluidState = world.getFluidState(pos);
            if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
                world.syncWorldEvent(2001, pos, Block.getRawIdFromState(blockState));
            }

            if (drop) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
                Block.dropStacks(blockState, world, pos, blockEntity, breakingEntity, stack == null ? ItemStack.EMPTY : stack);
            }

            boolean bl = world.setBlockState(pos, fluidState.getBlockState(), 3, maxUpdateDepth);
            if (bl) {
                world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(breakingEntity, blockState));
            }
            return bl;
        }
    }

}
