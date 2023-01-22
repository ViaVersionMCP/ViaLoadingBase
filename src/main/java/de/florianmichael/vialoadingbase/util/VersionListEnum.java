/**
 * @author RaphiMC as RK_01
 */
package de.florianmichael.vialoadingbase.util;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionRange;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

import java.util.*;

public enum VersionListEnum {

    c0_0_15a_1(getLegacyProtocol("c0_0_15a_1")),
    c0_0_16a_02(getLegacyProtocol("c0_0_16a_02")),
    c0_0_18a_02(getLegacyProtocol("c0_0_18a_02")),
    c0_0_19a_06(getLegacyProtocol("c0_0_19a_06")),
    c0_0_20ac0_27(getLegacyProtocol("c0_0_20ac0_27")),
    c0_30cpe(getLegacyProtocol("c0_30cpe")),
    c0_28toc0_30(getLegacyProtocol("c0_28toc0_30")),
    a1_0_15(getLegacyProtocol("a1_0_15")),
    a1_0_16toa1_0_16_2(getLegacyProtocol("a1_0_16toa1_0_16_2")),
    a1_0_17toa1_0_17_4(getLegacyProtocol("a1_0_17toa1_0_17_4")),
    a1_1_0toa1_1_2_1(getLegacyProtocol("a1_1_0toa1_1_2_1")),
    a1_2_0toa1_2_1_1(getLegacyProtocol("a1_2_0toa1_2_1_1")),
    a1_2_2(getLegacyProtocol("a1_2_2")),
    a1_2_3toa1_2_3_4(getLegacyProtocol("a1_2_3toa1_2_3_4")),
    a1_2_3_5toa1_2_6(getLegacyProtocol("a1_2_3_5toa1_2_6")),
    b1_0tob1_1_1(getLegacyProtocol("b1_0tob1_1_1")),
    b1_1_2(getLegacyProtocol("b1_1_2")),
    b1_2_0tob1_2_2(getLegacyProtocol("b1_2_0tob1_2_2")),
    b1_3tob1_3_1(getLegacyProtocol("b1_3tob1_3_1")),
    b1_4tob1_4_1(getLegacyProtocol("b1_4tob1_4_1")),
    b1_5tob1_5_2(getLegacyProtocol("b1_5tob1_5_2")),
    b1_6tob1_6_6(getLegacyProtocol("b1_6tob1_6_6")),
    b1_7tob1_7_3(getLegacyProtocol("b1_7tob1_7_3")),
    b1_8tob1_8_1(getLegacyProtocol("b1_8tob1_8_1")),
    r1_0_0tor1_0_1(getLegacyProtocol("r1_0_0tor1_0_1")),
    r1_1(getLegacyProtocol("r1_1")),
    r1_2_1tor1_2_3(getLegacyProtocol("r1_2_1tor1_2_3")),
    r1_2_4tor1_2_5(getLegacyProtocol("r1_2_4tor1_2_5")),
    r1_3_1tor1_3_2(getLegacyProtocol("r1_3_1tor1_3_2")),
    r1_4_2(getLegacyProtocol("r1_4_2")),
    r1_4_4tor1_4_5(getLegacyProtocol("r1_4_4tor1_4_5")),
    r1_4_6tor1_4_7(getLegacyProtocol("r1_4_6tor1_4_7")),
    r1_5tor1_5_1(getLegacyProtocol("r1_5tor1_5_1")),
    r1_5_2(getLegacyProtocol("r1_5_2")),
    r1_6_1(getLegacyProtocol("r1_6_1")),
    r1_6_2(getLegacyProtocol("r1_6_2")),
    r1_6_3_pre(getLegacyProtocol("r1_6_3_pre")),
    r1_6_4(getLegacyProtocol("r1_6_4")),
    r1_7_2tor1_7_5(ProtocolVersion.v1_7_1),
    r1_7_6tor1_7_10(ProtocolVersion.v1_7_6),
    r1_8(ProtocolVersion.v1_8),
    r1_9(ProtocolVersion.v1_9),
    r1_9_1(ProtocolVersion.v1_9_1),
    r1_9_2(ProtocolVersion.v1_9_2),
    r1_9_3tor1_9_4(ProtocolVersion.v1_9_3),
    r1_10(ProtocolVersion.v1_10),
    r1_11(ProtocolVersion.v1_11),
    r1_11_1to1_11_2(ProtocolVersion.v1_11_1),
    r1_12(ProtocolVersion.v1_12),
    r1_12_1(ProtocolVersion.v1_12_1),
    r1_12_2(ProtocolVersion.v1_12_2),
    r1_13(ProtocolVersion.v1_13),
    r1_13_1(ProtocolVersion.v1_13_1),
    r1_13_2(ProtocolVersion.v1_13_2),
    s3d_shareware(getSnapshotProtocol("s3d_shareware")),
    r1_14(ProtocolVersion.v1_14),
    r1_14_1(ProtocolVersion.v1_14_1),
    r1_14_2(ProtocolVersion.v1_14_2),
    r1_14_3(ProtocolVersion.v1_14_3),
    r1_14_4(ProtocolVersion.v1_14_4),
    r1_15(ProtocolVersion.v1_15),
    r1_15_1(ProtocolVersion.v1_15_1),
    r1_15_2(ProtocolVersion.v1_15_2),
    s20w14infinite(getSnapshotProtocol("s20w14infinite")),
    r1_16(ProtocolVersion.v1_16),
    r1_16_1(ProtocolVersion.v1_16_1),
    sCombatTest8c(getSnapshotProtocol("sCombatTest8C")),
    r1_16_2(ProtocolVersion.v1_16_2),
    r1_16_3(ProtocolVersion.v1_16_3),
    r1_16_4tor1_16_5(ProtocolVersion.v1_16_4),
    r1_17(ProtocolVersion.v1_17),
    r1_17_1(ProtocolVersion.v1_17_1),
    r1_18tor1_18_1(ProtocolVersion.v1_18),
    r1_18_2(ProtocolVersion.v1_18_2),
    r1_19(ProtocolVersion.v1_19),
    r1_19_1tor1_19_2(ProtocolVersion.v1_19_1),
    r1_19_3(ProtocolVersion.v1_19_3),
    r1_19_4(ProtocolVersion.v1_19_4),
    rBedrock1_19_51(getSnapshotProtocol("rBedrock1_19_51")),

    //
    UNKNOWN(ProtocolVersion.unknown), // Not in Registry
    ;


    private static final Map<ProtocolVersion, VersionListEnum> VERSION_REGISTRY = new LinkedHashMap<>();
    public static final List<VersionListEnum> RENDER_VERSIONS = new LinkedList<>();
    public static final List<VersionListEnum> LEGACY_VERSIONS = new LinkedList<>();
    public static final List<VersionListEnum> OFFICIAL_SUPPORTED_PROTOCOLS = new LinkedList<>();

    private final static Map<String, String> SPECIAL_NAMES = new HashMap<String, String>() {
        {
            put("1.7-1.7.5", "1.7.2-1.7.5");
            put("1.9.3/4", "1.9.3-1.9.4");
            put("1.11.1/2", "1.11.1-1.11.2");
            put("1.16.4/5", "1.16.4-1.16.5");
            put("1.18/1.18.1", "1.18-1.18.1");
            put("1.19.1/2", "1.19.1-1.19.2");
            put("1.19.4", "22w03a");
        }
    };

    static {
        for (VersionListEnum version : VersionListEnum.values()) {
            if (version == UNKNOWN) continue;
            VERSION_REGISTRY.put(version.getProtocol(), version);
        }

        for (VersionListEnum version : VersionListEnum.getAllVersions()) {
            if (version.isOlderThan(VersionListEnum.r1_7_2tor1_7_5)) {
                LEGACY_VERSIONS.add(version);
            }
        }
        Collections.reverse(LEGACY_VERSIONS);

        for (VersionListEnum version : VersionListEnum.getAllVersions()) {
            if (version.isNewerThan(VersionListEnum.r1_6_4) && version != VersionListEnum.s3d_shareware && version != VersionListEnum.s20w14infinite && version != VersionListEnum.sCombatTest8c && version != VersionListEnum.rBedrock1_19_51) {
                OFFICIAL_SUPPORTED_PROTOCOLS.add(version);
            }
        }
        Collections.reverse(OFFICIAL_SUPPORTED_PROTOCOLS);

        RENDER_VERSIONS.add(rBedrock1_19_51);
        RENDER_VERSIONS.add(r1_19_4);
        RENDER_VERSIONS.add(r1_19_3);
        RENDER_VERSIONS.add(r1_19_1tor1_19_2);
        RENDER_VERSIONS.add(r1_19);
        RENDER_VERSIONS.add(r1_18_2);
        RENDER_VERSIONS.add(r1_18tor1_18_1);
        RENDER_VERSIONS.add(r1_17_1);
        RENDER_VERSIONS.add(r1_17);
        RENDER_VERSIONS.add(r1_16_4tor1_16_5);
        RENDER_VERSIONS.add(r1_16_3);
        RENDER_VERSIONS.add(r1_16_2);
        RENDER_VERSIONS.add(sCombatTest8c);
        RENDER_VERSIONS.add(r1_16_1);
        RENDER_VERSIONS.add(r1_16);
        RENDER_VERSIONS.add(s20w14infinite);
        RENDER_VERSIONS.add(r1_15_2);
        RENDER_VERSIONS.add(r1_15_1);
        RENDER_VERSIONS.add(r1_15);
        RENDER_VERSIONS.add(r1_14_4);
        RENDER_VERSIONS.add(r1_14_3);
        RENDER_VERSIONS.add(r1_14_2);
        RENDER_VERSIONS.add(r1_14_1);
        RENDER_VERSIONS.add(r1_14);
        RENDER_VERSIONS.add(s3d_shareware);
        RENDER_VERSIONS.add(r1_13_2);
        RENDER_VERSIONS.add(r1_13_1);
        RENDER_VERSIONS.add(r1_13);
        RENDER_VERSIONS.add(r1_12_2);
        RENDER_VERSIONS.add(r1_12_1);
        RENDER_VERSIONS.add(r1_12);
        RENDER_VERSIONS.add(r1_11_1to1_11_2);
        RENDER_VERSIONS.add(r1_11);
        RENDER_VERSIONS.add(r1_10);
        RENDER_VERSIONS.add(r1_9_3tor1_9_4);
        RENDER_VERSIONS.add(r1_9_2);
        RENDER_VERSIONS.add(r1_9_1);
        RENDER_VERSIONS.add(r1_9);
        RENDER_VERSIONS.add(r1_8);
        RENDER_VERSIONS.add(r1_7_6tor1_7_10);
        RENDER_VERSIONS.add(r1_7_2tor1_7_5);
        RENDER_VERSIONS.add(r1_6_4);
        RENDER_VERSIONS.add(r1_6_3_pre);
        RENDER_VERSIONS.add(r1_6_2);
        RENDER_VERSIONS.add(r1_6_1);
        RENDER_VERSIONS.add(r1_5_2);
        RENDER_VERSIONS.add(r1_5tor1_5_1);
        RENDER_VERSIONS.add(r1_4_6tor1_4_7);
        RENDER_VERSIONS.add(r1_4_4tor1_4_5);
        RENDER_VERSIONS.add(r1_4_2);
        RENDER_VERSIONS.add(r1_3_1tor1_3_2);
        RENDER_VERSIONS.add(r1_2_4tor1_2_5);
        RENDER_VERSIONS.add(r1_2_1tor1_2_3);
        RENDER_VERSIONS.add(r1_1);
        RENDER_VERSIONS.add(r1_0_0tor1_0_1);
        RENDER_VERSIONS.add(b1_8tob1_8_1);
        RENDER_VERSIONS.add(b1_7tob1_7_3);
        RENDER_VERSIONS.add(b1_6tob1_6_6);
        RENDER_VERSIONS.add(b1_5tob1_5_2);
        RENDER_VERSIONS.add(b1_4tob1_4_1);
        RENDER_VERSIONS.add(b1_3tob1_3_1);
        RENDER_VERSIONS.add(b1_2_0tob1_2_2);
        RENDER_VERSIONS.add(b1_1_2);
        RENDER_VERSIONS.add(b1_0tob1_1_1);
        RENDER_VERSIONS.add(a1_2_3_5toa1_2_6);
        RENDER_VERSIONS.add(a1_2_3toa1_2_3_4);
        RENDER_VERSIONS.add(a1_2_2);
        RENDER_VERSIONS.add(a1_2_0toa1_2_1_1);
        RENDER_VERSIONS.add(a1_1_0toa1_1_2_1);
        RENDER_VERSIONS.add(a1_0_17toa1_0_17_4);
        RENDER_VERSIONS.add(a1_0_16toa1_0_16_2);
        RENDER_VERSIONS.add(a1_0_15);
        RENDER_VERSIONS.add(c0_28toc0_30);
        RENDER_VERSIONS.add(c0_30cpe);
        RENDER_VERSIONS.add(c0_0_20ac0_27);
        RENDER_VERSIONS.add(c0_0_19a_06);
        RENDER_VERSIONS.add(c0_0_18a_02);
        RENDER_VERSIONS.add(c0_0_16a_02);
        RENDER_VERSIONS.add(c0_0_15a_1);
    }

    public static VersionListEnum fromProtocolVersion(final ProtocolVersion protocolVersion) {
        if (!protocolVersion.isKnown()) return UNKNOWN;
        return VERSION_REGISTRY.getOrDefault(protocolVersion, UNKNOWN);
    }

    public static VersionListEnum fromProtocolId(final int protocolId) {
        return fromProtocolVersion(ProtocolVersion.getProtocol(protocolId));
    }

    public static VersionListEnum fromUserConnection(final UserConnection userConnection) {
        return fromUserConnection(userConnection, true);
    }

    public static VersionListEnum fromUserConnection(final UserConnection userConnection, final boolean serverProtocol) {
        return fromProtocolId(serverProtocol ? userConnection.getProtocolInfo().getServerProtocolVersion() : userConnection.getProtocolInfo().getProtocolVersion());
    }

    public static Collection<VersionListEnum> getAllVersions() {
        return VERSION_REGISTRY.values();
    }

    public static ProtocolVersion getLegacyProtocol(final String name) {
        try {
            return (ProtocolVersion) Class.forName(ViaLoadingBase.instance().provider().getLegacyProtocolImplementationClass()).getField(name).get(null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            return ProtocolVersion.unknown;
        }
    }

    public static ProtocolVersion getSnapshotProtocol(final String name) {
        try {
            return (ProtocolVersion) Class.forName(ViaLoadingBase.instance().provider().getSnapshotProtocolImplementationClass()).getField(name).get(null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            return ProtocolVersion.unknown;
        }
    }

    private final ProtocolVersion protocolVersion;

    VersionListEnum(final int version, final String name) {
        this(ProtocolVersion.register(version, name));
    }

    VersionListEnum(final int version, final String name, final VersionRange versionRange) {
        this(ProtocolVersion.register(version, name, versionRange));
    }

    VersionListEnum(final ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public ProtocolVersion getProtocol() {
        return this.protocolVersion;
    }

    public String getName() {
        String name = this.protocolVersion.getName();
        if (SPECIAL_NAMES.containsKey(name)) {
            name = SPECIAL_NAMES.get(name);
        }
        return name;
    }

    public int getVersion() {
        return this.protocolVersion.getVersion();
    }

    public int getOriginalVersion() {
        return this.protocolVersion.getOriginalVersion();
    }

    public boolean isOlderThan(final VersionListEnum other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isOlderThanOrEqualTo(final VersionListEnum other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isNewerThan(final VersionListEnum other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isNewerThanOrEqualTo(final VersionListEnum other) {
        return this.ordinal() >= other.ordinal();
    }

    public boolean isBetweenInclusive(final VersionListEnum min, final VersionListEnum max) {
        return this.isNewerThanOrEqualTo(min) && this.isOlderThanOrEqualTo(max);
    }

    public boolean isBetweenExclusive(final VersionListEnum min, final VersionListEnum max) {
        return this.isNewerThan(min) && this.isOlderThan(max);
    }
}
