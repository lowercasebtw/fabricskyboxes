package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
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
     * @param matrix4f            The current MatrixStack.
     * @param tickDelta           The current tick delta.
     */
    @Override
    public final void render(WorldRendererAccess worldRendererAccess, MatrixStack matrixStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        this.blend.applyBlendFunc(this.alpha);

        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);

        Vector3f rotationStatic = this.rotation.getStatic();

        matrixStack.push();

        // axis + time rotation
        double timeRotationX = Utils.calculateRotation(this.rotation.getRotationSpeedX(), this.rotation.getTimeShift().x(), this.rotation.getSkyboxRotation(), world);
        double timeRotationY = Utils.calculateRotation(this.rotation.getRotationSpeedY(), this.rotation.getTimeShift().y(), this.rotation.getSkyboxRotation(), world);
        double timeRotationZ = Utils.calculateRotation(this.rotation.getRotationSpeedZ(), this.rotation.getTimeShift().z(), this.rotation.getSkyboxRotation(), world);
        this.applyTimeRotation(matrixStack, (float) timeRotationX, (float) timeRotationY, (float) timeRotationZ);
        // static
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationStatic.x()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationStatic.y()));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationStatic.z()));
        this.renderSkybox(worldRendererAccess, matrixStack, tickDelta, camera, thickFog, fogCallback);
        matrixStack.pop();

        this.renderDecorations(worldRendererAccess, matrixStack, projectionMatrix, tickDelta, this.alpha, fogCallback);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        // fixme:
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrixStack, float tickDelta, Camera camera, boolean thickFog, Runnable runnable);

    private void applyTimeRotation(MatrixStack matrixStack, float timeRotationX, float timeRotationY, float timeRotationZ) {
        // Very ugly, find a better way to do this
        Vector3f timeRotationAxis = this.rotation.getAxis();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(timeRotationAxis.x()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(timeRotationAxis.y()));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(timeRotationAxis.z()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(timeRotationX));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(timeRotationY));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(timeRotationZ));
        matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(timeRotationAxis.z()));
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(timeRotationAxis.y()));
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(timeRotationAxis.x()));
    }

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
