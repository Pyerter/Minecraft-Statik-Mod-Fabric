package pyerter.statik.item.custom.engineering;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pyerter.statik.util.Util;

import java.util.List;

public interface IChargeable {
    public int getMaxCharge();
    public default String getChargeNbtID() {
        return "pootsadditions.charge";
    }

    public default int tryCharge(ItemStack stack, int amount) {
        int currentCharge = getCharge(stack);
        return addCharge(stack, amount) - currentCharge;
    }

    public default int addCharge(ItemStack stack, int charge) {
        return setCharge(stack, charge + getCharge(stack));
    }

    public default int setCharge(ItemStack stack, int charge) {
        charge = Util.clamp(charge, 0, getMaxCharge());
        if (stack.hasNbt()) {
            stack.getNbt().putInt(getChargeNbtID(), charge);
        } else {
            NbtCompound nbtData = new NbtCompound();
            nbtData.putInt(getChargeNbtID(), charge);
            stack.setNbt(nbtData);
        }

        return charge;
    }

    public default int getCharge(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains(getChargeNbtID())) {
            return stack.getNbt().getInt(getChargeNbtID());
        } else {
            return setCharge(stack, 0);
        }
    }

    public default double getFilledPercent(ItemStack stack) {
        return (float)getCharge(stack) / (float)getMaxCharge();
    }

    public default float getOverridePredicateChargeValue(ItemStack stack) {
        // 0, 0.25, 0.5, and 0.75 are the thresholds
        // 0 is empty
        // 0.25 is less than 1/3 full charge
        // 0.5 is greater than 0.25, less than 2/3 full charge
        // 0.75 is greater than 0.5
        double filledPercent = getFilledPercent(stack);
        if (filledPercent > 0.66)
            return 0.751f;
        if (filledPercent > 0.33)
            return 0.501f;
        if (filledPercent > 0)
            return 0.251f;
        return 0;
    }

    public default void addChargeTooltip(ItemStack stack, List<Text> tooltip) {
        tooltip.add(MutableText.of(new LiteralTextContent("Core Charge: " + getCharge(stack))).formatted(Formatting.AQUA));
    }
}
