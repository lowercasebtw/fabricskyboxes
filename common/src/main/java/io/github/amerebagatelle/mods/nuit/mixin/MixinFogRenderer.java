package io.github.amerebagatelle.mods.nuit.mixin;

import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skybox.decorations.DecorationBox;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
    /**
     * Checks if we should change the fog color to whatever the skybox set it to, and sets it.
     */
//    @Inject(method = "computeFogColor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J", ordinal = 6))
//    private static void modifyColors(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfoReturnable<Vector4f> cir) {
//        int y = clientLevel.getSkyColor(camera.getPosition(), f);
//        float fogRed = ARGB.from8BitChannel(ARGB.red(y));
//        float fogGreen = ARGB.from8BitChannel(ARGB.green(y));
//        float fogBlue = ARGB.from8BitChannel(ARGB.blue(y));
//        RGB initialFogColor = new RGB(fogRed, fogGreen, fogBlue);
//        RGB fogColor = Utils.alphaBlendFogColors(SkyboxManager.getInstance().getActiveSkyboxes(), initialFogColor);
//        if (SkyboxManager.getInstance().isEnabled() && fogColor != initialFogColor) {
//            fogRed = fogColor.getRed();
//            fogBlue = fogColor.getBlue();
//            fogGreen = fogColor.getGreen();
//        }
//    }

//    @Redirect(method = "levelFogColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"))
//    private static void redirectSetShaderFogColor(float red, float green, float blue) {
//        float initialFogDensity = 1.0F; // Vanilla's default is 1F
//        float fogDensity = Utils.alphaBlendFogDensity(SkyboxManager.getInstance().getActiveSkyboxes(), initialFogDensity);
//        if (SkyboxManager.getInstance().isEnabled() && fogDensity != initialFogDensity) {
//            RenderSystem.setShaderFogColor(red, green, blue, fogDensity);
//        } else {
//            RenderSystem.setShaderFogColor(red, green, blue);
//        }
//    }

    @Redirect(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
    private static float nuit$redirectSkyAngle(ClientLevel instance, float v) {
        if (SkyboxManager.getInstance().isEnabled() && SkyboxManager.getInstance().getActiveSkyboxes().stream().anyMatch(skybox -> skybox instanceof DecorationBox decorBox && decorBox.getProperties().getRotation().getSkyboxRotation())) {
            return Mth.positiveModulo(instance.getDayTime() / 24000F + 0.75F, 1);
        }
        return instance.getTimeOfDay(v);
    }

    @Redirect(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSunAngle(F)F"))
    private static float nuit$redirectSkyAngleRadian(ClientLevel instance, float v) {
        if (SkyboxManager.getInstance().isEnabled() && SkyboxManager.getInstance().getActiveSkyboxes().stream().anyMatch(skybox -> skybox instanceof DecorationBox decorBox && decorBox.getProperties().getRotation().getSkyboxRotation())) {
            float skyAngle = Mth.positiveModulo(instance.getDayTime() / 24000F + 0.75F, 1);
            return skyAngle * (float) (Math.PI * 2);
        }
        return instance.getSunAngle(v);
    }

    @ModifyConstant(
            method = "computeFogColor",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;")),
            constant = @Constant(intValue = 4, ordinal = 0)
    )
    private static int renderSkyColor(int original) {
        Skybox skybox = SkyboxManager.getInstance().getCurrentSkybox();
        if (skybox instanceof NuitSkybox nuitSkybox) {
            if (!nuitSkybox.getProperties().isRenderSunSkyTint()) {
                return Integer.MAX_VALUE;
            }
        }
        return original;
    }
}
