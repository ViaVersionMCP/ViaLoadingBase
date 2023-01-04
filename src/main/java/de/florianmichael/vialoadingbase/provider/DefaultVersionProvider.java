package de.florianmichael.vialoadingbase.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class DefaultVersionProvider extends BaseVersionProvider {

    public int getVersion() {
        return ViaLoadingBase.instance().provider().getClientsideVersion();
    }

    @Override
    public int getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide()) {
            return this.getVersion();
        }
        return super.getClosestServerProtocol(connection);
    }
}

