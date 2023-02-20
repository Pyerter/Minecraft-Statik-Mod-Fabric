package pyerter.statik.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import pyerter.statik.Statik;
import pyerter.statik.particle.custom.ManaParticle;

public class ModParticles {

    public static final Random RANDOM = Random.create();

    public static final DefaultParticleType ELECTRO_STATIC_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType MANA_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Statik.MOD_ID, "electro_static_particle"), ELECTRO_STATIC_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Statik.MOD_ID, "mana_particle"), MANA_PARTICLE);
    }

    public static void registerParticleFactories() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.ELECTRO_STATIC_PARTICLE, GlowParticle.ElectricSparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MANA_PARTICLE, ManaParticle.Factory::new);
    }

}
