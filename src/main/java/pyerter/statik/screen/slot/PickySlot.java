package pyerter.statik.screen.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PickySlot extends Slot {
    public final BiPredicate<ItemStack, Integer> acceptanceCriteria;
    public final Integer pickyIdentifier;
    public Supplier<Pair<Identifier, Identifier>> backgroundSpriteSupplier = null;

    public PickySlot(Inventory inventory, int index, int x, int y) {
        this(inventory, index, x, y, (item, i) -> true, -1);
    }

    public PickySlot(Inventory inventory, int index, int x, int y, BiPredicate<ItemStack, Integer> acceptanceCriteria) {
        this(inventory, index, x, y, acceptanceCriteria, -1);
    }

    public PickySlot(Inventory inventory, int index, int x, int y, BiPredicate<ItemStack, Integer> acceptanceCriteria, Integer pickyIdentifier) {
        super(inventory, index, x, y);
        this.acceptanceCriteria = acceptanceCriteria;
        this.pickyIdentifier = pickyIdentifier;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return acceptanceCriteria.test(stack, pickyIdentifier);
    }
}
