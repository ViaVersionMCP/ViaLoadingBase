package de.florianmichael.vialoadingbase.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.viaversion.CustomViaAPIWrapper;
import de.florianmichael.vialoadingbase.platform.viaversion.CustomViaConfig;
import de.florianmichael.vialoadingbase.util.FutureTaskId;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ViaVersionPlatformImpl implements ViaPlatform<UUID> {

    private final ViaAPI<UUID> api = new CustomViaAPIWrapper();

    private final Logger logger;
    private final CustomViaConfig config;

    public ViaVersionPlatformImpl(final Logger logger) {
        this.logger = logger;
        config = new CustomViaConfig(new File(ViaLoadingBase.instance().directory(), "viaversion.yml"));
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
                .runAsync(runnable, ViaLoadingBase.instance().executorService())
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
        return new FutureTaskId(ViaLoadingBase.instance().getEventLoop()
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
        return new FutureTaskId(ViaLoadingBase.instance().getEventLoop()
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
        return new FutureTaskId(ViaLoadingBase.instance().getEventLoop()
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
        return ViaLoadingBase.instance().directory();
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
        return "1.3.3.7";
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
        return ViaLoadingBase.instance().provider().createDump();
    }
}
