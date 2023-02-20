package pyerter.statik.item.custom.engineering;

public class EngineersBlueprintItem extends AbstractEngineersItem {
    public EngineersBlueprintItem(Settings settings) {
        super(settings);
    }

    @Override
    public ENGINEERS_CRAFT_TYPE getEngineersCraftType() {
        return ENGINEERS_CRAFT_TYPE.ENGINEERIFY;
    }
}
