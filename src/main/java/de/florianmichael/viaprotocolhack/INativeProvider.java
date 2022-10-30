package de.florianmichael.viaprotocolhack;

import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.libs.gson.JsonObject;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface INativeProvider {

    boolean isSinglePlayer();
    int nativeVersion();
    int realClientsideVersion();

    String[] nettyOrder();
    File run();
    JsonObject createDump();

    default void createProviders(final ViaProviders providers) {
    }
    default List<ProtocolVersion> getOptionalProtocols() {
        return null;
    }
    default void onBuildViaPlatform(ViaManagerImpl.ViaManagerBuilder builder) {
    }

    default int getClientsideVersion() {
        if (isSinglePlayer()) {
            return nativeVersion();
        }
        return realClientsideVersion();
    }
}
