package pyerter.statik.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;

public class ModItems {

    public static final Item SEPHRINE_DUST = registerItemToGroups("sephrine_dust",
            new Item(new FabricItemSettings()),
            ModItemGroup.STATIK, ItemGroups.INGREDIENTS);

    private static Item registerItemToGroups(String name, Item item, ItemGroup ... groups) {
        Item result = Registry.register(Registries.ITEM, new Identifier(Statik.MOD_ID, name), item);
        for (ItemGroup group: groups) {
            addItemToGroup(group, result);
        }
        return result;
    }

    private static void addItemToGroup(ItemGroup group, Item item) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
    }

    public static void registerModItems() {
        Statik.LOGGER.info("Registering mod items.");
    }

}
