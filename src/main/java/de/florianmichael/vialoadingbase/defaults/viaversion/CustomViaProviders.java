package de.florianmichael.vialoadingbase.defaults.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class CustomViaProviders implements ViaPlatformLoader {

    @Override
    public void load() {
        // Now, we can implement custom providers
        final ViaProviders providers = Via.getManager().getProviders();
        providers.use(VersionProvider.class, new BaseVersionProvider() {

            @Override
            public int getClosestServerProtocol(UserConnection connection) throws Exception {
                if (connection.isClientSide()) {
                    return ViaLoadingBase.getClassWrapper().getTargetVersion().getVersion();
                }
                return super.getClosestServerProtocol(connection);
            }
        });
        providers.use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());

        if (ViaLoadingBase.getClassWrapper().getProviders() != null) ViaLoadingBase.getClassWrapper().getProviders().accept(providers);
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
