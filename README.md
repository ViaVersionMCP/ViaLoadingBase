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
2. [ViaFabricPlus](https://github.com/FlorianMichael/ViaFabricPlus): Clientside ViaVersion, ViaLegacy, ViaBedrock and ViaAprilFools implementation with clientside fixes for Fabric

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
    implementation "com.viaversion:viaversion:4.7.0-23w13a-SNAPSHOT"
    implementation "com.viaversion:viabackwards:4.7.0-23w13a-SNAPSHOT"
    implementation "com.viaversion:viarewind-core:2.0.4-SNAPSHOT"
    
    implementation "com.github.FlorianMichael:ViaLoadingBase:a660bf00d5" // https://jitpack.io/#FlorianMichael/ViaLoadingBase
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
public void init() { // Basic code to load all platforms which are in the class path
    ViaLoadingBase.ViaLoadingBaseBuilder.
        create().
        runDirectory(new File("ViaVersion")).
        nativeVersion(47).
        build();
}
```

The most important part is the modification of your netty pipeline. This is needed for **ViaVersion** to translate the packets in both ways.
```java
final UserConnection user = new UserConnectionImpl(channel, true);

new ProtocolPipelineImpl(user);

channel.pipeline().addLast(new VLBPipeline(user) {

    @Override
    public String getDecoderHandlerName() {
        return "decoder";
    }

    @Override
    public String getEncoderHandlerName() {
        return "encoder";
    }

    @Override
    public String getDecompressionHandlerName() {
        return "decompress";
    }

    @Override
    public String getCompressionHandlerName() {
        return "compress";
    }
});
```
In case your platform has compression, you can call the **CompressionReorderEvent** at the end of the compression code to correct the compression.
```java
channel.pipeline().fireUserEventTriggered(new CompressionReorderEvent());
```
In order for ViaLoadingBase to find the compression handler in the pipeline, there is a String[] in the **NettyConstants** class that has **decompress, compress** by default. you can modify this field

### For a mcp based implementation you can have a look at the code in [ViaForge](https://github.com/FlorianMichael/ViaForge)

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

## How to load platforms:

```java
public class ExampleImplementation {

    private final Platform examplePlatform = new Platform(
            "Example",
            () -> ViaLoadingBase.inClassPath("net.exampledev.exampleplatform.ExamplePlatform"), // Checks if the platform class is in the class path
            ExamplePlatformImpl::new,
            protocolVersions -> protocolVersions.addAll(ExamplePlatformVersions.PROTOCOLS));

    public void main() {
        ViaLoadingBase.ViaLoadingBaseBuilder.
                create().

                platform(examplePlatform). // will set the platform as last 
                platform(examplePlatform, 0). // will set the platform as first 

                runDirectory(new File("ViaVersion")).
                nativeVersion(47).
                build();
    }
}
```

For some example implementations and applications you can have a look at the code in [ViaFabricPlus](https://github.com/FlorianMichael/ViaFabricPlus) 
