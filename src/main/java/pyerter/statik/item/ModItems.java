package pyerter.statik.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import pyerter.statik.Statik;
import pyerter.statik.block.ModBlocks;
import pyerter.statik.item.custom.*;
import pyerter.statik.item.custom.engineering.*;
import pyerter.statik.item.custom.engineering.augment.AugmentedTabletItem;
import pyerter.statik.item.custom.engineering.augment.Augments;
import pyerter.statik.util.IItemWithVariantItemGroupStacks;

import java.util.List;

public class ModItems {

    /* Sephrine-centric items */
    public static final Item SEPHRINE_DUST = registerItemToGroups("sephrine_dust",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK, ItemGroups.INGREDIENTS);
    public static final Item STARMETAL_ALLOY_INGOT = registerItemToGroups("starmetal_alloy_ingot",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item STARMETAL_COMPOUND_MIX = registerItemToGroups("starmetal_compound_mix",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item NETHER_STAR_DUST = registerItemToGroups("nether_star_dust",
            new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)), ModItemGroup.STATIK);
    public static final Item SEPHRINE_STAR = registerItemToGroups("sephrine_star",
            new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)), ModItemGroup.STATIK);
    public static final Item SEPHRINE_STAR_BASE = registerItemToGroups("sephrine_star_base",
            new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)), ModItemGroup.STATIK);
    // This thing is kinda outta place
    /*public static final Item CELESTIAL_FOCUS_STAFF = registerItemToGroups("celestial_focus_staff",
            new CelestialFocusStaff(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)), ModItemGroup.STATIK);*/

    /* Sephrine weapons */
    public static final Item STARMETAL_SWORD = registerItemToGroups("starmetal_sword",
            new StarmetalSword(3, 1f,
                    new FabricItemSettings().fireproof()), ModItemGroup.STATIK);
    public static final Item STARMETAL_SHOVEL = registerItemToGroups("starmetal_shovel",
            new StarmetalShovel(1.5f, -3f,
                    new FabricItemSettings().fireproof()), ModItemGroup.STATIK);
    public static final Item STARMETAL_PICKAXE = registerItemToGroups("starmetal_pickaxe",
            new StarmetalPickaxe(1, -2.8f,
                    new FabricItemSettings().fireproof()), ModItemGroup.STATIK);
    public static final Item STARMETALE_AXE = registerItemToGroups("starmetal_axe",
            new StarmetalAxe(6, -3f,
                    new FabricItemSettings().fireproof()), ModItemGroup.STATIK);
    public static final Item STARMETAL_HOE = registerItemToGroups("starmetal_hoe",
            new StarmetalHoe(-4, 0f,
                    new FabricItemSettings().fireproof()), ModItemGroup.STATIK);

    /* Engineered tools and weapons */
    public static final Item ENGINEERED_STONE_SWORD = registerItemToGroups("engineered_stone_sword",
            new EngineeredSword(3, -2.4f, ToolMaterials.STONE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STONE_SHOVEL = registerItemToGroups("engineered_stone_shovel",
            new EngineeredShovel(1.5f, -3f, ToolMaterials.STONE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STONE_PICKAXE = registerItemToGroups("engineered_stone_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ToolMaterials.STONE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STONE_AXE = registerItemToGroups("engineered_stone_axe",
            new EngineeredAxe(6, -3f, ToolMaterials.STONE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STONE_HOE = registerItemToGroups("engineered_stone_hoe",
            new EngineeredHoe(-1, 0f, ToolMaterials.STONE,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item ENGINEERED_IRON_SWORD = registerItemToGroups("engineered_iron_sword",
            new EngineeredSword(3, -2.4f, ToolMaterials.IRON,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_IRON_SHOVEL = registerItemToGroups("engineered_iron_shovel",
            new EngineeredShovel(1.5f, -3f, ToolMaterials.IRON,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_IRON_PICKAXE = registerItemToGroups("engineered_iron_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ToolMaterials.IRON,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_IRON_AXE = registerItemToGroups("engineered_iron_axe",
            new EngineeredAxe(6, -3f, ToolMaterials.IRON,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_IRON_HOE = registerItemToGroups("engineered_iron_hoe",
            new EngineeredHoe(-2, 0f, ToolMaterials.IRON,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item ENGINEERED_GOLDEN_SWORD = registerItemToGroups("engineered_golden_sword",
            new EngineeredSword(3, -2.4f, ToolMaterials.GOLD,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_GOLDEN_SHOVEL = registerItemToGroups("engineered_golden_shovel",
            new EngineeredShovel(1.5f, -3f, ToolMaterials.GOLD,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_GOLDEN_PICKAXE = registerItemToGroups("engineered_golden_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ToolMaterials.GOLD,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_GOLDEN_AXE = registerItemToGroups("engineered_golden_axe",
            new EngineeredAxe(6, -3f, ToolMaterials.GOLD,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_GOLDEN_HOE = registerItemToGroups("engineered_golden_hoe",
            new EngineeredHoe(0, 0f, ToolMaterials.GOLD,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item ENGINEERED_DIAMOND_SWORD = registerItemToGroups("engineered_diamond_sword",
            new EngineeredSword(3, -2.4f, ToolMaterials.DIAMOND,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_DIAMOND_SHOVEL = registerItemToGroups("engineered_diamond_shovel",
            new EngineeredShovel(1.5f, -3f, ToolMaterials.DIAMOND,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_DIAMOND_PICKAXE = registerItemToGroups("engineered_diamond_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ToolMaterials.DIAMOND,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_DIAMOND_AXE = registerItemToGroups("engineered_diamond_axe",
            new EngineeredAxe(6, -3f, ToolMaterials.DIAMOND,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_DIAMOND_HOE = registerItemToGroups("engineered_diamond_hoe",
            new EngineeredHoe(-3, 0f, ToolMaterials.DIAMOND,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item ENGINEERED_NETHERITE_SWORD = registerItemToGroups("engineered_netherite_sword",
            new EngineeredSword(3, -2.4f, ToolMaterials.NETHERITE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_NETHERITE_SHOVEL = registerItemToGroups("engineered_netherite_shovel",
            new EngineeredShovel(1.5f, -3f, ToolMaterials.NETHERITE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_NETHERITE_PICKAXE = registerItemToGroups("engineered_netherite_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ToolMaterials.NETHERITE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_NETHERITE_AXE = registerItemToGroups("engineered_netherite_axe",
            new EngineeredAxe(6, -3f, ToolMaterials.NETHERITE,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_NETHERITE_HOE = registerItemToGroups("engineered_netherite_hoe",
            new EngineeredHoe(-4, 0f, ToolMaterials.NETHERITE,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item ENGINEERED_STARMETAL_SWORD = registerItemToGroups("engineered_starmetal_sword",
            new EngineeredSword(3, 1f, ModToolMaterials.STARMETAL,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STARMETAL_SHOVEL = registerItemToGroups("engineered_starmetal_shovel",
            new EngineeredShovel(1.5f, -3f, ModToolMaterials.STARMETAL,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STARMETAL_PICKAXE = registerItemToGroups("engineered_starmetal_pickaxe",
            new EngineeredPickaxe(1, -2.8f, ModToolMaterials.STARMETAL,
                    new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ENGINEERED_STARMETAL_AXE = registerItemToGroups("engineered_starmetal_axe",
            new EngineeredAxe(6, -3f, ModToolMaterials.STARMETAL,
                    new FabricItemSettings()));
    public static final Item ENGINEERED_STARMETAL_HOE = registerItemToGroups("engineered_starmetal_hoe",
            new EngineeredHoe(-5, 0f, ModToolMaterials.STARMETAL,
                    new FabricItemSettings()), ModItemGroup.STATIK);

    /* Engineering tools and items */
    public static final Item ENGINEERS_TRUSTY_HAMMER = registerItemToGroups("engineers_trusty_hammer",
            new EngineersTrustyHammer(3, -2f, new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item EMPTY_BLUEPRINT = registerItemToGroups("empty_blueprint",
            new Item(new FabricItemSettings().maxCount(16)), ModItemGroup.STATIK);
    public static final Item ENGINEERS_BLUPRINT = registerItemToGroups("engineers_blueprint",
            new EngineersBlueprintItem(new FabricItemSettings().maxCount(1)), ModItemGroup.STATIK);
    public static final Item ENGINEERS_REPAIR_KIT = registerItemToGroups("engineers_repair_kit",
            new EngineersRepairKit(new FabricItemSettings().maxCount(16)), ModItemGroup.STATIK);
    public static final Item FLASK_BLUEPRINT = registerItemToGroups("flask_blueprint",
            new Item(new FabricItemSettings().maxCount(1)), ModItemGroup.STATIK);

    public static final Item SCREW_ITEM = registerItemToGroups("screw_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item COPPER_WIRE_ITEM = registerItemToGroups("copper_wire_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GOLD_THREADING_ITEM = registerItemToGroups("gold_threading_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item SEPHRINE_WIRE_ITEM = registerItemToGroups("sephrine_wire_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item POUNDED_KELP = registerItemToGroups("pounded_kelp_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item STICKY_SLUDGE_ITEM = registerItemToGroups("sticky_sludge_item",
            new DescriptiveItem(new FabricItemSettings().maxCount(256),
                    DescriptiveItem.DescriptionBuilder.start().add("It's uncomfortably", Formatting.GRAY)
                            .add("STICKY...", Formatting.DARK_GREEN, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item PYRIFERA_INSULATION_GOO = registerItemToGroups("pyrifera_insulation_goo",
            new Item(new FabricItemSettings().recipeRemainder(STICKY_SLUDGE_ITEM)), ModItemGroup.STATIK);
    public static final Item WIRING_KIT_ITEM = registerItemToGroups("wiring_kit",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ELECTRONICS_BASE = registerItemToGroups("electronics_base",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item TEMPERED_IRON_INGOT = registerItemToGroups("tempered_iron_ingot",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item SEPHRINE_CAPACITOR = registerItemToGroups("sephrine_capacitor",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item SURGE_CAPACITOR = registerItemToGroups("surge_capacitor",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);

    public static final Item MAKESHIFT_CORE = registerItemToGroups("makeshift_core",
            new MakeshiftCore(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);
    public static final Item MAKESHIFT_CATALYST = registerItemToGroups("makeshift_catalyst",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);
    public static final Item CATALYST_ENHANCER_ENGRAVING = registerItemToGroups("catalyst_enhancer_engraving",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1),
                    DescriptiveItem.DescriptionBuilder.start().add("Engraving", Formatting.AQUA, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item CATALYST_ENHANCER_TEMPERING = registerItemToGroups("catalyst_enhancer_tempering",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1),
                    DescriptiveItem.DescriptionBuilder.start().add("Tempering", Formatting.AQUA, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item CATALYST_ENHANCER_SIFTING = registerItemToGroups("catalyst_enhancer_sifting",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1),
                    DescriptiveItem.DescriptionBuilder.start().add("Sifting", Formatting.AQUA, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item CATALYST_ENHANCER_EROSION = registerItemToGroups("catalyst_enhancer_erosion",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1),
                    DescriptiveItem.DescriptionBuilder.start().add("Erosion", Formatting.AQUA, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item CATALYST_ENHANCER_INFUSION = registerItemToGroups("catalyst_enhancer_infusion",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1),
                    DescriptiveItem.DescriptionBuilder.start().add("Infusion", Formatting.AQUA, Formatting.ITALIC).build()),
            ModItemGroup.STATIK);
    public static final Item STORM_CORE = registerItemToGroups("storm_core",
            new StormCore(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);
    public static final Item STORM_CATALYST = registerItemToGroups("storm_catalyst",
            new RecipeWithstandingItem(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);


    /* Legendary weapons? */
    public static final Item MALICE_SCYTHE = registerItemToGroups("malice_scythe",
            new MaliceScythe(9f, -1f, new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item JONATHAN_HAMMER = registerItemToGroups("jonathan_hammer",
            new JonathanHammer(6f, -2f, new FabricItemSettings()), ModItemGroup.STATIK);


    /* Augment focused items */
    public static final Item AUGMENTED_TABLET_ITEM = registerItemToGroups("augmented_tablet",
            new AugmentedTabletItem(new FabricItemSettings().maxCount(1)), ModItemGroup.STATIK);
    public static final Item GLOW_RING = registerItemToGroups("glow_ring",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GLOW_RINGS = registerItemToGroups("glow_rings",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GLOW_RING_BINDING = registerItemToGroups("glow_ring_binding",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item POLISHED_LEATHER = registerItemToGroups("polished_leather",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item TREATED_LEATHER = registerItemToGroups("treated_leather",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item ETCHED_COVER = registerItemToGroups("etched_cover",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GILDED_TABLET_PAGE = registerItemToGroups("gilded_tablet_page",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GILDED_TABLET_PAGES = registerItemToGroups("gilded_tablet_pages",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);

    /* Miscellanious items */
    public static final Item HEALTH_FLASK = registerItemToGroups("health_flask",
            new HealthFlaskItem(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);
    public static final Item PAUTSCH_ITEM = registerItemToGroups("pautsch_item",
            new PautschItem(new FabricItemSettings().fireproof().maxCount(1)), ModItemGroup.STATIK);

    /* Cooking items */

    public static final Item ROLLING_PIN_ITEM = registerItemToGroups("rolling_pin_item",
            new RecipeWithstandingItem(new FabricItemSettings().maxCount(1)), ModItemGroup.STATIK);
    public static final Item COOKIE_CUTTER_ITEM = registerItemToGroups("cookie_cutter_item",
            new RecipeWithstandingItem(new FabricItemSettings().maxCount(1)));
    public static final Item BREAD_KNIFE_ITEM = registerItemToGroups("bread_knife_item",
            new BreadKnifeItem(new FabricItemSettings().maxCount(1)), ModItemGroup.STATIK);

    public static final Item SLICED_BREAD_ITEM = registerItemToGroups("sliced_bread_item",
            new Item(new FabricItemSettings().food(
                    (new FoodComponent.Builder()).hunger(2).snack().build())),
            ModItemGroup.STATIK);
    public static final Item DOUGH_ITEM = registerItemToGroups("dough_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item FLATTENED_DOUGH_ITEM = registerItemToGroups("flattened_dough_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item COOKIE_DOUGH_ITEM = registerItemToGroups("cookie_dough_item",
            new Item(new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item CHOCOLATE_CHIP_COOKIE_DOUGH_ITEM = registerItemToGroups("chocolate_chip_cookie_dough_item",
            new Item(new FabricItemSettings().food(
                    (new FoodComponent.Builder()).hunger(2).snack().build())),
            ModItemGroup.STATIK);

    public static final Item GARLIC_CLOVE_ITEM = registerItemToGroups("garlic_clove_item",
            new AliasedBlockItem(ModBlocks.GARLIC_CROP_BLOCK, new FabricItemSettings()), ModItemGroup.STATIK);
    public static final Item GARLIC_ITEM = registerItemToGroups("garlic_item",
            new DescriptiveItem(new FabricItemSettings(),
                    DescriptiveItem.DescriptionBuilder.start().add("Stimulate the immune system!")
                            .add("And maybe keep away vampires...", Formatting.ITALIC, Formatting.GRAY).build()),
            ModItemGroup.STATIK);

    public static final Item ENDURING_FLESH = registerItemToGroups("enduring_flesh",
            new Item(new FabricItemSettings().food((
                    new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).meat()
                    .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)
                    .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 0), 0.5f).build())),
            ModItemGroup.STATIK);

    private static Item registerItemToGroups(String name, Item item, ItemGroup ... groups) {
        Item result = Registry.register(Registries.ITEM, new Identifier(Statik.MOD_ID, name), item);
        for (ItemGroup group: groups) {
            addItemToGroup(group, result);
        }
        return result;
    }

    private static void addItemToGroup(ItemGroup group, Item item) {
        if (item instanceof IItemWithVariantItemGroupStacks) {
            List<ItemStack> stacks = ((IItemWithVariantItemGroupStacks)item).getVariantStacks();
            addItemStacksToGroup(group, stacks);
        } else {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        }
    }

    private static void addItemStackToGroup(ItemGroup group, ItemStack stack) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(stack));
    }

    private static void addItemStacksToGroup(ItemGroup group, List<ItemStack> stacks) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
            for (ItemStack stack: stacks) {
                if (!stack.isEmpty())
                    entries.add(stack);
                else
                    Statik.LOGGER.warn("Appending stacks to group " + group.getDisplayName() + ", stack is empty: " + stack.toString());
            }
        });
    }

    public static void registerModItems() {
        Statik.LOGGER.info("Registering mod items.");
        Augments.registerAugments();
    }
}
