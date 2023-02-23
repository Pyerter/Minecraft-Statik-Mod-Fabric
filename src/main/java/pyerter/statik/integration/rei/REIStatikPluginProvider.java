package pyerter.statik.integration.rei;

import me.shedaniel.rei.api.common.plugins.REIPluginProvider;

import java.util.Collection;
import java.util.List;

public class REIStatikPluginProvider implements REIPluginProvider {
    @Override
    public Collection provide() {
        return null; //List.of(REIPootsAdditionsClientPlugin, REIPootsAdditionsServerPlugin);
    }

    @Override
    public Class getPluginProviderClass() {
        return this.getClass();
    }
}
