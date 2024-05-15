# API Documentation

Nuit provides an api that allows you to register skyboxes in code.
The code can be found at [here](/src/main/java/io/github/amerebagatelle/mods/nuit/api).

## Gradle

You can find the version of Nuit you require over at [Modrinth](https://modrinth.com/mod/fabricskyboxes/versions).

```
repositories {
    maven {
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation 'maven.modrinth:Nuit:${project.Nuit_version}'
}
```

## Quickstart

```java
// Verify that Nuit is present
if(FabricLoader.getInstance().

isModLoaded("Nuit")){
        // Verify Nuit's API feature set
        if(NuitApi.

getInstance().

getApiVersion() ==0){
        // Enables Nuit
        NuitApi.

getInstance().

setEnabled(true);
// Clear loaded skyboxes
        NuitApi.

getInstance().

clearSkyboxes();

// Adds a temporary skybox
        NuitApi.

getInstance().

addSkybox(Identifier.of("my_mod", "my_temporary_skybox"), /*JsonObject or Skybox implementation*/);
        // Adds a permanent skybox
        NuitApi.

getInstance().

addPermanentSkybox(Identifier.of("my_mod", "my_permanent_skybox"), /*Skybox implementation*/);
        }
        }
```

### Skybox Implementation

```java
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.mixin.skybox.WorldRendererAccess;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class MyModSkybox implements Skybox {
    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        // Render our own skybox implementation here
    }

    @Override
    public boolean isActive() {
        return true; // Always enabled
    }

    @Override
    public boolean isActiveLater() {
        return this.isActive(); // Is enabled next render?
    }
}
```