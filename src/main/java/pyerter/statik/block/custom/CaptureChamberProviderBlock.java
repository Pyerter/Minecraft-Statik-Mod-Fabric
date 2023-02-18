package pyerter.statik.block.custom;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.block.entity.CaptureChamberEntity;
import pyerter.statik.block.entity.CaptureChamberProviderEntity;
import pyerter.statik.block.entity.ModBlockEntities;

public class CaptureChamberProviderBlock extends BlockWithEntity implements BlockEntityProvider {

    public CaptureChamberProviderBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CaptureChamberProviderEntity(pos, state);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        BlockPos upPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos downPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());

        BlockEntity upEntity = world.getBlockEntity(upPos);
        if (upEntity instanceof CaptureChamberEntity) {
            CaptureChamberEntity entity = (CaptureChamberEntity) upEntity;
            entity.resetPriority();
        }

        BlockEntity downEntity = world.getBlockEntity(downPos);
        if (downEntity instanceof CaptureChamberEntity) {
            CaptureChamberEntity entity = (CaptureChamberEntity) downEntity;
            entity.resetPriority();
        }

        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        BlockEntity entity = world.getBlockEntity(pos);
        if (hand == Hand.MAIN_HAND && entity instanceof CaptureChamberProviderEntity) {
            CaptureChamberProviderEntity chamberProvider = (CaptureChamberProviderEntity) entity;
            if (player.isSneaking()) {
                if (!world.isClient) {
                    player.sendMessage(Text.of("New priority: " + chamberProvider.incrementPriority()));
                } else
                    chamberProvider.incrementPriority();
            } else if (!world.isClient) {
                player.sendMessage(Text.of("Current priority: " + chamberProvider.getPriority() + ", Crouch-click to change."));
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CAPTURE_CHAMBER_PROVIDER, CaptureChamberProviderEntity::tick);
    }
}
