package pyerter.statik;

import net.fabricmc.api.ClientModInitializer;
import pyerter.statik.block.ModBlocks;
import pyerter.statik.block.entity.ModBlockEntities;
import pyerter.statik.item.ModItems;
import pyerter.statik.item.predproviders.ModPredicateProviders;
import pyerter.statik.particle.ModParticles;
import pyerter.statik.screen.ModScreens;

public class StatikClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModScreens.registerScreens();
        ModBlocks.registerModBlockRenderLayers();
        ModParticles.registerParticleFactories();
        ModPredicateProviders.registerPredicateOverrides();
    }
}
