# ViaProtocolHack
Universal ViaVersion standalone implementation

## Projects where this is used:
1. ViaForge: Clientside ViaVersion for Forge

## Add this to your own project
build.gradle
```groovy
repositories {
    maven {
        url = "https://repo.viaversion.com/"
    }
    maven {
        url = "https://jitpack.io/"
    }
}

var libs = [
        // ViaVersion -> https://github.com/ViaVersion/ViaVersion
        "com.viaversion:viaversion:4.5.0-22w44a-SNAPSHOT",

        // ViaBackwards -> https://github.com/ViaVersion/ViaBackwards
        "com.viaversion:viabackwards-common:4.5.0-22w44a-SNAPSHOT",

        // ViaRewind -> https://github.com/ViaVersion/ViaRewind
        "com.viaversion:viarewind-core:2.0.3-SNAPSHOT",
        
        // ViaProtocolHack -> https://github.com/RejectedVia/ViaProtocolHack
        "com.github.RejectedVia:ViaProtocolHack:22w44a"
]

dependencies {
    for (final def via in libs) {
        implementation(via)
    }
}
```
I will always update the versions, above you can just re-copy it if a new Minecraft version releases: <br>
Current Version: `Minecraft 22w44a`

## Example implementation:
```java
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import de.florianmichael.viaprotocolhack.INativeProvider;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Test implements INativeProvider {

    public void main() throws Exception {
        ViaProtocolHack.instance().init(this, () -> {
            System.out.println("ViaProtocolHack is finished");
        });
    }

    @Override
    public boolean isSinglePlayer() {
        return Minecraft.getMinecraft().isInSingleplayer(); // for VersionList
    }

    @Override
    public int nativeVersion() {
        return 47; // native Version of your Client
    }
    
    @Override
    public int realClientsideVersion() {
        return 47; // the target version you want to connect
    }

    @Override
    public String[] nettyOrder() {
        return new String[] {
                "compress",
                "decompress"
        }; // namings of minecrafts compressing and decompressing from the pipeline
    }

    @Override
    public File run() {
        return Minecraft.getMinecraft().mcDataDir; // data dir for via
    }

    @Override
    public JsonObject createDump() {
        return new JsonObject(); // not important since commands aren't implemented by default
    }

    @Override // default: BasicVersionProvider and completely unlegit Movement Transmitter by Via TM 
    public void createProviders(ViaProviders providers) {
        super.createProviders(providers);
    }

    @Override
    public void onBuildViaPlatform(ViaManagerImpl.ViaManagerBuilder builder) {
    }
    
    @Override // default: null
    public List<ProtocolVersion> getOptionalProtocols() {
        return null; // in case you want custom protocols like 1.7
    }
}
```
