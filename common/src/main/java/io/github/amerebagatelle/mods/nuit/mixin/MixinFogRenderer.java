package io.github.amerebagatelle.mods.nuit.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.amerebagatelle.mods.nuit.SkyboxManager;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.components.RGB;
import io.github.amerebagatelle.mods.nuit.skybox.decorations.DecorationBox;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {
    @Unique
    private static float nuit$fogRed;

    @Unique
    private static float nuit$fogGreen;

    @Unique
    private static float nuit$fogBlue;

    /**
     * Checks if we should change the fog color to whatever the skybox set it to, and sets it.
     */
    @Inject(method = "computeFogColor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J", ordinal = 6))
    private static void modifyColors(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfoReturnable<Vector4f> cir) {
        int y = clientLevel.getSkyColor(camera.getPosition(), f);
        float fogRed = ARGB.from8BitChannel(ARGB.red(y));
        float fogGreen = ARGB.from8BitChannel(ARGB.green(y));
        float fogBlue = ARGB.from8BitChannel(ARGB.blue(y));
        RGB initialFogColor = new RGB(fogRed, fogGreen, fogBlue);
        RGB fogColor = Utils.alphaBlendFogColors(SkyboxManager.getInstance().getActiveSkyboxes(), initialFogColor);
        if (SkyboxManager.getInstance().isEnabled() && fogColor != initialFogColor) {
            nuit$fogRed = fogColor.getRed();
            nuit$fogBlue = fogColor.getBlue();
            nuit$fogGreen = fogColor.getGreen();
        }
    }

    @ModifyReturnValue(method = "setupFog", at = @At(value = "RETURN"))
    private static FogParameters redirectSetShaderFogColor(FogParameters original) {
        float fogDensity = Utils.alphaBlendFogDensity(SkyboxManager.getInstance().getActiveSkyboxes(), original.alpha());
        return new FogParameters(original.start(), original.end(), original.shape(), nuit$fogRed, nuit$fogBlue, nuit$fogGreen, fogDensity);
    }

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
