package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Properties {
    public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("layer", 0).forGetter(Properties::getLayer),
            Fade.CODEC.fieldOf("fade").forGetter(Properties::getFade),
            CodecUtils.getClampedInteger(1, Integer.MAX_VALUE).optionalFieldOf("transitionInDuration", 20).forGetter(Properties::getTransitionInDuration),
            CodecUtils.getClampedInteger(1, Integer.MAX_VALUE).optionalFieldOf("transitionOutDuration", 20).forGetter(Properties::getTransitionOutDuration),
            Fog.CODEC.optionalFieldOf("fog", Fog.of()).forGetter(Properties::getFog),
            Codec.BOOL.optionalFieldOf("sunSkyTint", true).forGetter(Properties::isRenderSunSkyTint),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.of()).forGetter(Properties::getRotation)
    ).apply(instance, Properties::new));

    private final int layer;
    private final Fade fade;
    private final int transitionInDuration;
    private final int transitionOutDuration;
    private final Fog fog;
    private final boolean renderSunSkyTint;
    private final Rotation rotation;

    public Properties(int layer, Fade fade, int transitionInDuration, int transitionOutDuration, Fog fog, boolean renderSunSkyTint, Rotation rotation) {
        this.layer = layer;
        this.fade = fade;
        this.transitionInDuration = transitionInDuration;
        this.transitionOutDuration = transitionOutDuration;
        this.fog = fog;
        this.renderSunSkyTint = renderSunSkyTint;
        this.rotation = rotation;
    }

    public int getLayer() {
        return this.layer;
    }

    public Fade getFade() {
        return this.fade;
    }

    public int getTransitionInDuration() {
        return this.transitionInDuration;
    }

    public int getTransitionOutDuration() {
        return this.transitionOutDuration;
    }

    public Fog getFog() {
        return this.fog;
    }

    public boolean isRenderSunSkyTint() {
        return this.renderSunSkyTint;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Properties of() {
        return new Properties(0, Fade.of(), 20, 20, Fog.of(), true, Rotation.of());
    }

    public static Properties decorations() {
        return new Properties(0, Fade.of(), 20, 20, Fog.of(), true, Rotation.decorations());
    }
}
