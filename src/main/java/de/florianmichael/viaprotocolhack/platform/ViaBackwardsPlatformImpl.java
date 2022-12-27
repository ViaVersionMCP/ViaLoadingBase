package de.florianmichael.viaprotocolhack.platform;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl implements ViaBackwardsPlatform {

    public ViaBackwardsPlatformImpl() {
        this.init(ViaProtocolHack.instance().directory());
    }

    @Override
    public Logger getLogger() {
        return ViaProtocolHack.instance().logger();
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
        return new File(ViaProtocolHack.instance().directory(), "viabackwards.yml");
    }
}
