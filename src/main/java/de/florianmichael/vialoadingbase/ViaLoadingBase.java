package de.florianmichael.vialoadingbase;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import de.florianmichael.vialoadingbase.platform.SubPlatform;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import de.florianmichael.vialoadingbase.defaults.ViaBackwardsPlatformImpl;
import de.florianmichael.vialoadingbase.defaults.ViaRewindPlatformImpl;
import de.florianmichael.vialoadingbase.defaults.viaversion.VLBViaProviders;
import de.florianmichael.vialoadingbase.defaults.ViaVersionPlatformImpl;
import de.florianmichael.vialoadingbase.defaults.viaversion.VLBViaInjector;
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
    private final BooleanSupplier forceNativeVersionCondition;
    private final EventLoop eventLoop;
    private final Supplier<JsonObject> dumpSupplier;
    private final Consumer<ViaProviders> providers;
    private final Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer;
    private final Consumer<ComparableProtocolVersion> onProtocolReload;

    private ComparableProtocolVersion nativeProtocolVersion;
    private ComparableProtocolVersion targetProtocolVersion;

    public ViaLoadingBase(LinkedList<SubPlatform> subPlatforms, File runDirectory, int nativeVersion, BooleanSupplier forceNativeVersionCondition, EventLoop eventLoop, Supplier<JsonObject> dumpSupplier, Consumer<ViaProviders> providers, Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer, Consumer<ComparableProtocolVersion> onProtocolReload) {
        this.subPlatforms = subPlatforms;

        this.runDirectory = new File(runDirectory, "ViaLoadingBase");
        this.nativeVersion = nativeVersion;
        this.forceNativeVersionCondition = forceNativeVersionCondition;
        this.eventLoop = eventLoop;
        this.dumpSupplier = dumpSupplier;
        this.providers = providers;
        this.managerBuilderConsumer = managerBuilderConsumer;
        this.onProtocolReload = onProtocolReload;

        classWrapper = this;
        initPlatform();
    }

    public ComparableProtocolVersion getTargetVersion() {
        if (forceNativeVersionCondition.getAsBoolean()) return nativeProtocolVersion;

        return targetProtocolVersion;
    }

    public void reload(final ProtocolVersion protocolVersion) {
        this.targetProtocolVersion = InternalProtocolList.fromProtocolVersion(protocolVersion);
        if (this.onProtocolReload != null) {
            this.onProtocolReload.accept(targetProtocolVersion);
        }
    }

    public void initPlatform() {
        for (SubPlatform subPlatform : subPlatforms) subPlatform.createProtocolPath();
        InternalProtocolList.createComparableTable();
        this.nativeProtocolVersion = InternalProtocolList.fromProtocolVersion(ProtocolVersion.getProtocol(this.nativeVersion));
        this.targetProtocolVersion = this.nativeProtocolVersion;

        final ViaVersionPlatformImpl viaVersionPlatform = new ViaVersionPlatformImpl(ViaLoadingBase.LOGGER);
        final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().injector(new VLBViaInjector()).loader(new VLBViaProviders()).platform(viaVersionPlatform);
        if (this.managerBuilderConsumer != null) {
            this.managerBuilderConsumer.accept(builder);
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

    public BooleanSupplier getForceNativeVersionCondition() {
        return forceNativeVersionCondition;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public Supplier<JsonObject> getDumpSupplier() {
        return dumpSupplier;
    }

    public Consumer<ViaProviders> getProviders() {
        return providers;
    }

    public Consumer<ViaManagerImpl.ViaManagerBuilder> getManagerBuilderConsumer() {
        return managerBuilderConsumer;
    }

    public static ViaLoadingBase getClassWrapper() {
        return classWrapper;
    }

    public static class ViaMetrics {
        public static final ViaMetrics CLASS_WRAPPER = new ViaMetrics();
        public int platformsLoaded;

        public void print(final LinkedList<SubPlatform> subPlatforms) {
            ViaLoadingBase.LOGGER.info("ViaLoadingBase has loaded " + platformsLoaded + "/" + subPlatforms.size() + " sub platforms");
        }
    }

    public static class ViaLoadingBaseBuilder {
        private final LinkedList<SubPlatform> subPlatforms = new LinkedList<>();

        private File runDirectory;
        private Integer nativeVersion;
        private BooleanSupplier forceNativeVersionCondition;
        private EventLoop eventLoop;
        private Supplier<JsonObject> dumpSupplier;
        private Consumer<ViaProviders> providers;
        private Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer;
        private Consumer<ComparableProtocolVersion> onProtocolReload;

        public ViaLoadingBaseBuilder() {
            subPlatforms.add(PSEUDO_VIA_VERSION);

            subPlatforms.add(SUB_PLATFORM_VIA_BACKWARDS);
            subPlatforms.add(SUB_PLATFORM_VIA_REWIND);
        }

        public static ViaLoadingBaseBuilder create() {
            return new ViaLoadingBaseBuilder();
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

        public ViaLoadingBaseBuilder forceNativeVersionCondition(final BooleanSupplier forceNativeVersionCondition) {
            this.forceNativeVersionCondition = forceNativeVersionCondition;
            return this;
        }

        public ViaLoadingBaseBuilder eventLoop(final EventLoop eventLoop) {
            this.eventLoop = eventLoop;
            return this;
        }

        public ViaLoadingBaseBuilder dumpSupplier(final Supplier<JsonObject> dumpSupplier) {
            this.dumpSupplier = dumpSupplier;
            return this;
        }

        public ViaLoadingBaseBuilder providers(final Consumer<ViaProviders> providers) {
            this.providers = providers;
            return this;
        }

        public ViaLoadingBaseBuilder managerBuilderConsumer(final Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer) {
            this.managerBuilderConsumer = managerBuilderConsumer;
            return this;
        }

        public ViaLoadingBaseBuilder onProtocolReload(final Consumer<ComparableProtocolVersion> onProtocolReload) {
            this.onProtocolReload = onProtocolReload;
            return this;
        }

        public void build() {
            if (ViaLoadingBase.getClassWrapper() != null) {
                ViaLoadingBase.LOGGER.severe("ViaLoadingBase has already started the platform!");
                return;
            }
            if (runDirectory == null || nativeVersion == null || forceNativeVersionCondition == null || eventLoop == null) {
                ViaLoadingBase.LOGGER.severe("Please check your ViaLoadingBaseBuilder arguments!");
                return;
            }
            new ViaLoadingBase(subPlatforms, runDirectory, nativeVersion, forceNativeVersionCondition, eventLoop, dumpSupplier, providers, managerBuilderConsumer, onProtocolReload);
        }
    }
}
