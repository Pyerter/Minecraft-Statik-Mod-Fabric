package pyerter.statik.item.custom;

import net.minecraft.item.PickaxeItem;
import pyerter.statik.item.ModToolMaterials;

public class StarmetalPickaxe extends PickaxeItem {
    public StarmetalPickaxe(int attackDamage, float attackSpeed, Settings settings) {
        super(ModToolMaterials.STARMETAL, attackDamage, attackSpeed, settings);
    }
}
