package de.florianmichael.vialoadingbase.api.version;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import java.util.*;

public class InternalProtocolList {
    private final static Map<ProtocolVersion, ComparableProtocolVersion> PROTOCOLS = new LinkedHashMap<>();
    public final static List<ProtocolVersion> PRE_PROTOCOLS = new ArrayList<>();

    public static void createComparableTable() {
        for (ProtocolVersion preProtocol : PRE_PROTOCOLS) {
            PROTOCOLS.put(preProtocol, new ComparableProtocolVersion(preProtocol.getOriginalVersion(), preProtocol.getName(), PRE_PROTOCOLS.indexOf(preProtocol)));
        }
    }

    public static ComparableProtocolVersion fromProtocolVersion(final ProtocolVersion protocolVersion) {
        return PROTOCOLS.get(protocolVersion);
    }

    public static ProtocolVersion fromProtocolId(final int protocolId) {
        return getProtocols().stream().filter(protocol -> protocol.getOriginalVersion() == protocolId).findFirst().orElse(null);
    }

    public static List<ProtocolVersion> getProtocols() {
        return new LinkedList<>(PROTOCOLS.keySet());
    }
}
