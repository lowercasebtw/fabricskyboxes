package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera camera);

    @Shadow
    @Final
    private SkyRenderer skyRenderer;

    @Shadow
    @Final
    private LevelTargetBundle targets;

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "addSkyPass", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickDelta, FogParameters fogParameters, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            FogType cameraSubmersionType = camera.getFluidInCamera();
            boolean thickFog = !this.doesMobEffectBlockSky(camera);
            boolean renderSky = !NuitClient.config().generalSettings.keepVanillaBehaviour || (thickFog && cameraSubmersionType != FogType.POWDER_SNOW && cameraSubmersionType != FogType.LAVA && cameraSubmersionType != FogType.WATER && !this.doesMobEffectBlockSky(camera));
            if (renderSky) {
                FramePass framePass = frameGraphBuilder.addPass("sky");
                this.targets.main = framePass.readsAndWrites(this.targets.main);
                framePass.executes(() -> {
                    RenderSystem.setShaderFog(fogParameters);
//                    RenderStateShard.MAIN_TARGET.setupRenderState();
                    PoseStack matrixStack = new PoseStack();
                    matrixStack.mulPose(RenderSystem.getModelViewMatrix());
                    skyboxManager.renderSkyboxes((SkyRendererAccessor) skyRenderer, matrixStack, RenderSystem.getProjectionMatrix(), tickDelta, camera, thickFog, FogRenderer::toggleFog);
                });
            }

            ci.cancel();
        }
    }
}
