package pyerter.statik.item.custom.engineering;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import pyerter.statik.tag.ModBlockTags;
import pyerter.statik.util.IModPlayerEntityWeaponAbilityTriggerer;

public class EngineeredSword extends AbstractEngineeredTool {
    public EngineeredSword(float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, ModBlockTags.SWORD_MINEABLE, settings);

        toolType = ToolType.SWORD;
        super.registerTool(this, material, ToolType.SWORD);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    @Override
    public boolean tryUseWeaponAbility(Entity target, PlayerEntity attacker) {
        return ((IModPlayerEntityWeaponAbilityTriggerer)attacker).trySwordSweepAttack(target);
    }
}
