package io.github.amerebagatelle.mods.nuit.api.skyboxes;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public interface Skybox {
    default int getPriority() {
        return 0;
    }

    void render(LevelRendererAccessor levelRendererAccessor, PoseStack poseStack, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback);

    void tick(ClientLevel clientLevel);

    boolean isActive();
}
