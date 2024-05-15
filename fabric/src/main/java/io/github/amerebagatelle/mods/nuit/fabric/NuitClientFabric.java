package io.github.amerebagatelle.mods.nuit.fabric;

import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.resource.SkyboxResourceListener;
import io.github.amerebagatelle.mods.nuit.screen.SkyboxDebugScreen;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class NuitClientFabric implements ClientModInitializer {
    public static final Registry<SkyboxType<? extends Skybox>> REGISTRY = FabricRegistryBuilder.from(new MappedRegistry<>(SkyboxType.SKYBOX_TYPE_REGISTRY_KEY, Lifecycle.stable())).buildAndRegister();

    @Override
    public void onInitializeClient() {
        SkyboxType.register(skyboxType -> Registry.register(REGISTRY, skyboxType.createId(), skyboxType));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(NuitClient.MOD_ID, "skybox_reader");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                new SkyboxResourceListener().readFiles(resourceManager);
            }
        });

        ClientTickEvents.END_WORLD_TICK.register(client -> SkyboxManager.getInstance().tick(client));
        ClientTickEvents.END_CLIENT_TICK.register(client -> NuitClient.config().getKeyBinding().tick(client));
        SkyboxDebugScreen screen = new SkyboxDebugScreen(Component.nullToEmpty("Skybox Debug Screen"));
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> screen.renderHud(drawContext));
        KeyBindingHelper.registerKeyBinding(NuitClient.config().getKeyBinding().toggleFabricSkyBoxes);
        KeyBindingHelper.registerKeyBinding(NuitClient.config().getKeyBinding().toggleSkyboxDebugHud);
        NuitClient.init();

    }
}