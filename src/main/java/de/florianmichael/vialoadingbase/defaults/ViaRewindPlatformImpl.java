package de.florianmichael.vialoadingbase.defaults;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl implements ViaRewindPlatform {

    public ViaRewindPlatformImpl(final File directory) {
        final ViaRewindConfigImpl config = new ViaRewindConfigImpl(new File(directory, "viarewind.yml"));
        config.reloadConfig();
        this.init(config);
    }

    @Override
    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }
}
