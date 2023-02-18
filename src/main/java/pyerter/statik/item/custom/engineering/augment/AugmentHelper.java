package pyerter.statik.item.custom.engineering.augment;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;

import java.util.ArrayList;
import java.util.List;

public class AugmentHelper {

    public static List<Class> augmentableClasses = initializeAugmentableClasses();
    private static List<Class> initializeAugmentableClasses() {
        List<Class> list = new ArrayList<>();
        list.add(AbstractEngineeredTool.class);
        list.add(AugmentedTabletItem.class);
        return list;
    }
    public static boolean tryRegisterAugmentableClass(Class cls) {
        if (!augmentableClasses.contains(cls)) {
            augmentableClasses.add(cls);
            return true;
        }
        return false;
    }

    public static List<Augment> getAugments(ItemStack stack) {
        NbtList nbtList = Augment.getAugmentsNbtList(stack);

        List<Augment> augments = new ArrayList<>(nbtList.size());
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);
            Augment aug = Augment.fromNbt(nbt);
            if (aug != null && !augments.contains(aug))
                augments.add(aug);
        }
        return augments;
    }

    public static List<Augment> getAugments(ItemStack stack, int mask) {
        NbtList nbtList = Augment.getAugmentsNbtList(stack);

        List<Augment> augments = new ArrayList<>(nbtList.size());
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);
            if ((nbt.getInt(Augment.AUGMENT_NBT_MASK_INDICATOR) & mask) == 0)
                continue;

            Augment aug = Augment.fromNbt(nbt);
            if (aug != null && !augments.contains(aug))
                augments.add(aug);
        }
        return augments;
    }

    public static boolean hasAugments(ItemStack stack) {
        NbtList nbtList = Augment.getAugmentsNbtList(stack);

        return nbtList.size() > 0;
    }

    public static int hasAugment(ItemStack stack, String rawId) {
        NbtList nbtList = Augment.getAugmentsNbtList(stack);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);
            Augment aug = Augment.fromNbt(nbt);
            if (aug.getAugmentIDWithoutLevel().equals(rawId))
                return aug.getLevel();
        }
        return -1;
    }

    public static Augment getPresentAugment(ItemStack stack, String rawId) {
        NbtList nbtList = Augment.getAugmentsNbtList(stack);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);
            Augment aug = Augment.fromNbt(nbt);
            if (aug.getAugmentIDWithoutLevel().equals(rawId))
                return aug;
        }
        return null;
    }

    public static float getAugmentDamage(ItemStack stack) {
        List<Augment> augments = getAugments(stack, Augment.MASK_GET_ATTACK_DAMAGE);
        float totalDamage = 0;
        for (Augment aug: augments) {
            totalDamage += aug.getAugmentAttackDamage();
        }
        return totalDamage;
    }

    public static float getAugmentDamage(List<Augment> augments) {
        float attackDamage = 0;
        for (Augment aug: augments) {
            attackDamage += aug.getAugmentAttackDamage();
        }
        return attackDamage;
    }

    public static float getAugmentAttackSpeed(List<Augment> augments) {
        float attackSpeed = 0;
        for (Augment aug: augments) {
            attackSpeed += aug.getAugmentAttackSpeed();
        }
        return attackSpeed;
    }

    public static Multimap<EntityAttribute, EntityAttributeModifier> constructAugmentedAttributes(List<Augment> attributeAugments, float baseAttackDamage, float baseAttackSpeed, float baseMiningSpeed) {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

        float attackDamage = baseAttackDamage + getAugmentDamage(attributeAugments);
        float attackSpeed = baseAttackSpeed + getAugmentAttackSpeed(attributeAugments);

        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(AbstractEngineeredTool.getAttackDamageID(), "Tool modifier", (double)attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(AbstractEngineeredTool.getAttackSpeedID(), "Tool modifier", (double)attackSpeed, EntityAttributeModifier.Operation.ADDITION));

        return builder.build();
    }

    public static boolean applyAugment(Augment aug, ItemStack stack) {
        if (!aug.acceptsItem(stack))
            return false;

        return aug.applyAugment(stack);
    }

    public static Augment getResultingAugmentModification(ItemStack stack, Augment aug) {
        if (!aug.acceptsItem(stack))
            return null;

        Augment result = getPresentAugment(stack, aug.getAugmentIDWithoutLevel());
        if (result == null || result.getLevel() < aug.getLevel())
            return aug;

        String nextLevel = aug.getAugmentIDWithoutLevel() + "_" + (aug.getLevel() + 1);
        if (result.getLevel() == aug.getLevel() && Augment.stringToAugment.containsKey(nextLevel))
            return Augment.stringToAugment.get(nextLevel);

        return null;
    }

}
