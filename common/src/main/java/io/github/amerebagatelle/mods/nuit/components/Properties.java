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
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter(Properties::isChangeFog),
            Codec.BOOL.optionalFieldOf("changeFogDensity", false).forGetter(Properties::isChangeFogDensity),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.DEFAULT).forGetter(Properties::getFogColors),
            Codec.BOOL.optionalFieldOf("sunSkyTint", true).forGetter(Properties::isRenderSunSkyTint),
            Codec.BOOL.optionalFieldOf("inThickFog", true).forGetter(Properties::isRenderInThickFog),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DEFAULT).forGetter(Properties::getRotation)
    ).apply(instance, Properties::new));

    public static final Properties DEFAULT = new Properties(0, Fade.DEFAULT, 20, 20, false, false, RGBA.DEFAULT, true, true, Rotation.DEFAULT);

    private final int layer;
    private final Fade fade;
    private final int transitionInDuration;
    private final int transitionOutDuration;
    private final boolean changeFog;
    private final boolean changeFogDensity;
    private final RGBA fogColors;
    private final boolean renderSunSkyTint;
    private final boolean renderInThickFog;
    private final Rotation rotation;

    public Properties(int layer, Fade fade, int transitionInDuration, int transitionOutDuration, boolean changeFog, boolean changeFogDensity, RGBA fogColors, boolean renderSunSkyTint, boolean renderInThickFog, Rotation rotation) {
        this.layer = layer;
        this.fade = fade;
        this.transitionInDuration = transitionInDuration;
        this.transitionOutDuration = transitionOutDuration;
        this.changeFog = changeFog;
        this.changeFogDensity = changeFogDensity;
        this.fogColors = fogColors;
        this.renderSunSkyTint = renderSunSkyTint;
        this.renderInThickFog = renderInThickFog;
        this.rotation = rotation;
    }

    public int getLayer() {
        return layer;
    }

    public Fade getFade() {
        return this.fade;
    }

    public int getTransitionInDuration() {
        return transitionInDuration;
    }

    public int getTransitionOutDuration() {
        return transitionOutDuration;
    }

    public boolean isChangeFog() {
        return this.changeFog;
    }

    public boolean isChangeFogDensity() {
        return changeFogDensity;
    }

    public RGBA getFogColors() {
        return this.fogColors;
    }

    public boolean isRenderSunSkyTint() {
        return renderSunSkyTint;
    }

    public boolean isRenderInThickFog() {
        return renderInThickFog;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
