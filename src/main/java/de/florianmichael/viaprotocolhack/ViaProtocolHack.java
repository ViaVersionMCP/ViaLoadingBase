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

        final ViaVersionPlatform platform = new ViaVersionPlatform(this.logger());

        final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform);
        provider().onBuildViaPlatform(builder);

        Via.init(builder.build());
        whenComplete.run();

        MappingDataLoader.enableMappingsCache();

        final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();

        viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
        viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
        viaManager.init();

        try {
            Class.forName("com.viaversion.viabackwards.api.ViaBackwardsPlatform");
            new ViaBackwardsPlatform();
            logger().log(Level.INFO, "Loaded ViaBackwards");
        } catch (Exception e) {
            logger().log(Level.INFO, "Failed to load ViaBackwards:");
            e.printStackTrace();
        }

        try {
            Class.forName("de.gerrygames.viarewind.api.ViaRewindPlatform");
            new ViaRewindPlatform();
            logger().log(Level.INFO, "Loaded ViaRewind");
        } catch (Exception e) {
            logger().log(Level.INFO, "Failed to load ViaRewind:");
            e.printStackTrace();
        }

//        CompletableFuture.runAsync(() -> {
//            final ViaVersionPlatform platform = new ViaVersionPlatform(this.logger());
//
//            final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform);
//            provider().onBuildViaPlatform(builder);
//
//            Via.init(builder.build());
//            whenComplete.run();
//
//            MappingDataLoader.enableMappingsCache();
//
//            final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();
//
//            viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
//            viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
//            viaManager.init();
//
//            try {
//                Class.forName("com.viaversion.viabackwards.api.ViaBackwardsPlatform");
//                new ViaBackwardsPlatform();
//                logger().log(Level.INFO, "Loaded ViaBackwards");
//            } catch (Exception e) {
//                logger().log(Level.INFO, "Failed to load ViaBackwards:");
//                e.printStackTrace();
//            }
//
//            try {
//                Class.forName("de.gerrygames.viarewind.api.ViaRewindPlatform");
//                new ViaRewindPlatform();
//                logger().log(Level.INFO, "Loaded ViaRewind");
//            } catch (Exception e) {
//                logger().log(Level.INFO, "Failed to load ViaRewind:");
//                e.printStackTrace();
//            }
//        }).whenComplete((unused, throwable) -> {
//            if (throwable != null) {
//                logger().log(Level.INFO, "Failed to load ViaProtocolHack:");
//                throwable.printStackTrace();
//            } else {
//                logger().log(Level.INFO, "Loaded ViaProtocolHack");
//            }
//        });
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
