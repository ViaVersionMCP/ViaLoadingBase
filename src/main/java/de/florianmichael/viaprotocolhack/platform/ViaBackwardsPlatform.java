package de.florianmichael.viaprotocolhack.platform;

import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatform implements com.viaversion.viabackwards.api.ViaBackwardsPlatform {

    public ViaBackwardsPlatform() {
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
