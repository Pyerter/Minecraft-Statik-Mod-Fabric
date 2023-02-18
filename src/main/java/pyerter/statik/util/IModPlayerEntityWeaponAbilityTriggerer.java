package pyerter.statik.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public interface IModPlayerEntityWeaponAbilityTriggerer {
    public boolean tryUseWeaponAbility(Entity target);
    public default boolean trySwordSweepAttack(Entity target) {
        return trySwordSweepAttack(target, 1);
    }
    public boolean tryHammerSmashAttack(Box boundingBox, float radius);
    public boolean trySwordSweepAttack(Entity target, float radiusMultiplier);
}
