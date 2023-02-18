package pyerter.statik.enchantments;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;

import java.util.Optional;

public enum ModEnchantmentTarget {

    /*ARMOR {
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem;
        }
    },
    ARMOR_FEET {
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
        }
    },
    ARMOR_LEGS {
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
        }
    },
    ARMOR_CHEST {
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
        }
    },
    ARMOR_HEAD {
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
        }
    },*/
    WEAPON {
        public boolean isAcceptableItem(Item item) {
            return (item instanceof AbstractEngineeredTool && AbstractEngineeredTool.toolIsWeapon((AbstractEngineeredTool) item)) ||
                    EnchantmentTarget.WEAPON.isAcceptableItem(item);
        }
    },
    DIGGER {
        public boolean isAcceptableItem(Item item) {
            return (item instanceof AbstractEngineeredTool && AbstractEngineeredTool.toolIsDigger((AbstractEngineeredTool) item)) ||
                    EnchantmentTarget.DIGGER.isAcceptableItem(item);
        }
    },
    /*
    FISHING_ROD {
        public boolean isAcceptableItem(Item item) {
            return item instanceof FishingRodItem;
        }
    },
    TRIDENT {
        public boolean isAcceptableItem(Item item) {
            return item instanceof TridentItem;
        }
    },
    BREAKABLE {
        public boolean isAcceptableItem(Item item) {
            return item.isDamageable();
        }
    },
    BOW {
        public boolean isAcceptableItem(Item item) {
            return item instanceof BowItem;
        }
    }*/
    WEARABLE {
        public boolean isAcceptableItem(Item item) {
            return EnchantmentTarget.WEARABLE.isAcceptableItem(item);
        }
    };
    /*
    CROSSBOW {
        public boolean isAcceptableItem(Item item) {
            return item instanceof CrossbowItem;
        }
    },
    VANISHABLE {
        public boolean isAcceptableItem(Item item) {
            return item instanceof Vanishable || Block.getBlockFromItem(item) instanceof Vanishable || BREAKABLE.isAcceptableItem(item);
        }
    };*/

    ModEnchantmentTarget() {
    }

    public abstract boolean isAcceptableItem(Item item);

    public static Optional<ModEnchantmentTarget> vanillaToModTarget(EnchantmentTarget target) {
        for (ModEnchantmentTarget enchant: values()) {
            if (target.name().equals(enchant.name())) {
                return Optional.of(enchant);
            }
        }
        return Optional.empty();
    }

    public static boolean calculateAcceptabilityFromVanilla(Item item, EnchantmentTarget target) {
        Optional<ModEnchantmentTarget> modEnchant = vanillaToModTarget(target);
        return modEnchant.isPresent() ? modEnchant.get().isAcceptableItem(item) : target.isAcceptableItem(item);
    }

    public static boolean stackIsCompatibleWithModEnchantment(ItemStack stack) {
        for (Class enchantClass: modEnchantClasses) {
            if (enchantClass.isInstance(stack.getItem()))
                return true;
        }
        return false;
    }

    public static final Class[] modEnchantClasses = new Class[]{ AbstractEngineeredTool.class };

}