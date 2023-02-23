package pyerter.statik.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.block.entity.KitchenStoveStationEntity;
import pyerter.statik.block.entity.ModBlockEntities;

public class KitchenStoveStationBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OVEN_LIT = BooleanProperty.of("ovenlit");
    public static final BooleanProperty STOVE_LIT = BooleanProperty.of("stovelit");

    public KitchenStoveStationBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(OVEN_LIT, false).with(STOVE_LIT, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OVEN_LIT);
        builder.add(STOVE_LIT);
        builder.add(FACING);
    }

    public static boolean getOvenLit(BlockState state) {
        return state.get(OVEN_LIT);
    }

    public static void setOvenLit(BlockState state, World world, BlockPos pos, boolean val) {
        if (!world.isClient()) {
            world.setBlockState(pos, state.with(OVEN_LIT, val), Block.NOTIFY_ALL);
        }
    }

    public static boolean getStoveLit(BlockState state) {
        return state.get(STOVE_LIT);
    }

    public static void setStoveLit(BlockState state, World world, BlockPos pos, boolean val) {
        if (!world.isClient()) {
            world.setBlockState(pos, state.with(STOVE_LIT, val), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof KitchenStoveStationEntity) {
                ItemScatterer.spawn(world, pos, (KitchenStoveStationEntity)blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

    /* Block Entity Stuff */

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KitchenStoveStationEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.KITCHEN_STOVE_STATION, KitchenStoveStationEntity::tick);
    }
}
