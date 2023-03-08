/*
 * This file is part of ViaLoadingBase - https://github.com/FlorianMichael/ViaLoadingBase
 * Copyright (C) 2022-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

public class VLBViaProviders implements ViaPlatformLoader {

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
