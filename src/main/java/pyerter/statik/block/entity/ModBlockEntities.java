package pyerter.statik.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;
import pyerter.statik.block.ModBlocks;

public class ModBlockEntities {
    public static BlockEntityType<CaptureChamberEntity> CAPTURE_CHAMBER;
    public static BlockEntityType<CaptureChamberProviderEntity> CAPTURE_CHAMBER_PROVIDER;

    public static void registerAllBlockEntities() {
        CAPTURE_CHAMBER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "capture_chamber"),
                FabricBlockEntityTypeBuilder.create(CaptureChamberEntity::new, ModBlocks.CAPTURE_CHAMBER).build(null));

        CAPTURE_CHAMBER_PROVIDER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "capture_chamber_provider"),
                FabricBlockEntityTypeBuilder.create(CaptureChamberProviderEntity::new, ModBlocks.CAPTURE_CHAMBER_PROVIDER).build(null));
    }
}
