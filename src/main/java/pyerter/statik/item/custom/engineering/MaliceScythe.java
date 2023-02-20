package pyerter.statik.item.custom.engineering;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.item.ModToolMaterials;
import pyerter.statik.particle.ModParticles;
import pyerter.statik.util.IModPlayerEntityWeaponAbilityTriggerer;

import java.util.List;

public class MaliceScythe extends AbstractEngineeredTool implements IChargeable {
    public static final int ZAP_CHARGE_REQ = 2;

    public MaliceScythe(float attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, ModToolMaterials.STARMETAL, BlockTags.HOE_MINEABLE, settings);

        toolType = ToolType.SWORD;
    }

    @Override
    public String getChargeNbtID() {
        return "pootsadditions.charge";
    }

    @Override
    public boolean tryUseWeaponAbility(Entity target, PlayerEntity attacker) {
        if (target instanceof LivingEntity) {
            LivingEntity targetEntity = (LivingEntity) target;
            if (((IModPlayerEntityWeaponAbilityTriggerer)attacker).trySwordSweepAttack(target, 2)) {
                ItemStack attackStack = attacker.getMainHandStack();
                if (attackStack.getItem() instanceof MaliceScythe && getCharge(attackStack) >= ZAP_CHARGE_REQ) {
                    float zapDamage = 2.0F + EnchantmentHelper.getSweepingMultiplier(attacker);
                    spawnAttackParticles(targetEntity);
                    targetEntity.damage(DamageSource.player(attacker), zapDamage);
                    addCharge(attackStack, -ZAP_CHARGE_REQ);
                }
                return true;
            }
        }
        return false;
    }

    public void spawnAttackParticles(LivingEntity target) {
        World world = target.world;
        if (world instanceof ServerWorld) {
            double x = target.getX();
            double y = target.getBodyY(0.5f);
            double z = target.getZ();
            double deltaVal = 1;
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.spawnParticles(ModParticles.ELECTRO_STATIC_PARTICLE, x, y, z, 20, deltaVal, deltaVal, deltaVal, 0.5);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return getCharge(stack) > getMaxCharge() / 2;
    }

    @Override
    public int getMaxCharge() {
        return 100;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        addChargeTooltip(stack, tooltip);
    }
}
