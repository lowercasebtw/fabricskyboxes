package io.github.amerebagatelle.mods.nuit.fabric;

import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.mods.nuit.api.NuitPlatformHelper;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

import java.nio.file.Path;

public class NuitFabricPlatformHelper implements NuitPlatformHelper {
    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Registry<SkyboxType<? extends Skybox>> getSkyboxTypeRegistry() {
        return NuitClientFabric.REGISTRY;
    }
}
