package io.github.amerebagatelle.mods.nuit;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import io.github.amerebagatelle.mods.nuit.config.NuitConfig;
import io.github.amerebagatelle.mods.nuit.resource.SkyboxResourceListener;
import io.github.amerebagatelle.mods.nuit.screen.SkyboxDebugScreen;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NuitClient {
	public static final String MOD_ID = "nuit";

	private static Logger LOGGER;
	private static NuitConfig CONFIG;

	public static void init() {
		SkyboxType.initRegistry();
		ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new SkyboxResourceListener());
		SkyboxManager.getInstance().setEnabled(config().generalSettings.enable);
		ClientTickEvent.CLIENT_LEVEL_POST.register(SkyboxManager.getInstance());
		ClientTickEvent.CLIENT_POST.register(config().getKeyBinding());
		SkyboxDebugScreen screen = new SkyboxDebugScreen(Component.nullToEmpty("Skybox Debug Screen"));
		ClientGuiEvent.RENDER_HUD.register(screen);
	}

	public static Logger getLogger() {
		if (LOGGER == null) {
			LOGGER = LogManager.getLogger("Nuit");
		}
		return LOGGER;
	}

	public static NuitConfig config() {
		if (CONFIG == null) {
			CONFIG = loadConfig();
		}

		return CONFIG;
	}

	private static NuitConfig loadConfig() {
		return NuitConfig.load(Platform.getConfigFolder().resolve("fabricskyboxes-config.json").toFile());
	}
}
