package de.florianmichael.vialoadingbase;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import de.florianmichael.vialoadingbase.platform.ViaRewindPlatformImpl;
import de.florianmichael.vialoadingbase.platform.viaversion.CustomViaProviders;
import de.florianmichael.vialoadingbase.platform.ViaBackwardsPlatformImpl;
import de.florianmichael.vialoadingbase.platform.ViaVersionPlatformImpl;
import de.florianmichael.vialoadingbase.platform.viaversion.CustomViaInjector;
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViaLoadingBase {
    public static final String VERSION = "${vialoadingbase_version}";
    private final static ViaLoadingBase instance = new ViaLoadingBase();

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaLoadingBase-%d").build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8, threadFactory);

    private final Logger logger = new JLoggerToLog4j(LogManager.getLogger("ViaLoadingBase"));

    private NativeProvider provider;
    private File directory;

    private VersionListEnum targetVersion = VersionListEnum.r1_19_3;

    public static VersionListEnum getTargetVersion() {
        final NativeProvider provider = ViaLoadingBase.instance().provider();
        if (provider.isSinglePlayer()) {
            return provider.nativeVersion();
        }
        return ViaLoadingBase.instance().targetVersion;
    }

    public void init(final NativeProvider provider, final Runnable onLoadSubPlatforms) {
        this.provider = provider;
        this.directory = new File(this.provider.run(), "ViaLoadingBase");

        final ViaVersionPlatformImpl platform = new ViaVersionPlatformImpl(this.logger());

        final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform);
        provider().createViaPlatform(builder);

        Via.init(builder.build());

        final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();

        viaManager.addEnableListener(() -> {
            loadSubPlatform("ViaBackwards", () -> {
                final boolean isBackwardsLoaded = hasClass("com.viaversion.viabackwards.api.ViaBackwardsPlatform");
                if (isBackwardsLoaded) new ViaBackwardsPlatformImpl();
                return isBackwardsLoaded;
            });

            loadSubPlatform("ViaRewind", () -> {
                final boolean isRewindLoaded = hasClass("de.gerrygames.viarewind.api.ViaRewindPlatform");
                if (isRewindLoaded) new ViaRewindPlatformImpl();
                return isRewindLoaded;
            });

            onLoadSubPlatforms.run();
        });
        MappingDataLoader.enableMappingsCache();

        viaManager.init();
        viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
        viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
        ((ProtocolManagerImpl) viaManager.getProtocolManager()).refreshVersions();
    }

    public void switchVersionTo(final int protocolVersion) {
        targetVersion = VersionListEnum.fromProtocolId(protocolVersion);
    }

    public static boolean hasClass(final String classPath) {
        try {
            Class.forName(classPath);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void loadSubPlatform(final String name, final BooleanSupplier caller) {
        final Logger logger = ViaLoadingBase.instance().logger();
        try {
            if (caller.getAsBoolean()) {
                logger.log(Level.INFO, "Loaded " + name);
            } else {
                logger.log(Level.WARNING, name + " is not provided at all?");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load " + name + ":");
            e.printStackTrace();
        }
    }

    public NativeProvider provider() {
        return provider;
    }

    public File directory() {
        return directory;
    }

    public ThreadFactory threadFactory() {
        return threadFactory;
    }

    public ExecutorService executorService() {
        return executorService;
    }

    public Logger logger() {
        return logger;
    }

    public static ViaLoadingBase instance() {
        return instance;
    }
}
