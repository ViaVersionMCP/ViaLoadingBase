# ViaLoadingBase
Universal ViaVersion standalone implementation

## Projects where this is used:
1. [ViaForge](https://github.com/FlorianMichael/ViaForge): Clientside ViaVersion for Forge
2. [ViaFabricPlus](https://github.com/FlorianMichael/ViaFabricPlus): Clientside ViaVersion, ViaLegacy and ViaAprilFools implementation with clientside fixes for Fabric

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
        "org.yaml:snakeyaml:1.29",
        
        "com.github.FlorianMichael:ViaLoadingBase:<newest version (checkout jitpack.io for that)>"
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
## Example implementation:

```java
public class ExampleImplementation {
    
    public void main() {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().
                runDirectory(new File("ViaVersion")).
                nativeVersion(47).
                singlePlayerProvider(() -> Minecraft.getMinecraft().isInSingleplayer).
                eventLoop(
                        // For Netty above 4.1.x, (>= Minecraft 1.12.2)
                        new DefaultEventLoop(ViaLoadingBase.EXECUTOR_SERVICE)

                        // For Netty older than 4.0.x (< Minecraft 1.12.2 && > Minecraft 1.6.4)
                        // new LocalEventLoopGroup(1, threadFactory).next()
                ).
        build();
    }
}
```
