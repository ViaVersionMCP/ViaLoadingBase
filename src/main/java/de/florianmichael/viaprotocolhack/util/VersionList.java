/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 1:27 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.viaprotocolhack.util;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.lang.reflect.Field;
import java.util.*;

public class VersionList {

    private static final List<ProtocolVersion> PROTOCOLS = new LinkedList<>();

    public static void registerProtocols() throws IllegalAccessException {
        int index = 0;
        for (Field declaredField : ProtocolVersion.class.getDeclaredFields()) {
            if (declaredField.getType() == ProtocolVersion.class) {
                index++;
                if (index > 9) {
                    ProtocolVersion protocolVersion = (ProtocolVersion) declaredField.get(null);
                    if (protocolVersion.isKnown()) {
                        PROTOCOLS.add(protocolVersion);
                    }
                }
            }
        }
        Collections.reverse(PROTOCOLS);
    }

    public static boolean isEqualTo(final ProtocolVersion protocolVersion) {
        return ViaProtocolHack.instance().provider().getClientsideVersion() == protocolVersion.getVersion();
    }

    public static boolean isOlderOrEqualTo(final ProtocolVersion protocolVersion) {
        return ViaProtocolHack.instance().provider().getClientsideVersion() <= protocolVersion.getVersion();
    }

    public static boolean isOlderTo(final ProtocolVersion protocolVersion) {
        return ViaProtocolHack.instance().provider().getClientsideVersion() < protocolVersion.getVersion();
    }

    public static boolean isNewerTo(final ProtocolVersion protocolVersion) {
        return ViaProtocolHack.instance().provider().getClientsideVersion() > protocolVersion.getVersion();
    }

    public static boolean isNewerOrEqualTo(final ProtocolVersion protocolVersion) {
        return ViaProtocolHack.instance().provider().getClientsideVersion() >= protocolVersion.getVersion();
    }

    public static List<ProtocolVersion> getProtocols() {
        final List<ProtocolVersion> versions = new ArrayList<>(PROTOCOLS);
        for (ProtocolVersion protocolVersion : ViaProtocolHack.instance().provider().getOptionalProtocols()) {
            versions.removeIf(version -> version.getVersion() == protocolVersion.getVersion());
            versions.add(protocolVersion);
        }
        return versions;
    }
}
