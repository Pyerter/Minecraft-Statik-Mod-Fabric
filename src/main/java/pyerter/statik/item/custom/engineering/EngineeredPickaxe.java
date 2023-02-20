package pyerter.statik.item.custom.engineering;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;

public class EngineeredPickaxe extends AbstractEngineeredTool {

    public EngineeredPickaxe(float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.PICKAXE_MINEABLE, settings);

        toolType = ToolType.PICKAXE;
        super.registerTool(this, material, ToolType.PICKAXE);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }
}
