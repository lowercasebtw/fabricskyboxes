package io.github.amerebagatelle.mods.nuit.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Axis;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.*;
import io.github.amerebagatelle.mods.nuit.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

        Vector3f rotationStatic = this.rotation.getStatic();

        matrixStack.pushPose();

        // axis + time rotation
        double timeRotationX = Utils.calculateRotation(this.rotation.getRotationSpeedX(), this.rotation.getTimeShift().x(), this.rotation.getSkyboxRotation(), world);
        double timeRotationY = Utils.calculateRotation(this.rotation.getRotationSpeedY(), this.rotation.getTimeShift().y(), this.rotation.getSkyboxRotation(), world);
        double timeRotationZ = Utils.calculateRotation(this.rotation.getRotationSpeedZ(), this.rotation.getTimeShift().z(), this.rotation.getSkyboxRotation(), world);
        this.applyTimeRotation(matrixStack, (float) timeRotationX, (float) timeRotationY, (float) timeRotationZ);
        // static
        matrixStack.mulPose(Axis.XP.rotationDegrees(rotationStatic.x()));
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotationStatic.y()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(rotationStatic.z()));
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

    private void applyTimeRotation(PoseStack matrixStack, float timeRotationX, float timeRotationY, float timeRotationZ) {
        // Very ugly, find a better way to do this
        Vector3f timeRotationAxis = this.rotation.getAxis();
        matrixStack.mulPose(Axis.XP.rotationDegrees(timeRotationAxis.x()));
        matrixStack.mulPose(Axis.YP.rotationDegrees(timeRotationAxis.y()));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(timeRotationAxis.z()));
        matrixStack.mulPose(Axis.XP.rotationDegrees(timeRotationX));
        matrixStack.mulPose(Axis.YP.rotationDegrees(timeRotationY));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(timeRotationZ));
        matrixStack.mulPose(Axis.ZN.rotationDegrees(timeRotationAxis.z()));
        matrixStack.mulPose(Axis.YN.rotationDegrees(timeRotationAxis.y()));
        matrixStack.mulPose(Axis.XN.rotationDegrees(timeRotationAxis.x()));
    }

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
