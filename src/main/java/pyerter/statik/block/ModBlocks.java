package pyerter.statik.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import pyerter.statik.Statik;
import pyerter.statik.item.ModItemGroup;

public class ModBlocks {
    public static final Block SPIRAL_CUBE_BLOCK = registerBlock("spiral_cube_block",
            new Block(FabricBlockSettings.of(Material.METAL).luminance(6).strength(4f).requiresTool()),
            ModItemGroup.STATIK);
    public static final Block SEPHRINE_ORE = registerBlock("sephrine_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.of(Material.STONE).strength(3f).requiresTool(),
                    UniformIntProvider.create(2, 7)),
            ModItemGroup.STATIK);
    public static final Block DEEPSLATE_SEPHRINE_ORE = registerBlock("deepslate_sephrine_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.of(Material.STONE).strength(3f).requiresTool(),
                    UniformIntProvider.create(2, 7)),
            ModItemGroup.STATIK);

    private static Block registerBlock(String name, Block block, ItemGroup ... groups) {
        registerBlockItem(name, block, groups);
        return Registry.register(Registries.BLOCK, new Identifier(Statik.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup ... groups) {
        Item item = Registry.register(Registries.ITEM, new Identifier(Statik.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
        for (ItemGroup group: groups) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        }
        return item;
    }

    public static void registerModBlocks() {
        Statik.LOGGER.info("Registering ModBlocks for " + Statik.MOD_ID);
    }
}
