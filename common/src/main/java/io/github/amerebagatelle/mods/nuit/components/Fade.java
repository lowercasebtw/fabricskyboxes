package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;

import java.util.Map;

public class Fade extends Keyable<Float> {
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn),
            CodecUtils.getClampedLong(1, Long.MAX_VALUE).optionalFieldOf("duration", 24000L).forGetter(Fade::getDuration),
            CodecUtils.unboundedMapFixed(Long.class, CodecUtils.getClampedFloat(0F, 1F), Long2FloatOpenHashMap::new)
                    .optionalFieldOf("keyFrames", CodecUtils.fastUtilLong2FloatOpenHashMap())
                    .forGetter(Fade::getKeyFrames)
    ).apply(instance, (alwaysOn1, duration, keyFrames) -> new Fade(duration, keyFrames, alwaysOn1)));
    private final boolean alwaysOn;

    public Fade(long duration, Map<Long, Float> keyFrames, boolean alwaysOn) {
        super(duration, keyFrames);
        this.alwaysOn = alwaysOn || keyFrames.isEmpty();
        validateKeyFrames();
    }

    private void validateKeyFrames() {
        // Validate that there is at least 1 keyframe if alwaysOn is false
        if (this.getKeyFrames().isEmpty() && !this.alwaysOn) {
            throw new IllegalArgumentException("Keyframes must have at least 1 entries");
        }

        // Validate that the keyframes are between 0 and the duration
        for (Long keyFrame : this.getKeyFrames().keySet()) {
            try {
                if (keyFrame < 0 || keyFrame >= this.getDuration()) {
                    throw new IllegalArgumentException("Keyframes must be between 0 and duration");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Keyframes must be numeric", e);
            }
        }
    }

    public boolean isAlwaysOn() {
        return this.alwaysOn;
    }

    public static Fade of() {
        return new Fade(24000L, CodecUtils.fastUtilLong2FloatOpenHashMap(), true);
    }
}