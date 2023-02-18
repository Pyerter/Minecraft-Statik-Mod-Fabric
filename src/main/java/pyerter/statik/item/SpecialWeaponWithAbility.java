package pyerter.statik.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface SpecialWeaponWithAbility {

    public boolean tryUseWeaponAbility(Entity target, PlayerEntity attacker);

}
