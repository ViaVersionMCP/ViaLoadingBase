# ViaLoadingBase
Universal ViaVersion, ViaBackwards and ViaRewind standalone implementation <br>
<br>
This library is mainly designed for clientside implementations, it is also very abstract and doesn't give the <br>
implementors much room to change ViaVersion, for a platform with less abstraction you can look at [ViaProtocolHack](https://github.com/RaphiMC/ViaProtocolHack).

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

dependencies {
    implementation "com.viaversion:viaversion:4.6.0-1.19.4-pre2-SNAPSHOT"
    implementation "com.viaversion:viabackwards:4.6.0-1.19.4-pre1-SNAPSHOT"
    implementation "com.viaversion:viarewind-core:2.0.3-SNAPSHOT"
    
    implementation "com.github.FlorianMichael:ViaLoadingBase:1b035c653e" // https://jitpack.io/#FlorianMichael/ViaLoadingBase
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
if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
    // Code is executed when the target version is < than 1.8
}
if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
    // Code is executed when the target version is < = than 1.16.4
}
if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) {
    // Code is executed when the target version is > than 1.12.2
}
if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_14_4)) {
    // Code is executed when the target version is > = than 1.14.4
}
if (ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(ProtocolVersion.v1_10)) {
    // Code is executed when the target version is equal to 1.10
}
```
The versions are compared according to the order in which they are loaded, each protocol version that is loaded gets an <br>
index that is then used for comparison, so if a platform is loaded last, its protocols are treated as oldest. <br>
Below is explained how to determine the pure sequence<br>
<br>
To define a range of versions you can use the *ProtocolRange* class:
```java
final ProtocolRange allVersionsAbove1_8 = ProtocolRange.andNewer(ProtocolVersion.v1_8);
final ProtocolRange allVersionsUnder1_12_2 = ProtocolRange.andOlder(ProtocolVersion.v1_12_2);
final ProtocolRange only1_18_2 = ProtocolRange.singleton(ProtocolRange.v1_18_2);

if (allVersionsAbove1_8.contains(ProtocolVersion.v1_10)) {
    // Check if a version is in the range
}
```
The class also has a toString() method that automatically formats the range

## How to load sub platforms:
```java
public class ExampleImplementation {
    
    private final SubPlatform examplePlatform = new SubPlatform(
            "Example", 
            () -> SubPlatform.isClass("net.exampledev.exampleplatform.ExamplePlatform"), // Checks if the platform class is in the class path
            ExamplePlatformImpl::new, 
            protocolVersions -> {
        protocolVersions.addAll(ExamplePlatformVersions.PROTOCOLS);
    });
    
    public void main() {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().

                subPlatform(examplePlatform). // will set the sub platform as last 
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
