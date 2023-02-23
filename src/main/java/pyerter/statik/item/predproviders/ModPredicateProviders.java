package pyerter.statik.item.predproviders;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import pyerter.statik.item.ModItems;

public class ModPredicateProviders {
    public static void registerPredicateOverrides() {
        ModelPredicateProviderRegistry.register(ModItems.MAKESHIFT_CORE, new Identifier("charge"), new ChargeablePredicateProvider(
                (stack, chargeable) -> chargeable.getOverridePredicateChargeValue(stack)));
        ModelPredicateProviderRegistry.register(ModItems.STORM_CORE, new Identifier("charge"), new ChargeablePredicateProvider(
                (stack, chargeable) -> chargeable.getOverridePredicateChargeValue(stack)));
        ModelPredicateProviderRegistry.register(ModItems.MALICE_SCYTHE, new Identifier("charge"), new ChargeablePredicateProvider(
                (stack, chargeable) -> chargeable.getOverridePredicateChargeValue(stack)));
        ModelPredicateProviderRegistry.register(ModItems.JONATHAN_HAMMER, new Identifier("charge"), new ChargeablePredicateProvider(
                (stack, chargeable) -> chargeable.getOverridePredicateChargeValue(stack)));

        ModelPredicateProviderRegistry.register(ModItems.HEALTH_FLASK, new Identifier("charge"), new FlaskChargePredicateProvider(
                (stack, flaskItem) -> flaskItem.getOverridePredicateChargeValue(stack)));
    }
}
