package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Fade {
    public static final Fade DEFAULT = new Fade(false, 0, 0, 0, 0, false, 24000L, new LinkedHashMap<>());
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("advanced", false).forGetter(Fade::isAdvanced),
            Codec.INT.optionalFieldOf("startFadeIn", 0).forGetter(Fade::getStartFadeIn),
            Codec.INT.optionalFieldOf("endFadeIn", 0).forGetter(Fade::getEndFadeIn),
            Codec.INT.optionalFieldOf("startFadeOut", 0).forGetter(Fade::getStartFadeOut),
            Codec.INT.optionalFieldOf("endFadeOut", 0).forGetter(Fade::getEndFadeOut),
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn),
            Codec.LONG.optionalFieldOf("duration", 24000L).forGetter(Fade::getDuration),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
                    .optionalFieldOf("keyFrames", new LinkedHashMap<>())
                    .forGetter(Fade::getKeyFramesParsed)
    ).apply(instance, Fade::new));
    private final boolean advanced;
    private final int startFadeIn;
    private final int endFadeIn;
    private final int startFadeOut;
    private final int endFadeOut;
    private final boolean alwaysOn;
    private final long duration;
    private final Map<String, Float> keyFramesParsed;

    private final Map<Long, Float> keyFrames;

    public Fade(boolean advanced, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut, boolean alwaysOn, long duration, Map<String, Float> keyFramesParsed) {
        this.advanced = advanced;
        this.startFadeIn = normalizeAndWarnIfDifferent(startFadeIn, alwaysOn);
        this.endFadeIn = normalizeAndWarnIfDifferent(endFadeIn, alwaysOn);
        this.startFadeOut = normalizeAndWarnIfDifferent(startFadeOut, alwaysOn);
        this.endFadeOut = normalizeAndWarnIfDifferent(endFadeOut, alwaysOn);
        validateDuration(duration);
        this.alwaysOn = alwaysOn || (keyFramesParsed.isEmpty() && advanced);
        this.duration = duration;
        this.keyFramesParsed = keyFramesParsed;
        this.keyFrames = parseKeyFrames(keyFramesParsed);
        validateKeyFrames();
    }

    private static int normalizeAndWarnIfDifferent(int time, boolean ignore) {
        if (ignore) {
            return time;
        }
        int normalized = Utils.normalizeTickTime(time);
        return Utils.warnIfDifferent(time, normalized, String.format("Provided time of %s has been normalized to %s", time, normalized));
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
        if (this.keyFrames.size() < 2 && !this.alwaysOn && this.advanced) {
            throw new IllegalArgumentException("Keyframes must have at least 2 entries");
        }
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public int getStartFadeIn() {
        return startFadeIn;
    }

    public int getEndFadeIn() {
        return endFadeIn;
    }

    public int getStartFadeOut() {
        return startFadeOut;
    }

    public int getEndFadeOut() {
        return endFadeOut;
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