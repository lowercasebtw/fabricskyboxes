package io.github.amerebagatelle.mods.nuit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import io.github.amerebagatelle.mods.nuit.api.NuitPlatformHelper;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.components.Metadata;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.DefaultHandler;
import io.github.amerebagatelle.mods.nuit.skybox.SkyboxType;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;

import java.util.*;

public class SkyboxManager implements NuitApi {
    private static final SkyboxManager INSTANCE = new SkyboxManager();
    private final Map<ResourceLocation, Skybox> skyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    /**
     * Stores a list of permanent skyboxes
     *
     * @see #addPermanentSkybox(ResourceLocation, Skybox)
     */
    private final Map<ResourceLocation, Skybox> permanentSkyboxMap = new Object2ObjectLinkedOpenHashMap<>();
    private final List<Skybox> activeSkyboxes = new LinkedList<>();
    private Skybox currentSkybox = null;
    private boolean enabled = true;

    public static Optional<Skybox> parseSkyboxJson(ResourceLocation id, JsonObject jsonObject) {
        Metadata metadata;

        try {
            metadata = Metadata.CODEC.decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst();
        } catch (RuntimeException e) {
            NuitClient.getLogger().warn("Skipping invalid skybox {}", id.toString(), e);
            NuitClient.getLogger().warn(jsonObject.toString());
            return Optional.empty();
        }

        SkyboxType<? extends Skybox> type = NuitPlatformHelper.INSTANCE.getSkyboxTypeRegistry().get(metadata.getType());
        if (type == null) {
            NuitClient.getLogger().warn("Skipping skybox {} with unknown type {}", id.toString(), metadata.getType().getPath().replace('_', '-'));
            return Optional.empty();
        }

        try {
            return Optional.of(type.getCodec(metadata.getSchemaVersion()).decode(JsonOps.INSTANCE, jsonObject).getOrThrow().getFirst());
        } catch (RuntimeException e) {
            NuitClient.getLogger().warn("Skipping invalid skybox {}", id.toString(), e);
            NuitClient.getLogger().warn(jsonObject.toString());
            return Optional.empty();
        }
    }

    public static SkyboxManager getInstance() {
        return INSTANCE;
    }

    public void addSkybox(ResourceLocation identifier, JsonObject jsonObject) {
        var skybox = SkyboxManager.parseSkyboxJson(identifier, jsonObject);
        if (skybox.isPresent()) {
            NuitClient.getLogger().info("Adding skybox {}", identifier.toString());
            this.addSkybox(identifier, skybox.get());
        }
    }

    public void addSkybox(ResourceLocation identifier, Skybox skybox) {
        Preconditions.checkNotNull(identifier, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        DefaultHandler.addConditions(skybox);
        this.skyboxMap.put(identifier, skybox);
    }

    /**
     * Permanent skyboxes are never cleared after a resource reload. This is
     * useful when adding skyboxes through code as resource reload listeners
     * have no defined order of being called.
     *
     * @param skybox the skybox to be added to the list of permanent skyboxes
     */
    public void addPermanentSkybox(ResourceLocation identifier, Skybox skybox) {
        Preconditions.checkNotNull(identifier, "Identifier was null");
        Preconditions.checkNotNull(skybox, "Skybox was null");
        DefaultHandler.addConditions(skybox);
        this.permanentSkyboxMap.put(identifier, skybox);
    }

    @Internal
    public void clearSkyboxes() {
        DefaultHandler.clearConditionsExcept(this.permanentSkyboxMap.values());
        this.skyboxMap.clear();
        this.activeSkyboxes.clear();
    }

    @Internal
    public void renderSkyboxes(LevelRendererAccessor worldRendererAccess, PoseStack matrixStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        for (Skybox skybox : this.activeSkyboxes) {
            this.currentSkybox = skybox;
            skybox.render(worldRendererAccess, matrixStack, projectionMatrix, tickDelta, camera, thickFog, fogCallback);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Skybox getCurrentSkybox() {
        return this.currentSkybox;
    }

    @Override
    public List<Skybox> getActiveSkyboxes() {
        return this.activeSkyboxes;
    }

    public void tick(ClientLevel level) {
        for (Skybox skybox : Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxMap.values())) {
            skybox.tick(level);
        }

        this.activeSkyboxes.removeIf(skybox -> !skybox.isActive());

        // Add the skyboxes to a activeSkyboxes container so that they can be ordered
        for (Skybox skybox : Iterables.concat(this.skyboxMap.values(), this.permanentSkyboxMap.values())) {
            if (!this.activeSkyboxes.contains(skybox) && skybox.isActive()) {
                this.activeSkyboxes.add(skybox);
            }
        }
        this.activeSkyboxes.sort(Comparator.comparingInt(Skybox::getLayer));
    }

    public Map<ResourceLocation, Skybox> getSkyboxMap() {
        return this.skyboxMap;
    }
}