package pyerter.statik.item.predproviders;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.item.custom.HealthFlaskItem;

import java.util.function.BiFunction;

@Environment(value = EnvType.CLIENT)
public class FlaskChargePredicateProvider implements ClampedModelPredicateProvider {

    public BiFunction<ItemStack, HealthFlaskItem, Float> flaskChargeFunction;
    public FlaskChargePredicateProvider(BiFunction<ItemStack, HealthFlaskItem, Float> flaskChargeFunction) {
        this.flaskChargeFunction = flaskChargeFunction;
    }

    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (stack.getItem() instanceof HealthFlaskItem) {
            HealthFlaskItem healthFlaskItem = (HealthFlaskItem) stack.getItem();
            return flaskChargeFunction.apply(stack, healthFlaskItem);
        }
        return 0;
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (stack.getItem() instanceof HealthFlaskItem) {
            HealthFlaskItem healthFlaskItem = (HealthFlaskItem) stack.getItem();
            return flaskChargeFunction.apply(stack, healthFlaskItem);
        }
        return 0;
    }
}