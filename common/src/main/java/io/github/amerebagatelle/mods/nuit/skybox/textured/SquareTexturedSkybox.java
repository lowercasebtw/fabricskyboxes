package io.github.amerebagatelle.mods.nuit.skybox.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.components.*;
import io.github.amerebagatelle.mods.nuit.mixin.SkyRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.List;

public class SquareTexturedSkybox extends TexturedSkybox {
    public static Codec<SquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.optionalFieldOf("properties", Properties.of()).forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.of()).forGetter(AbstractSkybox::getConditions),
            Blend.CODEC.optionalFieldOf("blend", Blend.normal()).forGetter(TexturedSkybox::getBlend),
            Texture.CODEC.fieldOf("texture").forGetter(SquareTexturedSkybox::getTexture)
    ).apply(instance, SquareTexturedSkybox::new));
    protected Texture texture;

    public SquareTexturedSkybox(Properties properties, Conditions conditions, Blend blend, Texture texture) {
        super(properties, conditions, blend);
        this.texture = texture;
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public void renderSkybox(SkyRendererAccessor skyRendererAccess, PoseStack matrices, float tickDelta, Camera camera, FogParameters fogParameters, Runnable runnable) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.setShaderTexture(0, this.texture.getTextureId());
        for (int face = 0; face < 6; face++) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            UVRange tex = Utils.TEXTURE_FACES[face];
            matrices.pushPose();

            if (face == 1) {
                matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            } else if (face == 2) {
                matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
            } else if (face == 3) {
                matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
            } else if (face == 4) {
                matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
            } else if (face == 5) {
                matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.last().pose();
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(tex.getMinU(), tex.getMinV());
            bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(tex.getMinU(), tex.getMaxV());
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(tex.getMaxU(), tex.getMaxV());
            bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(tex.getMaxU(), tex.getMinV());
            matrices.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    @Override
    public List<ResourceLocation> getTexturesToRegister() {
        return List.of(this.texture.getTextureId());
    }
}
