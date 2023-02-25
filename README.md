# ViaLoadingBase
Universal ViaVersion, ViaBackwards and ViaRewind standalone implementation

## Contact
If you encounter any issues, please report them on the
[issue tracker](https://github.com/FlorianMichael/ViaLoadingBase/issues).  
If you just want to talk or need help with ViaLoadingBase feel free to join my
[Discord](https://discord.gg/BwWhCHUKDf).

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

## API examples:
ViaLoadingBase also offers a system to compare the target version with other versions:
```java
public class APIExample {
    
    public void versionDiffTests() {
        if (ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
            // Code is executed when the target version is < than 1.8
        }
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            // Code is executed when the target version is < = than 1.16.4
        }
        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) {
            // Code is executed when the target version is > than 1.12.2
        }
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            // Code is executed when the target version is > = than 1.14.4
        }
        if (ViaLoadingBase.getTargetVersion() == ProtocolVersion.v1_10) {
            // Code is executed when the target version is equal to 1.10
        }
    }
}
```
The versions are compared according to the order in which they are loaded, each protocol version that is loaded gets an <br>
index that is then used for comparison, so if a platform is loaded last, its protocols are treated as oldest. <br>
Below is explained how to determine the pure sequence

## How to load sub platforms:
To load a sub platform, you simply create a SubPlatform Field, in which you first specify the name of the platform, <br>
the Boolean Supplier indicates whether the sub platform has been loaded in the classpath (you can use *SubPlatform.isClass()*), <br>
in the Runnable you then have to create and load the PlatformImpl as a class, as last you can add via the Consumer <br>
the protocols that the platform will add
```java
public class ExampleImplementation {
    
    private final SubPlatform examplePlatform = new SubPlatform("Example", () -> SubPlatform.isClass("net.exampledev.exampleplatform.ExamplePlatform"), ExamplePlatformImpl::new, protocolVersions -> {
        protocolVersions.addAll(ExamplePlatformVersions.PROTOCOLS);
    });
    
    public void main() {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().
                subPlatform(examplePlatform). // The ViaLoadingBaseBuilder has a sub platform method which can be used to register the sub platforms.
                subPlatform(examplePlatform, 0). // will set the sub platform as first 
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

For some example implementations and applications you can have a look at the code in [ViaFabricPlus](https://github.com/FlorianMichael/ViaFabricPlus) 
