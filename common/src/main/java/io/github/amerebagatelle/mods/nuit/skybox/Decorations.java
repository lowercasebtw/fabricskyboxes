package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * The moon texture must be a 4 wide, 2 high, stacked texture.
 * This is due to the fact that the moon is rendered with a
 * different u/v value depending on the moon phase.
 */
public class Decorations {
    public static final ResourceLocation MOON_PHASES = new ResourceLocation("textures/environment/moon_phases.png");
    public static final ResourceLocation SUN = new ResourceLocation("textures/environment/sun.png");
    public static final Codec<Decorations> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("sun", SUN).forGetter(Decorations::getSunTexture),
            ResourceLocation.CODEC.optionalFieldOf("moon", MOON_PHASES).forGetter(Decorations::getMoonTexture),
            Codec.BOOL.optionalFieldOf("showSun", false).forGetter(Decorations::isSunEnabled),
            Codec.BOOL.optionalFieldOf("showMoon", false).forGetter(Decorations::isMoonEnabled),
            Codec.BOOL.optionalFieldOf("showStars", false).forGetter(Decorations::isStarsEnabled),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DECORATIONS).forGetter(Decorations::getRotation),
            Blend.CODEC.optionalFieldOf("blend", Blend.DECORATIONS).forGetter(Decorations::getBlend)
    ).apply(instance, Decorations::new));
    public static final Decorations DEFAULT = new Decorations(SUN, MOON_PHASES, false, false, false, Rotation.DEFAULT, Blend.DECORATIONS);
    private final ResourceLocation sunTexture;
    private final ResourceLocation moonTexture;
    private final boolean sunEnabled;
    private final boolean moonEnabled;
    private final boolean starsEnabled;
    private final Rotation rotation;
    private final Blend blend;

    public Decorations(ResourceLocation sun, ResourceLocation moon, boolean sunEnabled, boolean moonEnabled, boolean starsEnabled, Rotation rotation, Blend blend) {
        this.sunTexture = sun;
        this.moonTexture = moon;
        this.sunEnabled = sunEnabled;
        this.moonEnabled = moonEnabled;
        this.starsEnabled = starsEnabled;
        this.rotation = rotation;
        this.blend = blend;
    }

    public ResourceLocation getSunTexture() {
        return this.sunTexture;
    }

    public ResourceLocation getMoonTexture() {
        return this.moonTexture;
    }

    public boolean isSunEnabled() {
        return this.sunEnabled;
    }

    public boolean isMoonEnabled() {
        return this.moonEnabled;
    }

    public boolean isStarsEnabled() {
        return this.starsEnabled;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Blend getBlend() {
        return blend;
    }
}

