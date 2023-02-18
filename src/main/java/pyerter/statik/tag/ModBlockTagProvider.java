package pyerter.statik.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import pyerter.statik.Statik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static Map<TagKey<Block>, List<Block>> pendingTags = new HashMap<>();
    public static boolean tryRegisterBlockToTag(TagKey<Block> blockTag, Block block) {
        if (!pendingTags.containsKey(blockTag)) {
            pendingTags.put(blockTag, new ArrayList<>());
            pendingTags.get(blockTag).add(block);
            return true;
        } else if (!pendingTags.get(blockTag).contains(block)) {
            pendingTags.get(blockTag).add(block);
            return true;
        }
        return false;
    }
    public static boolean tryRegisterBlockTag(TagKey<Block> blockTag) {
        if (!pendingTags.containsKey(blockTag)) {
            pendingTags.put(blockTag, new ArrayList<>());
            return true;
        }
        return false;
    }

    //@Override
    protected void generateTags() {
        int generatedTags = 0;
        for (TagKey<Block> tagKey: pendingTags.keySet()) {
            if (pendingTags.get(tagKey).size() > 0)
                generatedTags++;
            this.getOrCreateTagBuilder(tagKey).add(pendingTags.get(tagKey).stream().toArray(Block[]::new));
        }
        Statik.logInfo("Generated " + generatedTags + " custom block tags.");
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        int generatedTags = 0;
        for (TagKey<Block> tagKey: pendingTags.keySet()) {
            if (pendingTags.get(tagKey).size() > 0)
                generatedTags++;
            this.getOrCreateTagBuilder(tagKey).add(pendingTags.get(tagKey).stream().toArray(Block[]::new));
        }
        Statik.logInfo("Generated " + generatedTags + " custom block tags.");
    }
}
