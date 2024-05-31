package io.github.amerebagatelle.mods.nuit.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.joml.Quaternionf;

import java.util.Map;

public class Rotation extends Keyable<Quaternionf> {
    private static final Codec<Quaternionf> QUAT_FROM_VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() != 3) {
            return DataResult.error(() -> "Invalid number of elements in vector");
        }
        return DataResult.success(new Quaternionf().rotateXYZ((float) Math.toRadians(list.get(0)), (float) Math.toRadians(list.get(1)), (float) Math.toRadians(list.get(2))));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("skyboxRotation", true).forGetter(Rotation::getSkyboxRotation),
            CodecUtils.unboundedMapFixed(Long.class, QUAT_FROM_VEC_3_F, Long2ObjectOpenHashMap::new)
                    .optionalFieldOf("keyFrames", CodecUtils.fastUtilLong2ObjectOpenHashMap())
                    .forGetter(Rotation::getKeyFrames),
            Codec.LONG.optionalFieldOf("duration", 24000L).forGetter(Rotation::getDuration)
    ).apply(instance, (skyboxRotation1, keyframes, rotationDuration) -> new Rotation(rotationDuration, keyframes, skyboxRotation1)));
    private final boolean skyboxRotation;

    public Rotation(long duration, Map<Long, Quaternionf> keyFrames, boolean skyboxRotation) {
        super(duration, keyFrames);
        this.skyboxRotation = skyboxRotation;
    }

    public boolean getSkyboxRotation() {
        return skyboxRotation;
    }

    public static Rotation of() {
        return new Rotation(24000, CodecUtils.fastUtilLong2ObjectOpenHashMap(), true);
    }

    public static Rotation decorations() {
        return new Rotation(24000, CodecUtils.fastUtilLong2ObjectOpenHashMap(), false);
    }
}

