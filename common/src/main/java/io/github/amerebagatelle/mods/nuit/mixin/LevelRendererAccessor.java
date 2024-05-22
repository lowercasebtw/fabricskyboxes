package io.github.amerebagatelle.mods.nuit.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("SUN_LOCATION")
    static ResourceLocation getSun() {
        throw new AssertionError();
    }

    @Accessor("MOON_LOCATION")
    static ResourceLocation getMoonPhases() {
        throw new AssertionError();
    }

    @Accessor("END_SKY_LOCATION")
    static ResourceLocation getEndSky() {
        throw new AssertionError();
    }

    @Accessor("skyBuffer")
    VertexBuffer getLightSkyBuffer();

    @Accessor("starBuffer")
    VertexBuffer getStarsBuffer();

    @Accessor("darkBuffer")
    VertexBuffer getDarkSkyBuffer();
}
