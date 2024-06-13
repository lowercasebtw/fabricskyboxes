package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class, priority = 900)
public abstract class SkyboxRenderMixin {

    @Shadow
    protected abstract boolean hasBlindnessOrDarkness(Camera camera);

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
            boolean renderSky = !FabricSkyBoxesClient.config().generalSettings.keepVanillaBehaviour || (!thickFog && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.WATER && !this.hasBlindnessOrDarkness(camera));
            if (renderSky) {
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.multiplyPositionMatrix(matrix4f);
                skyboxManager.renderSkyboxes((WorldRendererAccess) this, matrixStack, projectionMatrix, tickDelta, camera, thickFog, fogCallback);
            }
            ci.cancel();
        }
    }
}
