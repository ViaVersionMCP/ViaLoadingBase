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

var viaLibs = [
        "com.viaversion:viaversion:latest.integration",
        "com.viaversion:viabackwards-common:latest.integration",
        "com.viaversion:viarewind-core:latest.integration",
        
        "com.github.RejectedVia:ViaProtocolHack:ac2c40ba18"
]

dependencies {
    for (final def via in viaLibs) {
        implementation(via)
    }
}
```
### Which library do I need?
If your platform is older than the latest Minecraft version, you need ViaVersion + ViaBackwards, if your platform is 1.8.x,
you need ViaVersion + ViaBackwards + ViaRewind, otherwise you only need ViaVersion: <br>

A `1.8.x` Minecraft client for example would need `ViaVersion + ViaBackwards + ViaRewind`. <br>
A `1.12.x` Minecraft client for example would need `ViaVersion + ViaBackwards`. <br>
A `1.19.x` Minecraft client, for example, would need `ViaVersion`. <br>

I will always update the versions, above you can just re-copy it if a new Minecraft version releases: <br>
Current Version: `Minecraft 22w45a`

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
                "decompress",
                "compress"
        }; // namings of Minecraft's compressing and decompressing from the pipeline
    }

    @Override
    public File run() {
        return Minecraft.getMinecraft().mcDataDir; // data dir for via
    }

    @Override
    public JsonObject createDump() {
        return new JsonObject(); // not important since commands aren't implemented by default
    }

    // #######################
    // # Netty Version based #
    // #######################
    @Override
    public EventLoop eventLoop(final ThreadFactory threadFactory, final ExecutorService executorService) {
        // For Netty above 4.1.x, (>= Minecraft 1.12.2)
        return new DefaultEventLoop(executorService);
        
        // For Netty older than 4.0.x (< Minecraft 1.12.2 && > Minecraft 1.6.4)
        return new LocalEventLoopGroup(1, threadFactory).next();
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
