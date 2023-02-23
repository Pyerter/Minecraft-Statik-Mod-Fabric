package pyerter.statik.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import pyerter.statik.util.IItemWithVariantItemGroupStacks;
import pyerter.statik.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class HealthFlaskItem extends Item implements IItemWithVariantItemGroupStacks {
    public static final int MAX_CHARGES = 3;
    public static final String CHARGE_NBT_ID = "statik.charges";
    public static final float HEAL_AMOUNT = 20;

    public HealthFlaskItem(Settings settings) {
        super(settings);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }

        if (!world.isClient) {
            user.heal(HEAL_AMOUNT);
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                addCharge(stack, -1);
            }
        }

        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 24;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (getCharge(user.getStackInHand(hand)) > 0)
            return ItemUsage.consumeHeldItem(world, user, hand);

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public boolean hasGlint(ItemStack stack) {
        return getCharge(stack) > 0;
    }

    public int addCharge(ItemStack stack, int charge) {
        return setCharge(stack, charge + getCharge(stack));
    }

    public int setCharge(ItemStack stack, int charge) {
        charge = Util.clamp(charge, 0, MAX_CHARGES);
        if (stack.hasNbt()) {
            stack.getNbt().putInt(CHARGE_NBT_ID, charge);
        } else {
            NbtCompound nbtData = new NbtCompound();
            nbtData.putInt(CHARGE_NBT_ID, charge);
            stack.setNbt(nbtData);
        }

        return charge;
    }

    public int getCharge(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(CHARGE_NBT_ID)) {
            return stack.getNbt().getInt(CHARGE_NBT_ID);
        } else {
            return setCharge(stack, 0);
        }
    }

    @Override
    public List<ItemStack> getVariantStacks() {
        ArrayList<ItemStack> stacks = new ArrayList<>(4);
        stacks.add(new ItemStack(this));
        ItemStack charge1 = new ItemStack(this);
        setCharge(charge1, 1);
        stacks.add(charge1);
        ItemStack charge2 = new ItemStack(this);
        setCharge(charge2, 2);
        stacks.add(charge2);
        ItemStack charge3 = new ItemStack(this);
        setCharge(charge3, 3);
        stacks.add(charge3);
        return stacks;
    }

    public float getOverridePredicateChargeValue(ItemStack stack) {
        switch (getCharge(stack)) {
            case 3: return 0.751f;
            case 2: return 0.501f;
            case 1: return 0.251f;
            default: case 0: return 0;
        }
    }
}
