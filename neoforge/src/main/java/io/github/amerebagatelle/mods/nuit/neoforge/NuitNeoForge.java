package io.github.amerebagatelle.mods.nuit.neoforge;

import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.resource.SkyboxResourceListener;
import io.github.amerebagatelle.mods.nuit.screen.SkyboxDebugScreen;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.concurrent.CompletableFuture;

@Mod(NuitClient.MOD_ID)
public final class NuitNeoForge {
    public static final Registry<SkyboxType<? extends Skybox>> REGISTRY = new RegistryBuilder<>(SkyboxType.SKYBOX_TYPE_REGISTRY_KEY).create();

    public NuitNeoForge(IEventBus bus) {
        bus.addListener(this::registerSkyTypeRegistry);
        bus.addListener(this::registerSkyTypes);
        bus.addListener(this::registerClientReloadListener);
        bus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::registerClientTick);
        NeoForge.EVENT_BUS.addListener(this::registerWorldTick);
        NeoForge.EVENT_BUS.addListener(this::registerHudRender);
        NuitClient.init();
    }

    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(NuitClient.config().getKeyBinding().toggleFabricSkyBoxes);
        event.register(NuitClient.config().getKeyBinding().toggleSkyboxDebugHud);
    }

    @SubscribeEvent
    public void registerClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        NuitClient.config().getKeyBinding().tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public void registerWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.type != TickEvent.Type.LEVEL || event.side != LogicalSide.CLIENT) {
            return;
        }
        SkyboxManager.getInstance().tick((ClientLevel) event.level);
    }

    SkyboxDebugScreen screen = new SkyboxDebugScreen(Component.nullToEmpty("Skybox Debug Screen"));

    @SubscribeEvent
    public void registerHudRender(RenderGuiLayerEvent.Post event) {
        screen.renderHud(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public void registerSkyTypeRegistry(NewRegistryEvent event) {
        event.register(REGISTRY);
    }

    @SubscribeEvent
    public void registerSkyTypes(RegisterEvent event) {
        //todo: we can probably create an event for this
        event.register(SkyboxType.SKYBOX_TYPE_REGISTRY_KEY, registry -> {
            registry.register(SkyboxType.OVERWORLD.createId(), SkyboxType.OVERWORLD);
            registry.register(SkyboxType.END.createId(), SkyboxType.END);
            registry.register(SkyboxType.MONO_COLOR_SKYBOX.createId(), SkyboxType.MONO_COLOR_SKYBOX);
            registry.register(SkyboxType.SQUARE_TEXTURED_SKYBOX.createId(), SkyboxType.SQUARE_TEXTURED_SKYBOX);
            registry.register(SkyboxType.MULTI_TEXTURED_SKYBOX.createId(), SkyboxType.MULTI_TEXTURED_SKYBOX);
        });
    }

    @SubscribeEvent
    public void registerClientReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((pPreparationBarrier, pResourceManager, pPreparationsProfiler, pReloadProfiler, pBackgroundExecutor, pGameExecutor) -> CompletableFuture.runAsync(() -> new SkyboxResourceListener().readFiles(pResourceManager), pGameExecutor).thenCompose(pPreparationBarrier::wait));
    }
}