package pyerter.statik.item.custom.engineering;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;

public class EngineeredShovel extends AbstractEngineeredTool {

    public EngineeredShovel(float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.SHOVEL_MINEABLE, settings);

        toolType = ToolType.SHOVEL;
        super.registerTool(this, material, ToolType.SHOVEL);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (anitcipatedUseOnBlockResult(context) == ActionResult.FAIL)
            return ActionResult.PASS;

        return super.useShovelActionOnBlock(context);
    }
}
