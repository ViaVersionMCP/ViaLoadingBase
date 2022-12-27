package de.florianmichael.viaprotocolhack;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import de.florianmichael.viaprotocolhack.platform.ViaRewindPlatform;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaProviders;
import de.florianmichael.viaprotocolhack.platform.ViaBackwardsPlatform;
import de.florianmichael.viaprotocolhack.platform.ViaVersionPlatform;
import de.florianmichael.viaprotocolhack.platform.viaversion.CustomViaInjector;
import de.florianmichael.viaprotocolhack.util.JLoggerToLog4j;
import de.florianmichael.viaprotocolhack.util.VersionList;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViaProtocolHack {
    private final static ViaProtocolHack instance = new ViaProtocolHack();

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaProtocolHack-%d").build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8, threadFactory);

    private final Logger logger = new JLoggerToLog4j(LogManager.getLogger("ViaProtocolHack"));

    private INativeProvider provider;
    private File directory;

    public void init(final INativeProvider provider, final Runnable whenComplete) {
        this.provider = provider;
        this.directory = new File(this.provider.run(), "ViaProtocolHack");

        try {
            VersionList.registerProtocols();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(() -> {
            final ViaVersionPlatform platform = new ViaVersionPlatform(this.logger());

            final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform);
            provider().onBuildViaPlatform(builder);

            Via.init(builder.build());
            whenComplete.run();

            final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();

            viaManager.addEnableListener(() -> {
                loadSubPlatform("ViaBackwards", "com.viaversion.viabackwards.api.ViaBackwardsPlatform", ViaBackwardsPlatform::new);
                loadSubPlatform("ViaRewind", "de.gerrygames.viarewind.api.ViaRewindPlatform", ViaRewindPlatform::new);
            });
            MappingDataLoader.enableMappingsCache();

            viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
            viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
            viaManager.init();
        }).whenComplete((unused, throwable) -> {
            if (throwable != null) {
                logger().log(Level.INFO, "Failed to load ViaProtocolHack:");
                throwable.printStackTrace();
            } else {
                logger().log(Level.INFO, "Loaded ViaProtocolHack");
            }
        });
    }

    private void loadSubPlatform(final String name, final String classPath, final Runnable runnable) {
        try {
            Class.forName(classPath);
            runnable.run();
            logger().log(Level.INFO, "Loaded " + name);
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                logger().log(Level.INFO, name + " is not provided");
            } else {
                logger().log(Level.INFO, "Failed to load " + name + ":");
                e.printStackTrace();
            }
        }
    }

    public INativeProvider provider() {
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

    public static ViaProtocolHack instance() {
        return instance;
    }
}
