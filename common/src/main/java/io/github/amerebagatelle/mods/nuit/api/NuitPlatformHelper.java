package io.github.amerebagatelle.mods.nuit.api;

import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.core.Registry;

import java.nio.file.Path;

public interface NuitPlatformHelper {
    public static NuitPlatformHelper INSTANCE = Utils.loadService(NuitPlatformHelper.class);

    Path getGameDir();

    Path getConfigDir();

    Registry<SkyboxType<? extends Skybox>> getSkyboxTypeRegistry();
}
