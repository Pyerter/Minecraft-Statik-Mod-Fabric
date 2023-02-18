package pyerter.statik.item.custom.engineering.augment;

import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pyerter.statik.Statik;
import pyerter.statik.item.ModItems;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;

import java.util.*;
import java.util.stream.Collectors;

// :)
public abstract class Augment {
    public static final String AUGMENT_NBT_ID = "pootsadditions.augments";
    public static final String AUGMENT_NBT_INDICATOR = "pootsadditions.augment_id";
    public static final String AUGMENT_NBT_MASK_INDICATOR = "pootsadditions.augment_mask";
    public static final Map<String, Augment> stringToAugment = new HashMap<>();
    public static Collection<Augment> getAllAugments() {
        return stringToAugment.values().stream().sorted(Comparator.comparing(Augment::getAugmentID)).collect(Collectors.toList());
    }

    public static final Map<Pair<Enchantment, Integer>, String> enchantmentToAugmentId = new HashMap<>();
    public static final List<Pair<Enchantment, Integer>> registeredEnchantmentLevels = new ArrayList<>();
    public static boolean tryRegisterEnchantmentToAugment(Enchantment enchantment, Integer level, String augmentId) {
        Optional<Pair<Enchantment, Integer>> existingPair = registeredEnchantmentLevels.stream()
                .filter(p -> p.getLeft() == enchantment && p.getRight() == level).findFirst();
        if (!existingPair.isPresent()) {
            Statik.logInfo("Registering augment " + augmentId + " from enchantment " + enchantment.getName(level));
            Pair<Enchantment, Integer> enchantEntry = new Pair<>(enchantment, level);
            registeredEnchantmentLevels.add(enchantEntry);
            enchantmentToAugmentId.put(enchantEntry, augmentId);
            return true;
        }
        return false;
    }
    public static Optional<Augment> getAugmentFromEnchantment(Enchantment enchantment, Integer level) {
        Optional<Pair<Enchantment, Integer>> existingPair = registeredEnchantmentLevels.stream()
                .filter(p -> p.getLeft() == enchantment && p.getRight() == level).findFirst();
        if (!existingPair.isPresent())
            return Optional.empty();

        Statik.logInfo("Found existing enchantment to augment entry");
        return Optional.of(stringToAugment.get(enchantmentToAugmentId.get(existingPair.get())));
    }

    public static final int MASK_NADA =                 0;
    public static final int MASK_GET_ATTACK_DAMAGE =    1;
    public static final int MASK_GET_MINING_SPEED =     1 << 1;
    public static final int MASK_GET_ATTACK_SPEED =     1 << 2;
    public static final int MASK_USE_ON_BLOCK =         1 << 3;
    public static final int MASK_POST_HIT =             1 << 4;
    public static final int MASK_POST_MINE =            1 << 5;
    public static final int MASK_USE_WEAPON_ABILITY =   1 << 6;
    public static final int MASK_USE_INVENTORY_TICK =   1 << 7;
    public static final int MASK_ITEM_NOT_SUITABLE =    1 << 8;
    public static final int MASK_ITEM_NOW_SUITABLE =    1 << 9;
    public static final int MASK_EFFECTIVE_BLOCKS =     1 << 10;

    public static final int MASK_ATTRIBUTE_PRESENT = MASK_GET_ATTACK_DAMAGE + MASK_GET_MINING_SPEED + MASK_GET_ATTACK_SPEED;

    public static boolean registerAugment(Augment aug, String augmentId) {
        if (!stringToAugment.containsKey(augmentId)) {
            stringToAugment.put(augmentId, aug);
            return true;
        }
        return false;
    }

    public Augment(String augmentId) {
        registerAugment(this, augmentId);
    }

    /** Used to map the NbtCompound of this augment to the Augment Item **/
    public abstract String getAugmentID();
    public String getAugmentIDWithoutLevel() {
        String augmentId = getAugmentID();
        StringTokenizer tokenizer = new StringTokenizer(augmentId, "_");
        String result = "";
        while (tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            try {
                Integer.parseInt(next);
            } catch (NumberFormatException e) {
                result += next + "_";
            }
        }
        if (result.length() > 1)
            result = result.substring(0, result.length() - 1);
        return result;
    }
    /** Used to filter augments when only augments with certain implementations are wanted **/
    public abstract int getAugmentMask();
    /** Returns an NbtCompound containing data that maps to the Augment **/
    public NbtCompound getNbtCompound() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(AUGMENT_NBT_INDICATOR, getAugmentID());
        nbt.putInt(AUGMENT_NBT_MASK_INDICATOR, getAugmentMask());
        return nbt;
    }

    public String getTranslation() {
        return TranslationStorage.getInstance().get("augment." + Statik.MOD_ID + "." + getAugmentIDWithoutLevel());
    }

    public boolean acceptsTool(AbstractEngineeredTool tool) {
        return true;
    }
    public boolean acceptsItem(ItemStack stack) {
        if (stack.getItem() instanceof AbstractEngineeredTool)
            return acceptsTool((AbstractEngineeredTool) stack.getItem());
        List<Augment> augments = AugmentHelper.getAugments(stack);
        return stack.getItem() == ModItems.AUGMENTED_TABLET_ITEM &&
                (augments.size() == 0 || (augments.size() == 1 && augments.get(0) == this));
    }

    public int getLevel() { return 1; }

    /** All of the augment methods that can be overriden to apply effects **/
    public float onGetAttackDamage(ItemStack stack, AbstractEngineeredTool tool) { return 0; }
    public float getAugmentAttackDamage() { return 0; }
    public float onGetMiningSpeedMultiplier(ItemStack stack, AbstractEngineeredTool tool) { return 1; }
    public float getAugmentMiningSpeedMultiplier() { return 1; }
    public float getAugmentAttackSpeed() { return 0; }
    public ActionResult onUseOnBlock(ItemStack stack, AbstractEngineeredTool tool, ItemUsageContext context) { return ActionResult.PASS; }
    public boolean onPostHit(ItemStack stack, AbstractEngineeredTool tool, LivingEntity target, LivingEntity attacker) { return false; }
    public boolean onPostMine(boolean effectiveMine, ItemStack stack, AbstractEngineeredTool tool, World world, BlockState state, BlockPos pos, LivingEntity miner) { return false; }
    public boolean onUseWeaponAbility(ItemStack stack, AbstractEngineeredTool tool, Entity target, PlayerEntity attacker) { return false; }
    public boolean onInventoryTick(ItemStack stack, AbstractEngineeredTool tool, World world, Entity entity, int slot, boolean selected) { return false; }
    public boolean onItemNoLongerSuitable(ItemStack stack, AbstractEngineeredTool tool) { return false; }
    public boolean onItemNowSuitable(ItemStack stack, AbstractEngineeredTool tool) { return false; }
    public boolean onStateInEffectiveBlockList(ItemStack stack, AbstractEngineeredTool tool) { return false; }

    public static Augment fromNbt(NbtCompound nbt) {
        String augmentID = nbt.getString(AUGMENT_NBT_INDICATOR);
        Augment aug = stringToAugment.get(augmentID);
        return aug;
    }

    public static boolean writeAugmentsToNbt(NbtCompound nbt, List<Augment> augments) {
        NbtList nbtList = new NbtList();
        for (Augment aug: augments) {
            nbtList.add(aug.getNbtCompound());
        }
        nbt.put(AUGMENT_NBT_ID, nbtList);
        return true;
    }

    public static boolean writeAugmentsToNbt(NbtCompound nbt, NbtList nbtList) {
        nbt.put(AUGMENT_NBT_ID, nbtList);
        return true;
    }

    public static NbtList getAugmentsNbtList(ItemStack stack) {
        return stack.hasNbt() ? stack.getNbt().getList(AUGMENT_NBT_ID, 10) : new NbtList();
    }

    public static List<Augment> getAugments(ItemStack stack) {
        NbtList nbtList = getAugmentsNbtList(stack);
        if (nbtList == null)
            return null;

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
        NbtList nbtList = getAugmentsNbtList(stack);
        if (nbtList == null)
            return null;

        List<Augment> augments = new ArrayList<>(nbtList.size());
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);
            if ((nbt.getInt(AUGMENT_NBT_MASK_INDICATOR) & mask) == 0)
                continue;

            Augment aug = Augment.fromNbt(nbt);
            if (aug != null && !augments.contains(aug))
                augments.add(aug);
        }
        return augments;
    }

    public boolean applyAugment(ItemStack stack) {
        NbtList augments = getAugmentsNbtList(stack);
        boolean contains = false;
        for (int i = 0; i < augments.size(); i++) {
            Augment temp = Augment.fromNbt(augments.getCompound(i));
            if (temp.getAugmentIDWithoutLevel().equals(getAugmentIDWithoutLevel())) {
                if (temp.getLevel() >= getLevel()) {
                    contains = true;
                    break;
                } else {
                    augments.remove(i);
                    i--;
                }
            }
        }
        if (!contains) {
            Statik.logInfo("Saving augment " + getAugmentID() + " to item");
            augments.add(getNbtCompound());
            writeAugmentsToNbt(stack.getOrCreateNbt(), augments);
            return true;
        }
        return false;
    }

}
