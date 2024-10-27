package io.github.amerebagatelle.mods.nuit.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SkyboxResourceListener implements PreparableReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();

    public void readFiles(ResourceManager resourceManager, Executor backgroundExecutor) {
        NuitApi skyboxManager = NuitApi.getInstance();

        skyboxManager.clearSkyboxes();

        Map<ResourceLocation, Resource> resources = resourceManager.listResources("sky", identifier -> identifier.getPath().endsWith(".json"));

        resources.forEach((identifier, resource) -> {
            try {
                JsonObject json = GSON.fromJson(new InputStreamReader(resource.open()), JsonObject.class);
                skyboxManager.addSkybox(identifier, json);
            } catch (Exception e) {
                NuitClient.getLogger().error("Error reading skybox {}", identifier.toString(), e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
        return CompletableFuture.runAsync(() -> this.readFiles(resourceManager, executor), executor2).thenCompose(preparationBarrier::wait);
    }
}
