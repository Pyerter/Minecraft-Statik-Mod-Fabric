package pyerter.statik.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

public interface StackDependentAttributeModifierItem {
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack);
    public boolean getNeedsAttributeRefresh(EquipmentSlot slot, ItemStack stack);
    public void setToRefreshAttributes(EquipmentSlot slot, ItemStack stack);
}
