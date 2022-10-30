package de.florianmichael.viaprotocolhack.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

public class DefaultVersionProvider extends BaseVersionProvider {

    public int getVersion() {
        return ViaProtocolHack.instance().provider().getClientsideVersion();
    }

    @Override
    public int getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide()) {
            return this.getVersion();
        }
        return super.getClosestServerProtocol(connection);
    }
}

