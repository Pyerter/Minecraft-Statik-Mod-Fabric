package pyerter.statik.item.custom.engineering.augment;

import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;
import pyerter.statik.util.WorldUtil;

public class Augments {

    // built with sharpness 5
    public static Augment[] HONED_EDGE = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("honed_edge"))
            .setLevel(1).setLevelMultiplier(1f).addAttackDamage(3).buildLevels(5), Enchantments.SHARPNESS);

    // built with efficiency 5
    public static Augment[] EXPERT_MINING = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("expert_mining"))
            .setLevel(1).setLevelMultiplier(1f).addMiningSpeedMult(1.5f).buildLevels(5), Enchantments.EFFICIENCY);

    // built with blast protection 4
    public static Augment[] ORE_CROSS_MINING = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("ore_smashing"))
            .setLevel(1).setMinePredicate((ItemStack stack, AbstractEngineeredTool tool, int level, World world, BlockState state, BlockPos pos, LivingEntity miner) -> {
                tryBreakAdjacentBlocks(tool, stack, world, state, pos, miner, level == 3);
                if (level >= 2)
                    tryBreakCornerAdjacentBlocks(tool, stack, world, state, pos, miner, level == 3);
                return true;
            }).buildLevels(3), Enchantments.BLAST_PROTECTION);

    public static void registerAugments() {
        // built with sharpness 5
        HONED_EDGE = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("honed_edge"))
                .setLevel(1).setLevelMultiplier(1f).addAttackDamage(3).buildLevels(5), Enchantments.SHARPNESS);

        // built with efficiency 5
        EXPERT_MINING = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("expert_mining"))
                .setLevel(1).setLevelMultiplier(1f).addMiningSpeedMult(1.5f).buildLevels(5), Enchantments.EFFICIENCY);

        // built with blast protection 4
        ORE_CROSS_MINING = registerFirstLevelAugmentRecipe((new SimpleAugment.Builder("ore_smashing"))
                .setLevel(1).setMinePredicate((ItemStack stack, AbstractEngineeredTool tool, int level, World world, BlockState state, BlockPos pos, LivingEntity miner) -> {
                    tryBreakAdjacentBlocks(tool, stack, world, state, pos, miner, level == 3);
                    if (level >= 2)
                        tryBreakCornerAdjacentBlocks(tool, stack, world, state, pos, miner, level == 3);
                    return true;
                }).buildLevels(3), Enchantments.BLAST_PROTECTION);
    }

    public static Augment[] registerFirstLevelAugmentRecipe(Augment[] augments, Enchantment enchantment) {
        for (Augment aug: augments) {
            if (aug.getLevel() == 1) {
                registerEnchantmentToAugmentRecipe(aug, enchantment);
                break;
            }
        }
        return augments;
    }

    public static Augment registerEnchantmentToAugmentRecipe(Augment augment, Enchantment enchantment) {
        Augment.tryRegisterEnchantmentToAugment(enchantment, enchantment.getMaxLevel(), augment.getAugmentID());
        return augment;
    }

    public static void tryBreakAdjacentBlocks(AbstractEngineeredTool tool, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, boolean mineExtraCorners) {
        for (Direction dir: Direction.values()) {
            BlockPos adjacentPos = new BlockPos(pos.getX() + dir.getOffsetX(), pos.getY() + dir.getOffsetY(), pos.getZ() + dir.getOffsetZ());
            BlockState adjacentBlock = world.getBlockState(adjacentPos);
            // experience dropping block used to be ore block, this might break things
            if (adjacentBlock.getBlock() instanceof ExperienceDroppingBlock && tool.stateInEffectiveBlocks(adjacentBlock)) {
                WorldUtil.mineBlock(world, adjacentPos, true, miner, stack);
            }

            if (mineExtraCorners && dir.getAxis() != Direction.Axis.Y) {
                for (Direction extraDir: Direction.Type.HORIZONTAL) {
                    if (extraDir.getAxis() == dir.getAxis())
                        continue;

                    BlockPos extraAdjacentPos = adjacentPos.offset(extraDir);
                    adjacentBlock = world.getBlockState(extraAdjacentPos);
                    if (adjacentBlock.getBlock() instanceof ExperienceDroppingBlock && tool.stateInEffectiveBlocks(adjacentBlock)) {
                        WorldUtil.mineBlock(world, extraAdjacentPos, true, miner, stack);
                    }
                }
            }
        }
    }

    public static void tryBreakCornerAdjacentBlocks(AbstractEngineeredTool tool, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, boolean mineExtraCorners) {
        for (Direction verticalDir: Direction.Type.VERTICAL) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos targetPos = pos.offset(verticalDir).offset(dir);
                BlockState targetBlock = world.getBlockState(targetPos);
                if (targetBlock.getBlock() instanceof ExperienceDroppingBlock && tool.stateInEffectiveBlocks(targetBlock)) {
                    WorldUtil.mineBlock(world, targetPos, true, miner, stack);
                }
                if (!mineExtraCorners)
                    continue;

                for (Direction extraDir: Direction.Type.HORIZONTAL) {
                    if (dir.getAxis() == extraDir.getAxis())
                        continue;

                    BlockPos extraTargetPos = targetPos.offset(extraDir);
                    targetBlock = world.getBlockState(extraTargetPos);
                    if (targetBlock.getBlock() instanceof ExperienceDroppingBlock && tool.stateInEffectiveBlocks(targetBlock)) {
                        WorldUtil.mineBlock(world, extraTargetPos, true, miner, stack);
                    }
                }
            }
        }
    }

}
