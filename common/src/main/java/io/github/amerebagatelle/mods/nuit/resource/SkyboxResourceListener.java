package io.github.amerebagatelle.mods.nuit.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.Map;

public class SkyboxResourceListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();

    public void readFiles(ResourceManager resourceManager) {
        NuitApi skyboxManager = NuitApi.getInstance();

        // clear registered skyboxes on reload
        skyboxManager.clearSkyboxes();

        // load new skyboxes
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("sky", identifier -> identifier.getPath().endsWith(".json"));

        resources.forEach((identifier, resource) -> {
            try {
                JsonObject json = GSON.fromJson(new InputStreamReader(resource.open()), JsonObject.class);
                skyboxManager.addSkybox(identifier, json);
            } catch (Exception e) {
                NuitClient.getLogger().error("Error reading skybox {}", identifier.toString());
                NuitClient.getLogger().error(e.getMessage());
            }
        });
    }
}
