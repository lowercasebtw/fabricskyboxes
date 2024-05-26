package io.github.amerebagatelle.mods.nuit.skybox;

import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.components.Conditions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Objects;

public class DefaultHandler {
    public static final ResourceLocation DEFAULT = new ResourceLocation(NuitClient.MOD_ID, "default");

    /**
     * Stores a Conditions instance concatenated from all Conditions instances in skyboxes in SkyboxManager.
     * Includes skyboxes from both skyboxMap and permanentSkyboxMap.
     * Default skyboxes check against the inverse of concatConditions.
     */
    private static Conditions concatConditions = Conditions.DEFAULT;

    /**
     * Concatenates conditions from a skybox to concatConditions.
     * Should be called whenever a skybox is added to SkyboxManager.
     *
     * @param skybox the skybox containing the conditions to be added to concatConditions.
     */
    public static void addConditions(Skybox skybox) {
        if (skybox instanceof NuitSkybox nuitSkybox) {
            addConditions(nuitSkybox.getConditions());
        }
    }

    public static void addConditions(Conditions conditions) {
        for (ResourceLocation location : conditions.getBiomes()) {
            if (!concatConditions.getBiomes().contains(location))
                concatConditions.getBiomes().add(location);
        }

        for (ResourceLocation resourceLocation : conditions.getWorlds()) {
            if (!concatConditions.getWorlds().contains(resourceLocation))
                concatConditions.getWorlds().add(resourceLocation);
        }

        for (ResourceLocation resource : conditions.getDimensions()) {
            if (!concatConditions.getDimensions().contains(resource))
                concatConditions.getDimensions().add(resource);
        }
    }

    /**
     * Clears all conditions from concatConditions.
     */
    private static void clearConditions() {
        concatConditions = Conditions.DEFAULT;
    }

    /**
     * Clears all conditions from concatConditions, then readds all conditions contained in the skyboxes in exceptions.
     */
    public static void clearConditionsExcept(Collection<Skybox> exceptions) {
        clearConditions();
        exceptions.forEach(DefaultHandler::addConditions);
    }

    /**
     * @return true if the current biome is not listed as a condition in any loaded skybox.
     */
    public static boolean checkFallbackBiomes() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        Objects.requireNonNull(client.player);
        return !concatConditions.getBiomes().contains(client.level.registryAccess().registryOrThrow(Registries.BIOME).getKey(client.level.getBiome(client.player.blockPosition()).value()));
    }

    /**
     * @return true if the current world is not listed as a condition in any loaded skybox.
     */
    public static boolean checkFallbackWorlds() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return !concatConditions.getWorlds().contains(client.level.dimensionType().effectsLocation());
    }

    /**
     * @return true if the current dimension is not listed as a condition in any loaded skybox.
     */
    public static boolean checkFallbackDimensions() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return !concatConditions.getDimensions().contains(client.level.dimension().location());
    }

    /**
     * @return a Conditions instance containing all biomes, worlds, and dimensions listed in all loaded skyboxes.
     */
    public static Conditions getConcatConditions() {
        return concatConditions;
    }
}
