package de.florianmichael.vialoadingbase.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ComparableProtocolVersion extends ProtocolVersion {
    private final int index;

    public ComparableProtocolVersion(final int version, final String name, final int index) {
        super(version, name);
        this.index = index;
    }

    public boolean isOlderThan(final ProtocolVersion other) {
        return this.getIndex() > InternalProtocolList.fromProtocolVersion(other).getIndex();
    }

    public boolean isOlderThanOrEqualTo(final ProtocolVersion other) {
        return this.getIndex() >= InternalProtocolList.fromProtocolVersion(other).getIndex();
    }

    public boolean isNewerThan(final ProtocolVersion other) {
        return this.getIndex() < InternalProtocolList.fromProtocolVersion(other).getIndex();
    }

    public boolean isNewerThanOrEqualTo(final ProtocolVersion other) {
        return this.getIndex() <= InternalProtocolList.fromProtocolVersion(other).getIndex();
    }

    public int getIndex() {
        return index;
    }
}
