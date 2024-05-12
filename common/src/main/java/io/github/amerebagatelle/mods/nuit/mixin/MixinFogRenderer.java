package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skybox.RGBA;
import io.github.amerebagatelle.mods.nuit.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.mods.nuit.util.FogRGBA;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {

    @Unique
    private static float density;

    @Unique
    private static boolean modifyDensity;

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    /**
     * Checks if we should change the fog color to whatever the skybox set it to, and sets it.
     */
    @Inject(method = "setupColor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J", ordinal = 6))
    private static void modifyColors(Camera camera, float tickDelta, ClientLevel world, int i, float f, CallbackInfo ci) {
        FogRGBA fogColor = Utils.alphaBlendFogColors(NuitApi.getInstance().getActiveSkyboxes(), new RGBA(fogRed, fogBlue, fogGreen));
        if (NuitApi.getInstance().isEnabled() && fogColor != null) {
            fogRed = fogColor.getRed();
            fogBlue = fogColor.getGreen();
            fogGreen = fogColor.getBlue();
            density = fogColor.getDensity();
            modifyDensity = fogColor.isModifyDensity();
        } else {
            modifyDensity = false;
        }
    }

    @Redirect(method = "levelFogColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogColor(FFF)V"), remap = false)
    private static void redirectSetShaderFogColor(float red, float green, float blue) {
        if (modifyDensity) {
            RenderSystem.setShaderFogColor(red, green, blue, density);
        } else {
            RenderSystem.setShaderFogColor(red, green, blue);
        }
    }

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"))
    private static float nuit$redirectSkyAngle(ClientLevel instance, float v) {
        if (NuitApi.getInstance().isEnabled() && NuitApi.getInstance().getActiveSkyboxes().stream().anyMatch(skybox -> skybox instanceof AbstractSkybox abstractSkybox && abstractSkybox.getDecorations().getRotation().getSkyboxRotation())) {
            return Mth.positiveModulo(instance.getDayTime() / 24000F + 0.75F, 1);
        }
        return instance.getTimeOfDay(v);
    }

    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSunAngle(F)F"))
    private static float nuit$redirectSkyAngleRadian(ClientLevel instance, float v) {
        if (NuitApi.getInstance().isEnabled() && NuitApi.getInstance().getActiveSkyboxes().stream().anyMatch(skybox -> skybox instanceof AbstractSkybox abstractSkybox && abstractSkybox.getDecorations().getRotation().getSkyboxRotation())) {
            float skyAngle = Mth.positiveModulo(instance.getDayTime() / 24000F + 0.75F, 1);
            return skyAngle * (float) (Math.PI * 2);
        }
        return instance.getSunAngle(v);
    }

    @ModifyConstant(
            method = "setupColor",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;")),
            constant = @Constant(intValue = 4, ordinal = 0)
    )
    private static int renderSkyColor(int original) {
        Skybox skybox = NuitApi.getInstance().getCurrentSkybox();
        if (skybox instanceof NuitSkybox nuitSkybox) {
            if (!nuitSkybox.getProperties().isRenderSunSkyTint()) {
                return Integer.MAX_VALUE;
            }
        }
        return original;
    }
}
