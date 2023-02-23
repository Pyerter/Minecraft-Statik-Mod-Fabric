package pyerter.statik.item.predproviders;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.item.custom.engineering.IChargeable;

import java.util.function.BiFunction;

@Environment(value= EnvType.CLIENT)
public class ChargeablePredicateProvider implements ClampedModelPredicateProvider {

    public BiFunction<ItemStack, IChargeable, Float> coreChargeFunction;
    public ChargeablePredicateProvider(BiFunction<ItemStack, IChargeable, Float> coreChargeFunction) {
        this.coreChargeFunction = coreChargeFunction;
    }

    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (stack.getItem() instanceof IChargeable) {
            IChargeable chargeable = (IChargeable) stack.getItem();
            return coreChargeFunction.apply(stack, chargeable);
        }
        return 0;
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (stack.getItem() instanceof IChargeable) {
            IChargeable chargeable = (IChargeable) stack.getItem();
            return coreChargeFunction.apply(stack, chargeable);
        }
        return 0;
    }
}