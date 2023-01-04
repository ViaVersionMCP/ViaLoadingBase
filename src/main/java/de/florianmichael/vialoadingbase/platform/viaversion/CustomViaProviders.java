package de.florianmichael.vialoadingbase.platform.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class CustomViaProviders implements ViaPlatformLoader {

    @Override
    public void load() {
        // Now, we can implement custom providers
        ViaLoadingBase.instance().provider().createProviders(Via.getManager().getProviders());
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
