package io.github.amerebagatelle.mods.nuit.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.*;
import io.github.amerebagatelle.mods.nuit.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MultiTexturedSkybox extends TexturedSkybox<MultiTexturedSkybox> {
    public static Codec<MultiTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            AnimatableTexture.CODEC.listOf().optionalFieldOf("animatableTextures", new ArrayList<>()).forGetter(MultiTexturedSkybox::getAnimations)
    ).apply(instance, MultiTexturedSkybox::new));
    protected final List<AnimatableTexture> animatableTextures;

    private final float quadSize = 100F;
    private final UVRange quad = new UVRange(-this.quadSize, -this.quadSize, this.quadSize, this.quadSize);

    public MultiTexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, List<AnimatableTexture> animatableTextures) {
        super(properties, conditions, decorations, blend);
        this.animatableTextures = animatableTextures;
    }

    @Override
    public RegistrySupplier<SkyboxType<MultiTexturedSkybox>> getType() {
        return SkyboxType.MULTI_TEXTURE_SKYBOX;
    }

    @Override
    public void renderSkybox(LevelRendererAccessor worldRendererAccess, PoseStack matrices, float tickDelta, Camera camera, boolean thickFog, Runnable runnable) {
        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            // List of UV ranges for each face of the cube
            UVRange faceUVRange = new UVRange(0, 0, 1.0F / 3.0F, 1.0F / 2.0F);
            matrices.pushPose();

            if (i == 1) {
                faceUVRange = new UVRange(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1);
                matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
            } else if (i == 2) {
                faceUVRange = new UVRange(2.0F / 3.0F, 0, 1, 1.0F / 2.0F);
                matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
            } else if (i == 3) {
                faceUVRange = new UVRange(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F);
                matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
            } else if (i == 4) {
                faceUVRange = new UVRange(2.0F / 3.0F, 1.0F / 2.0F, 1, 1);
                matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
            } else if (i == 5) {
                faceUVRange = new UVRange(0, 1.0F / 2.0F, 1.0F / 3.0F, 1);
                matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.last().pose();

            // animations
            for (AnimatableTexture animatableTexture : this.animatableTextures) {
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuilder();

                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

                animatableTexture.tick();
                UVRange intersect = Utils.findUVIntersection(faceUVRange, animatableTexture.getUvRange()); // todo: cache this intersections so we don't waste gpu cycles
                if (intersect != null && animatableTexture.getCurrentFrame() != null) {
                    UVRange intersectionOnCurrentTexture = Utils.mapUVRanges(faceUVRange, this.quad, intersect);
                    UVRange intersectionOnCurrentFrame = Utils.mapUVRanges(animatableTexture.getUvRange(), animatableTexture.getCurrentFrame(), intersect);

                    // Render the quad at the calculated position
                    RenderSystem.setShaderTexture(0, animatableTexture.getTexture().getTextureId());

                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMinU(), -this.quadSize, intersectionOnCurrentTexture.getMinV()).uv(intersectionOnCurrentFrame.getMinU(), intersectionOnCurrentFrame.getMinV()).endVertex();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMinU(), -this.quadSize, intersectionOnCurrentTexture.getMaxV()).uv(intersectionOnCurrentFrame.getMinU(), intersectionOnCurrentFrame.getMaxV()).endVertex();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMaxU(), -this.quadSize, intersectionOnCurrentTexture.getMaxV()).uv(intersectionOnCurrentFrame.getMaxU(), intersectionOnCurrentFrame.getMaxV()).endVertex();
                    bufferBuilder.vertex(matrix4f, intersectionOnCurrentTexture.getMaxU(), -this.quadSize, intersectionOnCurrentTexture.getMinV()).uv(intersectionOnCurrentFrame.getMaxU(), intersectionOnCurrentFrame.getMinV()).endVertex();
                }
                BufferUploader.drawWithShader(bufferBuilder.end());
            }

            matrices.popPose();
        }
    }

    public List<AnimatableTexture> getAnimations() {
        return animatableTextures;
    }
}
