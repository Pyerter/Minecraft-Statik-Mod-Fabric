package pyerter.statik.block.entity;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;
import pyerter.statik.block.ModBlocks;

public class ModBlockEntities {
    public static BlockEntityType<CaptureChamberEntity> CAPTURE_CHAMBER;
    public static BlockEntityType<CaptureChamberProviderEntity> CAPTURE_CHAMBER_PROVIDER;

    public static BlockEntityType<EngineeringStationEntity> ENGINEERING_STATION;

    public static BlockEntityType<FoodPreppingStationEntity> FOOD_PREPPING_STATION;

    public static BlockEntityType<KitchenStoveStationEntity> KITCHEN_STOVE_STATION;

    public static BlockEntityType<TridiBlockEntity> TRIDI;

    public static void registerAllBlockEntities() {
        CAPTURE_CHAMBER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "capture_chamber"),
                FabricBlockEntityTypeBuilder.create(CaptureChamberEntity::new, ModBlocks.CAPTURE_CHAMBER).build());

        CAPTURE_CHAMBER_PROVIDER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "capture_chamber_provider"),
                FabricBlockEntityTypeBuilder.create(CaptureChamberProviderEntity::new, ModBlocks.CAPTURE_CHAMBER_PROVIDER).build());

        ENGINEERING_STATION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "engineering_station"),
                FabricBlockEntityTypeBuilder.create(EngineeringStationEntity::new, ModBlocks.ENGINEERING_STATION).build(null));

        FOOD_PREPPING_STATION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "food_prepping_station"),
                FabricBlockEntityTypeBuilder.create(FoodPreppingStationEntity::new, ModBlocks.FOOD_PREPPING_STATION).build(null));

        KITCHEN_STOVE_STATION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "kitchen_stove_station"),
                FabricBlockEntityTypeBuilder.create(KitchenStoveStationEntity::new, ModBlocks.KITCHEN_STOVE_STATION).build(null));

        TRIDI = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(Statik.MOD_ID, "tridi"),
                FabricBlockEntityTypeBuilder.create(TridiBlockEntity::new, ModBlocks.TRIDI).build(null));
    }
}
