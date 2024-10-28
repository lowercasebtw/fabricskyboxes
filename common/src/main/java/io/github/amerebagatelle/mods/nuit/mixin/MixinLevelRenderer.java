package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    @Final
    private SkyRenderer skyRenderer;

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderStateShard$OutputStateShard;setupRenderState()V", shift = At.Shift.AFTER), cancellable = true)
    private void renderCustomSkyboxes(FogParameters fogParameters, DimensionSpecialEffects.SkyType skyType, float f, DimensionSpecialEffects dimensionSpecialEffects, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            boolean renderSky = !NuitClient.config().generalSettings.keepVanillaBehaviour;
            if (renderSky) {
                PoseStack matrixStack = new PoseStack();
                matrixStack.mulPose(RenderSystem.getModelViewMatrix());
                skyboxManager.renderSkyboxes(
                        (SkyRendererAccessor) skyRenderer,
                        matrixStack,
                        RenderSystem.getProjectionMatrix(),
                        f,
                        Minecraft.getInstance().gameRenderer.getMainCamera(),
                        fogParameters,
                        FogRenderer::toggleFog
                );
            }
            ci.cancel();
        }
    }
}
