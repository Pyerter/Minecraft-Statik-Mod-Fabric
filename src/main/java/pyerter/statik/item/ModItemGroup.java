package pyerter.statik.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pyerter.statik.Statik;

public class ModItemGroup {
    public static ItemGroup STATIK;

    public static void registerItemGroups() {
        STATIK = FabricItemGroup.builder(new Identifier(Statik.MOD_ID))
                .displayName(Text.translatable("itemgroup.statik")) // or Text.literal("sephrine Items")
                .icon(() -> new ItemStack(ModItems.SEPHRINE_DUST)).build();
    }

}
