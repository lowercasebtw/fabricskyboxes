package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Fade {
    public static final Fade DEFAULT = new Fade(false, 24000L, new LinkedHashMap<>());
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn),
            Codec.LONG.optionalFieldOf("duration", 24000L).forGetter(Fade::getDuration),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
                    .optionalFieldOf("keyFrames", new LinkedHashMap<>())
                    .forGetter(Fade::getKeyFramesParsed)
    ).apply(instance, Fade::new));
    private final boolean alwaysOn;
    private final long duration;
    private final Map<String, Float> keyFramesParsed;

    private final Map<Long, Float> keyFrames;

    public Fade(boolean alwaysOn, long duration, Map<String, Float> keyFramesParsed) {
        validateDuration(duration);
        this.alwaysOn = alwaysOn || keyFramesParsed.isEmpty();
        this.duration = duration;
        this.keyFramesParsed = keyFramesParsed;
        this.keyFrames = parseKeyFrames(keyFramesParsed);
        validateKeyFrames();
    }

    private void validateDuration(long duration) {
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be greater than or equal to 1");
        }
    }

    private Map<Long, Float> parseKeyFrames(Map<String, Float> keyFramesParsed) {
        Map<Long, Float> parsedKeyFrames = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : keyFramesParsed.entrySet()) {
            try {
                long keyFrame = Long.parseLong(entry.getKey());
                if (keyFrame < 0 || keyFrame >= this.duration) {
                    throw new IllegalArgumentException("Keyframes must be between 0 and duration");
                }
                parsedKeyFrames.put(keyFrame, entry.getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Keyframes must be numeric", e);
            }
        }
        return parsedKeyFrames;
    }

    private void validateKeyFrames() {
        if (this.keyFrames.size() < 2 && !this.alwaysOn) {
            throw new IllegalArgumentException("Keyframes must have at least 2 entries");
        }
    }

    public boolean isAlwaysOn() {
        return this.alwaysOn;
    }

    public long getDuration() {
        return this.duration;
    }

    public Map<String, Float> getKeyFramesParsed() {
        return this.keyFramesParsed;
    }

    public Map<Long, Float> getKeyFrames() {
        return this.keyFrames;
    }
}