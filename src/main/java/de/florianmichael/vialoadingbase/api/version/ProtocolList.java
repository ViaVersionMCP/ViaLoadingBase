package de.florianmichael.vialoadingbase.api.version;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProtocolList {
    private final static Map<ProtocolVersion, ComparableProtocolVersion> PROTOCOLS = new LinkedHashMap<>();

    public static void load(final List<ProtocolVersion> protocolVersions) {
        for (ProtocolVersion protocolVersion : protocolVersions) {
            PROTOCOLS.put(protocolVersion, new ComparableProtocolVersion(protocolVersion.getOriginalVersion(), protocolVersion.getName(), protocolVersions.indexOf(protocolVersion)));
        }
    }

    public static ComparableProtocolVersion fromProtocolVersion(final ProtocolVersion protocolVersion) {
        return PROTOCOLS.get(protocolVersion);
    }

    public static List<ProtocolVersion> getProtocols() {
        return new ArrayList<>(PROTOCOLS.keySet());
    }
}
