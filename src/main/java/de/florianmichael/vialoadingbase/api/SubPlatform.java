package de.florianmichael.vialoadingbase.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.api.version.ProtocolList;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

public class SubPlatform {
    private final String name;
    private final BooleanSupplier load;
    private final Runnable executor;
    private final List<ProtocolVersion> protocolVersions;

    public SubPlatform(String name, BooleanSupplier load, Runnable executor, List<ProtocolVersion> protocolVersions) {
        this.name = name;
        this.load = load;
        this.executor = executor;
        this.protocolVersions = protocolVersions;
    }

    public static boolean isClass(final String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public void build(final Logger logger) {
        if (this.load.getAsBoolean()) {
            try {
                this.executor.run();
                ProtocolList.load(this.protocolVersions);
                logger.info("Loaded sub Platform " + this.name);
            } catch (Throwable t) {
                logger.severe("An error occurred while loading sub Platform " + this.name + ":");
                t.printStackTrace();
            }
        }
    }
}
