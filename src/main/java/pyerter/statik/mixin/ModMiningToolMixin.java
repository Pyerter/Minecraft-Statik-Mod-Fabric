package pyerter.statik.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pyerter.statik.tag.ModBlockTags;

@Mixin(MiningToolItem.class)
public abstract class ModMiningToolMixin extends ToolItem {

    public ModMiningToolMixin(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Inject(method="isSuitableFor", at=@At(value="HEAD"), cancellable = true)
    public void onToolIsSuitableForCheck(BlockState state, CallbackInfoReturnable info) {
        int toolLevel = getMaterial().getMiningLevel();
        if (toolLevel < 5 && state.isIn(ModBlockTags.NEEDS_STARMETAL_TOOL)) {
            info.setReturnValue(false);
        } else if (toolLevel < 4 && state.isIn(ModBlockTags.NEEDS_NETHERITE_TOOL)) {
            info.setReturnValue(false);
        }
    }

}
