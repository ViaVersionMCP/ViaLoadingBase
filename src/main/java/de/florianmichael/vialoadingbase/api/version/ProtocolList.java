package de.florianmichael.vialoadingbase.api.version;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

import java.util.*;

public class ProtocolList {
    private final static Map<ProtocolVersion, ComparableProtocolVersion> PROTOCOLS = new LinkedHashMap<>();

    public static void load(final List<ProtocolVersion> protocolVersions) {
        for (ProtocolVersion protocolVersion : protocolVersions) {
            PROTOCOLS.put(protocolVersion, new ComparableProtocolVersion(protocolVersion.getOriginalVersion(), protocolVersion.getName(), protocolVersions.indexOf(protocolVersion)));
        }
        ViaLoadingBase.LOGGER.info("Loaded ViaVersion protocols...");
    }

    public static ComparableProtocolVersion fromProtocolVersion(final ProtocolVersion protocolVersion) {
        return PROTOCOLS.get(protocolVersion);
    }

    public static List<ProtocolVersion> getProtocols() {
        return new LinkedList<>(PROTOCOLS.keySet());
    }
}
