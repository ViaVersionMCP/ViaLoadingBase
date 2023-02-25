package de.florianmichael.vialoadingbase.defaults;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.defaults.viaversion.CustomViaAPIWrapper;
import de.florianmichael.vialoadingbase.defaults.viaversion.CustomViaConfig;
import de.florianmichael.vialoadingbase.util.FutureTaskId;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ViaVersionPlatformImpl implements ViaPlatform<UUID> {

    private final ViaAPI<UUID> api = new CustomViaAPIWrapper();

    private final Logger logger;
    private final CustomViaConfig config;

    public ViaVersionPlatformImpl(final Logger logger) {
        this.logger = logger;
        config = new CustomViaConfig(new File(ViaLoadingBase.getClassWrapper().getRunDirectory(), "viaversion.yml"));
    }

    public static List<ProtocolVersion> createVersionList() {
        final List<ProtocolVersion> versions = new ArrayList<>(ProtocolVersion.getProtocols()).stream().filter(protocolVersion -> protocolVersion != ProtocolVersion.unknown && ProtocolVersion.getProtocols().indexOf(protocolVersion) >= 7).collect(Collectors.toList());
        Collections.reverse(versions);
        return versions;
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        return new ViaCommandSender[0];
    }

    @Override
    public void sendMessage(UUID uuid, String msg) {
        if (uuid == null) {
            this.getLogger().info(msg);
        } else {
            this.getLogger().info("[" + uuid + "] " + msg);
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return false;
    }

    @Override
    public boolean disconnect(UserConnection connection, String message) {
        return ViaPlatform.super.disconnect(connection, message);
    }

    @Override
    public FutureTaskId runAsync(Runnable runnable) {
        return new FutureTaskId(CompletableFuture
                .runAsync(runnable, ViaLoadingBase.EXECUTOR_SERVICE)
                .exceptionally(throwable -> {
                    if (!(throwable instanceof CancellationException)) {
                        throwable.printStackTrace();
                    }
                    return null;
                })
        );
    }

    @Override
    public FutureTaskId runSync(Runnable runnable) {
        return new FutureTaskId(ViaLoadingBase.getClassWrapper().getEventLoop()
                .submit(runnable)
                .addListener(future -> {
                    if (!future.isCancelled() && future.cause() != null) {
                        future.cause().printStackTrace();
                    }
                })
        );
    }

    @Override
    public FutureTaskId runSync(Runnable runnable, long ticks) {
        return new FutureTaskId(ViaLoadingBase.getClassWrapper().getEventLoop()
                .schedule(() -> runSync(runnable), ticks * 50, TimeUnit.MILLISECONDS)
                .addListener(future -> {
                    if (!future.isCancelled() && future.cause() != null) {
                        future.cause().printStackTrace();
                    }
                })
        );
    }

    @Override
    public FutureTaskId runRepeatingSync(Runnable runnable, long ticks) {
        return new FutureTaskId(ViaLoadingBase.getClassWrapper().getEventLoop()
                .scheduleAtFixedRate(runnable, 0, ticks * 50, TimeUnit.MILLISECONDS)
                .addListener(future -> {
                    if (!future.isCancelled() && future.cause() != null) {
                        future.cause().printStackTrace();
                    }
                })
        );
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    public void onReload() {
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public ViaVersionConfig getConf() {
        return config;
    }

    @Override
    public ViaAPI<UUID> getApi() {
        return api;
    }

    @Override
    public File getDataFolder() {
        return ViaLoadingBase.getClassWrapper().getRunDirectory();
    }

    @Override
    public String getPluginVersion() {
        return "4.5.2-SNAPSHOT";
    }

    @Override
    public String getPlatformName() {
        return "ViaLoadingBase by FlorianMichael";
    }

    @Override
    public String getPlatformVersion() {
        return ViaLoadingBase.VERSION;
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return config;
    }

    @Override
    public boolean isOldClientsAllowed() {
        return true;
    }

    @Override
    public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return ViaPlatform.super.getUnsupportedSoftwareClasses();
    }

    @Override
    public boolean hasPlugin(String s) {
        return false;
    }

    @Override
    public JsonObject getDump() {
        if (ViaLoadingBase.getClassWrapper().getDumpSupplier() == null) return new JsonObject();

        return ViaLoadingBase.getClassWrapper().getDumpSupplier().get();
    }
}
