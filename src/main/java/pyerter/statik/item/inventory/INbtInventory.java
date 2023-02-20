package pyerter.statik.item.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface INbtInventory {

    public DefaultedList<ItemStack> getItems(ItemStack stack);
    public boolean validateInventory(ItemStack stack);
}
