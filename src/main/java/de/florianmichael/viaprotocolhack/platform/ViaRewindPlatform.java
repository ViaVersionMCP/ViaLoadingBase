package de.florianmichael.viaprotocolhack.platform;

import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import de.gerrygames.viarewind.api.ViaRewindConfigImpl;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatform implements de.gerrygames.viarewind.api.ViaRewindPlatform {

    public ViaRewindPlatform() {
        final ViaRewindConfigImpl config = new ViaRewindConfigImpl(new File(ViaProtocolHack.instance().directory(), "viarewind.yml"));
        config.reloadConfig();
        this.init(config);
    }

    @Override
    public Logger getLogger() {
        return ViaProtocolHack.instance().logger();
    }
}
