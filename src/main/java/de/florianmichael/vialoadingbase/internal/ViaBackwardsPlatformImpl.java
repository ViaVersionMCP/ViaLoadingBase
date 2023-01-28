package de.florianmichael.vialoadingbase.internal;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.api.SubPlatform;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl implements ViaBackwardsPlatform {
    private final File directory;

    public ViaBackwardsPlatformImpl(final File directory) {
        this.init(this.directory = directory);
    }

    @Override
    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public void disable() {}

    @Override
    public File getDataFolder() {
        return directory;
    }
}