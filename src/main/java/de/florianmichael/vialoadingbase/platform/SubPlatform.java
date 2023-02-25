package de.florianmichael.vialoadingbase.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class SubPlatform {
    private final String name;
    private final BooleanSupplier load;
    private final Runnable executor;
    private final Consumer<List<ProtocolVersion>> versionCallback;

    public SubPlatform(String name, BooleanSupplier load, Runnable executor) {
        this(name, load, executor, null);
    }

    public SubPlatform(String name, BooleanSupplier load, Runnable executor, Consumer<List<ProtocolVersion>> versionCallback) {
        this.name = name;
        this.load = load;
        this.executor = executor;
        this.versionCallback = versionCallback;
    }

    public static boolean isClass(final String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void createProtocolPath() {
        if (this.versionCallback != null) {
            this.versionCallback.accept(InternalProtocolList.PRE_PROTOCOLS);
        }
    }

    public boolean build(final Logger logger) {
        if (this.load.getAsBoolean()) {
            try {
                this.executor.run();
                logger.info("Loaded sub Platform " + this.name);
                return true;
            } catch (Throwable t) {
                logger.severe("An error occurred while loading sub Platform " + this.name + ":");
                t.printStackTrace();
                return false;
            }
        }
        logger.severe("Sub platform " + this.name + " is not present");
        return false;
    }
}
