package de.florianmichael.vialoadingbase.platform;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl implements ViaBackwardsPlatform {

    public ViaBackwardsPlatformImpl() {
        this.init(ViaLoadingBase.instance().directory());
    }

    @Override
    public Logger getLogger() {
        return ViaLoadingBase.instance().logger();
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public void disable() {
    }

    @Override
    public File getDataFolder() {
        return new File(ViaLoadingBase.instance().directory(), "viabackwards.yml");
    }
}
