package io.github.amerebagatelle.mods.nuit.screen;

import dev.architectury.event.events.client.ClientGuiEvent;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SkyboxDebugScreen extends Screen implements ClientGuiEvent.RenderHud {
    public SkyboxDebugScreen(Component title) {
        super(title);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderHud(context, delta);
    }

    @Override
    public void renderHud(GuiGraphics drawContext, float tickDelta) {
        if (NuitClient.config().generalSettings.debugHud || Minecraft.getInstance().screen == this) {
            int yPadding = 2;
            for (Map.Entry<ResourceLocation, Skybox> identifierSkyboxEntry : SkyboxManager.getInstance().getSkyboxMap().entrySet()) {
                Skybox activeSkybox = identifierSkyboxEntry.getValue();
                if (activeSkybox instanceof NuitSkybox nuitSkybox && nuitSkybox.isActive()) {
                    drawContext.drawString(Minecraft.getInstance().font, identifierSkyboxEntry.getKey() + " " + activeSkybox.getPriority() + " " + nuitSkybox.getAlpha(), 2, yPadding, 0xffffffff, false);
                    yPadding += 14;
                }
            }
        }
    }
}
