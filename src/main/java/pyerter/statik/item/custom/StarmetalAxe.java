package pyerter.statik.item.custom;

import net.minecraft.item.AxeItem;
import pyerter.statik.item.ModToolMaterials;

public class StarmetalAxe extends AxeItem {
    public StarmetalAxe(float attackDamage, float attackSpeed, Settings settings) {
        super(ModToolMaterials.STARMETAL, attackDamage, attackSpeed, settings);
    }
}
