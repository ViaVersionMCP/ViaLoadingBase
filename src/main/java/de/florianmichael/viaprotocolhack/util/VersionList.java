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

    private static final Map<ProtocolVersion, String> SPECIAL_NAMES = new HashMap<>();
    private static final List<ProtocolVersion> PROTOCOLS = new LinkedList<>();

    public VersionList() {
        SPECIAL_NAMES.put(ProtocolVersion.v1_9_3, "1.9.3-1.9.4");
        SPECIAL_NAMES.put(ProtocolVersion.v1_11_1, "1.11.1-1.11.2");
        SPECIAL_NAMES.put(ProtocolVersion.v1_16_4, "1.16.4-1.16.5");
        SPECIAL_NAMES.put(ProtocolVersion.v1_18, "1.18-1.18.1");
        SPECIAL_NAMES.put(ProtocolVersion.v1_19_1, "1.19.1-1.19.2");
        SPECIAL_NAMES.put(ProtocolVersion.v1_19_3, "22w44a");
    }

    public static void registerProtocols() throws IllegalAccessException {
        for (Field declaredField : ProtocolVersion.class.getDeclaredFields()) {
            if (declaredField.get(null) instanceof ProtocolVersion) {
                declaredField.setAccessible(true);
                PROTOCOLS.add((ProtocolVersion) declaredField.get(null));
            }
        }
    }

    public static String formatProtocolName(final ProtocolVersion version) {
        if (SPECIAL_NAMES.containsKey(version)) {
            return SPECIAL_NAMES.get(version);
        }

        return version.getName();
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
        final List<ProtocolVersion> optionalVersions = ViaProtocolHack.instance().provider().getOptionalProtocols();

        if (optionalVersions != null) {
            versions.addAll(optionalVersions);
        }
        return versions;
    }
}
