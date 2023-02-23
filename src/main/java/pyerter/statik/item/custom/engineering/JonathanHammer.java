package pyerter.statik.item.custom.engineering;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.item.ModToolMaterials;
import pyerter.statik.particle.ModParticles;
import pyerter.statik.util.IModPlayerEntityWeaponAbilityTriggerer;

import java.util.List;

public class JonathanHammer extends AbstractEngineeredTool implements IChargeable {
    public static final int CHARGE_REQ = 100;
    public static final int MAX_CHARGE = 500;
    public static final List<TagKey<Block>> SHOCKWAVE_BLOCK_TAGS = List.of(BlockTags.PICKAXE_MINEABLE, BlockTags.SHOVEL_MINEABLE);
    public static final float SHOCKWAVE_MAX_HARDNESS = 50f; // must be less than the hardness of obsidian - that's kind of OP

    public JonathanHammer(float attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, ModToolMaterials.STARMETAL, BlockTags.PICKAXE_MINEABLE, settings);

        toolType = ToolType.SWORD;
    }

    @Override
    public String getChargeNbtID() {
        return "statik.charge";
    }

    @Override
    public boolean tryUseWeaponAbility(Entity target, PlayerEntity attacker) {
        if (target instanceof LivingEntity) {
            LivingEntity targetEntity = (LivingEntity) target;
            ((IModPlayerEntityWeaponAbilityTriggerer)attacker).trySwordSweepAttack(target, 0.5f);
        }
        return false;
    }

    public static void spawnAttackParticles(LivingEntity target) {
        BlockPos targetPos = new BlockPos(target.getPos().x, target.getBodyY(0.5f), target.getPos().z);
        spawnAttackParticles(target.world, targetPos, 20);
    }

    public static void spawnAttackParticles(World world, BlockPos target, int numb) {
        if (world instanceof ServerWorld) {
            double x = target.getX();
            double y = target.getY();
            double z = target.getZ();
            double deltaVal = 1;
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticles(ModParticles.ELECTRO_STATIC_PARTICLE, x, y, z, numb, deltaVal, deltaVal, deltaVal, 0.5);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (super.useOnBlock(context) == ActionResult.SUCCESS) {
            if (getCharge(context.getStack()) >= CHARGE_REQ) {
                addCharge(context.getStack(), -CHARGE_REQ);
                causeShockwave(context);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public static void causeShockwave(ItemUsageContext context) {
        Direction shockDirection = context.getPlayerFacing();
        Direction leftDirection = shockDirection.rotateCounterclockwise(Direction.Axis.Y);
        Direction rightDirection = shockDirection.rotateClockwise(Direction.Axis.Y);
        BlockPos startBlock = context.getBlockPos().offset(leftDirection).offset(Direction.DOWN, 1);
        BlockPos endBlock = context.getBlockPos().offset(rightDirection).offset(Direction.UP, 3).offset(shockDirection, 4);

        Box posBoundingBox = new Box(startBlock, endBlock);
        PlayerEntity player = context.getPlayer();
        World world = context.getPlayer().world;
        BlockPos.stream(posBoundingBox).filter(p -> {
            BlockState state = world.getBlockState(p);
            return !state.isAir() && state.getBlock().getHardness() < SHOCKWAVE_MAX_HARDNESS && stateInEffectiveBlocks(state, SHOCKWAVE_BLOCK_TAGS);
        }).forEach(p -> {
            world.breakBlock(p, true, player);
        });

        if (leftDirection == Direction.SOUTH || leftDirection == Direction.EAST)
            startBlock = startBlock.offset(leftDirection);
        if (rightDirection == Direction.SOUTH || rightDirection == Direction.EAST)
            endBlock = endBlock.offset(rightDirection);
        if (shockDirection == Direction.SOUTH || shockDirection == Direction.EAST)
            endBlock = endBlock.offset(shockDirection);
        else
            startBlock = startBlock.offset(shockDirection.getOpposite());
        Box hitBox = new Box(startBlock, endBlock);

        spawnAttackParticles(context.getWorld(), context.getBlockPos(), 40);
        ((IModPlayerEntityWeaponAbilityTriggerer)context.getPlayer()).tryHammerSmashAttack(hitBox, 0.5f);
    }


    @Override
    public boolean hasGlint(ItemStack stack) {
        return getCharge(stack) >= CHARGE_REQ;
    }

    @Override
    public int getMaxCharge() {
        return MAX_CHARGE;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        addChargeTooltip(stack, tooltip);
    }
}
