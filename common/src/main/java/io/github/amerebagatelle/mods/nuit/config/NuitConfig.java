package io.github.amerebagatelle.mods.nuit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class NuitConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    public final GeneralSettings generalSettings = new GeneralSettings();
    private final KeyBindingImpl keyBinding = new KeyBindingImpl();
    private File file;

    public static NuitConfig load(File file) {
        NuitConfig config;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, NuitConfig.class);
            } catch (Exception e) {
                NuitClient.getLogger().error("Could not parse config, falling back to defaults!", e);
                config = new NuitConfig();
            }
        } else {
            config = new NuitConfig();
        }
        config.file = file;
        config.save();

        return config;
    }

    public KeyBindingImpl getKeyBinding() {
        return this.keyBinding;
    }

    public void save() {
        File dir = this.file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent file is not a directory");
        }

        try (FileWriter writer = new FileWriter(this.file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save configuration file", e);
        }
    }

    public static class GeneralSettings {
        public boolean enable = true;
        public int unexpectedTransitionDuration = 20;
        public boolean keepVanillaBehaviour = true;

        public boolean debugMode = false;
        public boolean debugHud = false;
    }


    public static class KeyBindingImpl {

        public final KeyMapping toggleNuit = new KeyMapping("key.nuit.toggle", InputConstants.Type.KEYSYM, -1, "category.nuit");
        public final KeyMapping toggleSkyboxDebugHud = new KeyMapping("key.nuit.toggle.debug_hud", InputConstants.Type.KEYSYM, -1, "category.nuit");

        public void tick(Minecraft client) {
            while (this.toggleNuit.consumeClick()) {
                NuitClient.config().generalSettings.enable = !NuitClient.config().generalSettings.enable;
                NuitClient.config().save();
                SkyboxManager.getInstance().setEnabled(NuitClient.config().generalSettings.enable);

                assert client.player != null;
                if (SkyboxManager.getInstance().isEnabled()) {
                    client.player.displayClientMessage(Component.translatable("nuit.message.enabled"), false);
                } else {
                    client.player.displayClientMessage(Component.translatable("nuit.message.disabled"), false);
                }
            }
            while (this.toggleSkyboxDebugHud.consumeClick()) {
                NuitClient.config().generalSettings.debugHud = !NuitClient.config().generalSettings.debugHud;
                NuitClient.config().save();
            }
        }
    }
}
