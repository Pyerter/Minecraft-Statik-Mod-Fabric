package pyerter.statik.item.custom;

import net.minecraft.item.ShovelItem;
import pyerter.statik.item.ModToolMaterials;

public class StarmetalShovel extends ShovelItem {
    public StarmetalShovel(float attackDamage, float attackSpeed, Settings settings) {
        super(ModToolMaterials.STARMETAL, attackDamage, attackSpeed, settings);
    }
}

