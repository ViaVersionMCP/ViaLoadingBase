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
import de.florianmichael.viaprotocolhack.util.JLoggerToLog4J;
import de.florianmichael.viaprotocolhack.util.VersionList;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ViaProtocolHack {
    private final static ViaProtocolHack instance = new ViaProtocolHack();

    private final ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaProtocolHack-%d").build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaProtocolHack-%d").build());
    private final EventLoop eventLoop = new DefaultEventLoopGroup(1, (Executor) factory).next();
    private final Logger logger = new JLoggerToLog4J(LogManager.getLogger("ViaProtocolHack"));

    private INativeProvider provider;
    private File directory;

    public void init(final INativeProvider provider, final Runnable whenComplete) throws Exception {
        this.provider = provider;
        this.directory = new File(this.provider.run(), "ViaProtocolHack");

        VersionList.registerProtocols();

        CompletableFuture.runAsync(() -> {
            final ViaVersionPlatform platform = new ViaVersionPlatform(this.logger());

            final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(platform);
            provider().onBuildViaPlatform(builder);

            Via.init(builder.build());
            MappingDataLoader.enableMappingsCache();

            final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();

            viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
            viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
            viaManager.init();

            if (provider().loadBackwards()) {
                new ViaBackwardsPlatform();
            }

            if (provider.loadRewind()) {
                new ViaRewindPlatform();
            }

        }).whenComplete((unused, throwable) -> whenComplete.run());
    }

    public INativeProvider provider() {
        return provider;
    }

    public File directory() {
        return directory;
    }

    public ExecutorService executorService() {
        return executorService;
    }

    public EventLoop eventLoop() {
        return eventLoop;
    }

    public Logger logger() {
        return logger;
    }

    public static ViaProtocolHack instance() {
        return instance;
    }
}
