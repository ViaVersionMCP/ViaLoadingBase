package de.florianmichael.vialoadingbase;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import de.florianmichael.vialoadingbase.api.SubPlatform;
import de.florianmichael.vialoadingbase.api.version.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList;
import de.florianmichael.vialoadingbase.internal.ViaBackwardsPlatformImpl;
import de.florianmichael.vialoadingbase.internal.ViaRewindPlatformImpl;
import de.florianmichael.vialoadingbase.internal.viaversion.CustomViaProviders;
import de.florianmichael.vialoadingbase.internal.ViaVersionPlatformImpl;
import de.florianmichael.vialoadingbase.internal.viaversion.CustomViaInjector;
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ViaLoadingBase {
    public static final String VERSION = "${vialoadingbase_version}";

    public static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaLoadingBase-%d").build();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
    public static final Logger LOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaLoadingBase"));

    public static final SubPlatform PSEUDO_VIA_VERSION = new SubPlatform("ViaVersion", () -> true, () -> {
        // Empty
    }, protocolVersions -> protocolVersions.addAll(ViaVersionPlatformImpl.createVersionList()));
    public static final SubPlatform SUB_PLATFORM_VIA_BACKWARDS = new SubPlatform("ViaBackwards", () -> SubPlatform.isClass("com.viaversion.viabackwards.api.ViaBackwardsPlatform"), () -> {
        new ViaBackwardsPlatformImpl(Via.getManager().getPlatform().getDataFolder());
    });
    public static final SubPlatform SUB_PLATFORM_VIA_REWIND = new SubPlatform("ViaRewind", () -> SubPlatform.isClass("de.gerrygames.viarewind.api.ViaRewindPlatform"), () -> {
        new ViaRewindPlatformImpl(Via.getManager().getPlatform().getDataFolder());
    });

    private static ViaLoadingBase classWrapper;

    private final LinkedList<SubPlatform> subPlatforms;
    private final File runDirectory;
    private final int nativeVersion;
    private final BooleanSupplier singlePlayerProvider;
    private final EventLoop eventLoop;
    private final Supplier<JsonObject> dumpCreator;
    private final Consumer<ViaProviders> viaProviderCreator;
    private final Consumer<ViaManagerImpl.ViaManagerBuilder> viaManagerBuilderCreator;
    private final Consumer<ComparableProtocolVersion> protocolReloader;

    private ComparableProtocolVersion nativeProtocolVersion;
    private ComparableProtocolVersion targetProtocolVersion;

    public ViaLoadingBase(LinkedList<SubPlatform> subPlatforms, File runDirectory, int nativeVersion, BooleanSupplier singlePlayerProvider, EventLoop eventLoop, Supplier<JsonObject> dumpCreator, Consumer<ViaProviders> viaProviderCreator, Consumer<ViaManagerImpl.ViaManagerBuilder> viaManagerBuilderCreator, Consumer<ComparableProtocolVersion> protocolReloader) {
        this.subPlatforms = subPlatforms;

        this.runDirectory = new File(runDirectory, "ViaLoadingBase");
        this.nativeVersion = nativeVersion;
        this.singlePlayerProvider = singlePlayerProvider;
        this.eventLoop = eventLoop;
        this.dumpCreator = dumpCreator;
        this.viaProviderCreator = viaProviderCreator;
        this.viaManagerBuilderCreator = viaManagerBuilderCreator;
        this.protocolReloader = protocolReloader;

        classWrapper = this;
        initPlatform();
    }

    public static ComparableProtocolVersion getTargetVersion() {
        if (classWrapper == null) return new ComparableProtocolVersion(ProtocolVersion.unknown.getVersion(), ProtocolVersion.unknown.getName(), 0);
        if (classWrapper.singlePlayerProvider.getAsBoolean()) return classWrapper.nativeProtocolVersion;
        return classWrapper.targetProtocolVersion;
    }

    public void reload(final ProtocolVersion protocolVersion) {
        this.targetProtocolVersion = InternalProtocolList.fromProtocolVersion(protocolVersion);
        if (this.protocolReloader != null) {
            this.protocolReloader.accept(targetProtocolVersion);
        }
    }

    public void initPlatform() {
        for (SubPlatform subPlatform : subPlatforms) {
            subPlatform.createProtocolPath();
            ViaLoadingBase.LOGGER.info("Created protocol path for: " + subPlatform.getName());
        }
        InternalProtocolList.createComparableTable();
        this.nativeProtocolVersion = InternalProtocolList.fromProtocolVersion(ProtocolVersion.getProtocol(this.nativeVersion));
        this.targetProtocolVersion = this.nativeProtocolVersion;

        final ViaVersionPlatformImpl viaVersionPlatform = new ViaVersionPlatformImpl(ViaLoadingBase.LOGGER);
        final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new CustomViaInjector()).loader(new CustomViaProviders()).platform(viaVersionPlatform);
        if (this.viaManagerBuilderCreator != null) {
            this.viaManagerBuilderCreator.accept(builder);
        }
        Via.init(builder.build());

        final ViaManagerImpl viaManager = (ViaManagerImpl) Via.getManager();
        viaManager.addEnableListener(() -> {
            for (SubPlatform subPlatform : this.subPlatforms)
                if (subPlatform.build(ViaLoadingBase.LOGGER)) ViaMetrics.CLASS_WRAPPER.platformsLoaded++;
        });
        MappingDataLoader.enableMappingsCache();

        viaManager.init();
        viaManager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
        viaManager.getProtocolManager().setMaxPathDeltaIncrease(-1);
        ((ProtocolManagerImpl) viaManager.getProtocolManager()).refreshVersions();

        ViaMetrics.CLASS_WRAPPER.print(this.subPlatforms);
    }

    public List<SubPlatform> getSubPlatforms() {
        return subPlatforms;
    }

    public File getRunDirectory() {
        return runDirectory;
    }

    public int getNativeVersion() {
        return nativeVersion;
    }

    public BooleanSupplier getSinglePlayerProvider() {
        return singlePlayerProvider;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public Supplier<JsonObject> getDumpCreator() {
        return dumpCreator;
    }

    public Consumer<ViaProviders> getViaProviderCreator() {
        return viaProviderCreator;
    }

    public Consumer<ViaManagerImpl.ViaManagerBuilder> getViaManagerBuilderCreator() {
        return viaManagerBuilderCreator;
    }

    public static ViaLoadingBase getClassWrapper() {
        return classWrapper;
    }

    public static class ViaMetrics {
        public static final ViaMetrics CLASS_WRAPPER = new ViaMetrics();
        public int platformsLoaded;

        public void print(final LinkedList<SubPlatform> subPlatforms) {
            ViaLoadingBase.LOGGER.info("ViaLoadingBase has loaded " + platformsLoaded + "/" + subPlatforms.size());
        }
    }

    public static class ViaLoadingBaseBuilder {
        private final LinkedList<SubPlatform> subPlatforms = new LinkedList<>();

        private File runDirectory;
        private Integer nativeVersion;
        private BooleanSupplier singlePlayerProvider;
        private EventLoop eventLoop;
        private Supplier<JsonObject> dumpCreator;
        private Consumer<ViaProviders> viaProviderCreator;
        private Consumer<ViaManagerImpl.ViaManagerBuilder> viaManagerBuilderCreator;
        private Consumer<ComparableProtocolVersion> protocolReloader;

        public ViaLoadingBaseBuilder() {
            subPlatforms.add(PSEUDO_VIA_VERSION);

            subPlatforms.add(SUB_PLATFORM_VIA_BACKWARDS);
            subPlatforms.add(SUB_PLATFORM_VIA_REWIND);
        }

        public static ViaLoadingBaseBuilder create() {
            return new ViaLoadingBaseBuilder();
        }

        public ViaLoadingBaseBuilder subPlatformFirst(final SubPlatform subPlatform) {
            return subPlatform(subPlatform, 0);
        }

        public ViaLoadingBaseBuilder subPlatform(final SubPlatform subPlatform) {
            this.subPlatforms.add(subPlatform);
            return this;
        }

        public ViaLoadingBaseBuilder subPlatform(final SubPlatform subPlatform, final int position) {
            this.subPlatforms.add(position, subPlatform);
            return this;
        }

        public ViaLoadingBaseBuilder runDirectory(final File runDirectory) {
            this.runDirectory = runDirectory;
            return this;
        }

        public ViaLoadingBaseBuilder nativeVersion(final int nativeVersion) {
            this.nativeVersion = nativeVersion;
            return this;
        }

        public ViaLoadingBaseBuilder singlePlayerProvider(final BooleanSupplier singlePlayerProvider) {
            this.singlePlayerProvider = singlePlayerProvider;
            return this;
        }

        public ViaLoadingBaseBuilder eventLoop(final EventLoop eventLoop) {
            this.eventLoop = eventLoop;
            return this;
        }

        public ViaLoadingBaseBuilder dumpCreator(final Supplier<JsonObject> dumpCreator) {
            this.dumpCreator = dumpCreator;
            return this;
        }

        public ViaLoadingBaseBuilder viaProviderCreator(final Consumer<ViaProviders> viaProviderCreator) {
            this.viaProviderCreator = viaProviderCreator;
            return this;
        }

        public ViaLoadingBaseBuilder viaManagerBuilderCreator(final Consumer<ViaManagerImpl.ViaManagerBuilder> viaManagerBuilderCreator) {
            this.viaManagerBuilderCreator = viaManagerBuilderCreator;
            return this;
        }

        public ViaLoadingBaseBuilder protocolReloader(final Consumer<ComparableProtocolVersion> protocolReloader) {
            this.protocolReloader = protocolReloader;
            return this;
        }

        public void build() {
            if (ViaLoadingBase.getClassWrapper() != null) {
                ViaLoadingBase.LOGGER.severe("ViaLoadingBase has already started the platform!");
                return;
            }
            if (runDirectory == null || nativeVersion == null || singlePlayerProvider == null || eventLoop == null) {
                ViaLoadingBase.LOGGER.severe("Please check your ViaLoadingBaseBuilder arguments!");
                return;
            }
            new ViaLoadingBase(subPlatforms, runDirectory, nativeVersion, singlePlayerProvider, eventLoop, dumpCreator, viaProviderCreator, viaManagerBuilderCreator, protocolReloader);
        }
    }
}
