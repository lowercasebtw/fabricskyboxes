package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Map;

public class Keyable<T> {
    private final long duration;
    private final Map<Long, T> keyFrames;

    public Keyable(long duration, Map<Long, T> keyFrames) {
        this.duration = duration;
        this.keyFrames = keyFrames;
    }

    public long getDuration() {
        return duration;
    }

    public Map<Long, T> getKeyFrames() {
        return keyFrames;
    }

    public static <T> Codec<Keyable<T>> create(long durationDefault, Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.optionalFieldOf("duration", durationDefault).forGetter(Keyable::getDuration),
                CodecUtils.unboundedMapFixed(Long.class, codec, Long2ObjectOpenHashMap::new)
                        .optionalFieldOf("keyFrames", CodecUtils.fastUtilLong2ObjectOpenHashMap())
                        .forGetter(Keyable::getKeyFrames)
        ).apply(instance, Keyable::new));
    }
}
