package io.github.amerebagatelle.mods.nuit.skybox.decorations;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.components.Blend;
import io.github.amerebagatelle.mods.nuit.components.Conditions;
import io.github.amerebagatelle.mods.nuit.components.Decorations;
import io.github.amerebagatelle.mods.nuit.components.Properties;
import io.github.amerebagatelle.mods.nuit.mixin.LevelRendererAccessor;
import io.github.amerebagatelle.mods.nuit.skybox.AbstractSkybox;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class DecorationBox extends AbstractSkybox {
    public static Codec<DecorationBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(DecorationBox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.of()).forGetter(DecorationBox::getConditions),
            Decorations.CODEC.fieldOf("decorations").forGetter(DecorationBox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.decorations()).forGetter(DecorationBox::getBlend)
    ).apply(instance, DecorationBox::new));

    private Decorations decorations;
    private Blend blend;

    protected DecorationBox() {
    }

    public DecorationBox(Properties properties, Conditions conditions, Decorations decorations, Blend blend) {
        this.properties = properties;
        this.conditions = conditions;
        this.decorations = decorations;
        this.blend = blend;
    }

    @Override
    public void render(LevelRendererAccessor levelRendererAccessor, PoseStack poseStack, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        RenderSystem.enableBlend();
        var world = Minecraft.getInstance().level;
        assert world != null;

        // Custom Blender
        this.blend.applyBlendFunc(this.alpha);
        poseStack.pushPose();

        // static
        this.properties.getRotation().rotateStack(poseStack, world);

        // Iris Compat
        //poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(IrisCompat.getSunPathRotation()));
        //poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(world.getSkyAngle(tickDelta) * 360.0F * this.decorations.getRotation().getRotationSpeed()));

        Matrix4f matrix4f2 = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        // Sun
        if (this.decorations.isSunEnabled()) {
            this.renderSun(bufferBuilder, matrix4f2);
        }
        // Moon
        if (this.decorations.isMoonEnabled()) {
            this.renderMoon(bufferBuilder, matrix4f2);
        }
        // Stars
        if (this.decorations.isStarsEnabled()) {
            this.renderStars(levelRendererAccessor, tickDelta, poseStack, matrix4f);
        }
        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public void renderSun(BufferBuilder bufferBuilder, Matrix4f matrix4f) {
        RenderSystem.setShaderTexture(0, this.decorations.getSunTexture());
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, -30.0F).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, -30.0F).uv(1.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f, 30.0F, 100.0F, 30.0F).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, -30.0F, 100.0F, 30.0F).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public void renderMoon(BufferBuilder bufferBuilder, Matrix4f matrix4f) {
        RenderSystem.setShaderTexture(0, this.decorations.getMoonTexture());
        int moonPhase = Minecraft.getInstance().level.getMoonPhase();
        int xCoord = moonPhase % 4;
        int yCoord = moonPhase / 4 % 2;
        float startX = xCoord / 4.0F;
        float startY = yCoord / 2.0F;
        float endX = (xCoord + 1) / 4.0F;
        float endY = (yCoord + 1) / 2.0F;
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -20.0F, -100.0F, 20.0F).uv(endX, endY).endVertex();
        bufferBuilder.vertex(matrix4f, 20.0F, -100.0F, 20.0F).uv(startX, endY).endVertex();
        bufferBuilder.vertex(matrix4f, 20.0F, -100.0F, -20.0F).uv(startX, startY).endVertex();
        bufferBuilder.vertex(matrix4f, -20.0F, -100.0F, -20.0F).uv(endX, startY).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public void renderStars(LevelRendererAccessor levelRendererAccessor, float tickDelta, PoseStack poseStack, Matrix4f matrix4f) {
        ClientLevel world = Minecraft.getInstance().level;
        float i = 1.0F - world.getRainLevel(tickDelta);
        float brightness = world.getStarBrightness(tickDelta) * i;
        if (brightness > 0.0F) {
            RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
            FogRenderer.setupNoFog();
            levelRendererAccessor.getStarsBuffer().bind();
            levelRendererAccessor.getStarsBuffer().drawWithShader(poseStack.last().pose(), matrix4f, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
        }
    }

    public Decorations getDecorations() {
        return decorations;
    }

    public Blend getBlend() {
        return this.blend;
    }
}