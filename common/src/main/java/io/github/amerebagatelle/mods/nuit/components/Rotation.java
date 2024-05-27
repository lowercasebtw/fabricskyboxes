package io.github.amerebagatelle.mods.nuit.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import org.joml.Quaternionf;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class Rotation {
    private static final Codec<Quaternionf> QUAT_FROM_VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() != 3) {
            return DataResult.error(() -> "Invalid number of elements in vector");
        }
        return DataResult.success(new Quaternionf().rotateXYZ(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    private static final Codec<Vector3i> VEC_3_I = Codec.INT.listOf().comapFlatMap((list) -> {
        if (list.size() != 3) {
            return DataResult.error(() -> "Invalid number of elements in vector");
        }
        return DataResult.success(new Vector3i(list.get(0), list.get(1), list.get(2)));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("skyboxRotation", true).forGetter(Rotation::getSkyboxRotation),
            CodecUtils.unboundedMapFixed(Long.class, QUAT_FROM_VEC_3_F, HashMap::new)
                    .optionalFieldOf("keyFrames", new HashMap<>())
                    .forGetter(Rotation::getKeyframes),
            VEC_3_I.optionalFieldOf("timeShift", new Vector3i(0, 0, 0)).forGetter(Rotation::getTimeShift),
            Codec.LONG.optionalFieldOf("rotationDuration", 24000L).forGetter(Rotation::getRotationDuration)
    ).apply(instance, Rotation::new));
    private final boolean skyboxRotation;
    private final Map<Long, Quaternionf> keyframes;
    private final Vector3i timeShift;
    private final long rotationDuration;

    public Rotation(boolean skyboxRotation, Map<Long, Quaternionf> keyframes, Vector3i timeShift, long rotationDuration) {
        this.skyboxRotation = skyboxRotation;
        this.keyframes = keyframes;
        this.timeShift = timeShift;
        this.rotationDuration = rotationDuration;
    }

    public boolean getSkyboxRotation() {
        return skyboxRotation;
    }

    public Map<Long, Quaternionf> getKeyframes() {
        return this.keyframes;
    }

    public Vector3i getTimeShift() {
        return timeShift;
    }

    public long getRotationDuration() {
        return rotationDuration;
    }

    public static Rotation of() {
        return new Rotation(true, Map.of(0L, new Quaternionf()), new Vector3i(0, 0, 0), 24000);
    }

    public static Rotation decorations() {
        return new Rotation(false, Map.of(0L, new Quaternionf()), new Vector3i(0, 0, 0), 24000);
    }
}

