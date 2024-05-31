package io.github.amerebagatelle.mods.nuit.skybox.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.mods.nuit.components.*;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Objects;

public abstract class TexturedSkybox extends AbstractSkybox implements RotatableSkybox {
    public Rotation rotation;
    public Blend blend;

    protected TexturedSkybox() {
    }

    protected TexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend) {
        super(properties, conditions, decorations);
        this.blend = blend;
        this.rotation = properties.getRotation();
    }

    /**
     * Overrides and makes final here as there are options that should always be respected in a textured skybox.
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param matrixStack         The current MatrixStack.
     * @param tickDelta           The current tick delta.
     */
    @Override
    public final void render(LevelRendererAccessor worldRendererAccess, PoseStack matrixStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        this.blend.applyBlendFunc(this.alpha);

        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        long currentTime = world.getDayTime() % this.rotation.getRotationDuration();

        var mappingFrames = this.rotation.getMapping();
        var axisFrames = this.rotation.getAxis();

        matrixStack.pushPose();

        // static
        var possibleMappingKeyframes = Utils.findClosestKeyframes(mappingFrames, currentTime);
        Quaternionf mappingRot = new Quaternionf();
        if (possibleMappingKeyframes.isPresent()) {
            mappingRot.set(Utils.interpolateQuatKeyframes(mappingFrames, possibleMappingKeyframes.get(), currentTime));
            matrixStack.mulPose(mappingRot);
        }

        var possibleAxisKeyframes = Utils.findClosestKeyframes(axisFrames, currentTime);
        Quaternionf axisRot = new Quaternionf();
        if (possibleAxisKeyframes.isPresent()) {
            axisRot.set(Utils.interpolateQuatKeyframes(axisFrames, possibleAxisKeyframes.get(), currentTime));
            axisRot.difference(mappingRot.conjugate());
            matrixStack.mulPose(axisRot);

            double timeRotation = Utils.calculateRotation(this.getRotation().getSpeed(), this.getRotation().getSkyboxRotation(), world);
            matrixStack.mulPose(Axis.XP.rotationDegrees((float) timeRotation));

            matrixStack.mulPose(axisRot.conjugate());
        }

        this.renderSkybox(worldRendererAccess, matrixStack, tickDelta, camera, thickFog, fogCallback);
        matrixStack.popPose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        this.renderDecorations(worldRendererAccess, matrixStack, projectionMatrix, tickDelta, bufferBuilder, this.alpha, fogCallback);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(LevelRendererAccessor worldRendererAccess, PoseStack matrixStack, float tickDelta, Camera camera, boolean thickFog, Runnable runnable);

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
