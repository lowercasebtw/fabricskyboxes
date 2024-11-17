package io.github.amerebagatelle.mods.nuit.skybox.vanilla;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.components.Conditions;
import io.github.amerebagatelle.mods.nuit.components.Properties;
import io.github.amerebagatelle.mods.nuit.mixin.SkyRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.AbstractSkybox;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.FogParameters;
import org.joml.Matrix4f;

public class EndSkybox extends AbstractSkybox {
    public static Codec<EndSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.optionalFieldOf("properties", Properties.of()).forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.of()).forGetter(AbstractSkybox::getConditions)
    ).apply(instance, EndSkybox::new));

    public EndSkybox(Properties properties, Conditions conditions) {
        super(properties, conditions);
    }

    @Override
    public void render(SkyRendererAccessor skyRendererAccess, PoseStack poseStack, Matrix4f projectionMatrix, float tickDelta, Camera camera, FogParameters fogParameters, Runnable fogCallback) {
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, SkyRendererAccessor.getEndSky());
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            if (i == 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = poseStack.last().pose();
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(40, 40, 40, (int) (255 * this.alpha));
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(40, 40, 40, (int) (255 * this.alpha));
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(40, 40, 40, (int) (255 * this.alpha));
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(40, 40, 40, (int) (255 * this.alpha));
            poseStack.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}

