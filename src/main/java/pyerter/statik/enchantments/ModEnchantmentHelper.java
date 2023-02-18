package pyerter.statik.enchantments;
import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// The code in this class is essentially a copy of the EnchantmentHelper already made, except for one thing
public class ModEnchantmentHelper {

    public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
        Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();

        while(iterator.hasNext()) {
            if (!pickedEntry.enchantment.canCombine(((EnchantmentLevelEntry)iterator.next()).enchantment)) {
                iterator.remove();
            }
        }

    }

    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        int i = item.getEnchantability();
        if (i <= 0) {
            return list;
        } else {
            level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentLevelEntry> list2 = getPossibleEntries(level, stack, treasureAllowed);
            if (!list2.isEmpty()) {
                Optional var10000 = Weighting.getRandom(random, list2);
                Objects.requireNonNull(list);
                var10000.ifPresent(e -> list.add((EnchantmentLevelEntry) e));

                while(random.nextInt(50) <= level) {
                    if (!list.isEmpty()) {
                        removeConflicts(list2, (EnchantmentLevelEntry) Util.getLast(list));
                    }

                    if (list2.isEmpty()) {
                        break;
                    }

                    var10000 = Weighting.getRandom(random, list2);
                    Objects.requireNonNull(list);
                    var10000.ifPresent(e -> list.add((EnchantmentLevelEntry) e));
                    level /= 2;
                }
            }

            return list;
        }
    }

    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        Iterator var6 = Registries.ENCHANTMENT.iterator();

        while(true) {
            while(true) {
                Enchantment enchantment;
                do {
                    do {
                        do {
                            if (!var6.hasNext()) {
                                return list;
                            }

                            enchantment = (Enchantment)var6.next();
                        } while(enchantment.isTreasure() && !treasureAllowed);
                    } while(!enchantment.isAvailableForRandomSelection());
                } while(!enchantment.isAcceptableItem(stack) && !bl);
                // here, instead of using enchantment.type.isAcceptableItem, I used enchantment.isAcceptableItem
                // this makes it so the other mixin can be used to calculate if the enchantment is compatible
                // as a modded or vanilla enchantment target, and determine acceptability

                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                        list.add(new EnchantmentLevelEntry(enchantment, i));
                        break;
                    }
                }
            }
        }
    }

}