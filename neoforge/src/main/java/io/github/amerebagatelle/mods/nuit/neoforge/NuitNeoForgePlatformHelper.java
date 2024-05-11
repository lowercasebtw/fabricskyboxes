package io.github.amerebagatelle.mods.nuit.neoforge;

import io.github.amerebagatelle.mods.nuit.api.NuitPlatformHelper;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.core.Registry;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NuitNeoForgePlatformHelper implements NuitPlatformHelper {

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Registry<SkyboxType<? extends Skybox>> getSkyboxTypeRegistry() {
        return NuitNeoForge.REGISTRY;
    }
}
