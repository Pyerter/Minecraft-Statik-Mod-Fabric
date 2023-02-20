package pyerter.statik.item.custom;

import net.minecraft.item.SwordItem;
import pyerter.statik.item.ModToolMaterials;

public class StarmetalSword extends SwordItem {
    public StarmetalSword(int attackDamage, float attackSpeed, Settings settings) {
        super(ModToolMaterials.STARMETAL, attackDamage, attackSpeed, settings);
    }
}
