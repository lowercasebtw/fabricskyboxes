package io.github.amerebagatelle.mods.nuit.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.util.CodecUtils;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class Rotation {
    private static final Codec<Quaternionf> QUAT_FROM_VEC_3_F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        if (list.size() != 3) {
            return DataResult.error(() -> "Invalid number of elements in vector");
        }
        return DataResult.success(new Quaternionf().rotateXYZ((float) Math.toRadians(list.get(0)), (float) Math.toRadians(list.get(1)), (float) Math.toRadians(list.get(2))));
    }, (vec) -> ImmutableList.of(vec.x(), vec.y(), vec.z()));
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("skyboxRotation", true).forGetter(Rotation::getSkyboxRotation),
            CodecUtils.unboundedMapFixed(Long.class, QUAT_FROM_VEC_3_F, HashMap::new)
                    .optionalFieldOf("mapping", Map.of())
                    .forGetter(Rotation::getMapping),
            CodecUtils.unboundedMapFixed(Long.class, QUAT_FROM_VEC_3_F, HashMap::new)
                    .optionalFieldOf("axis", Map.of())
                    .forGetter(Rotation::getAxis),
            Codec.LONG.optionalFieldOf("duration", 24000L).forGetter(Rotation::getRotationDuration),
            Codec.FLOAT.optionalFieldOf("speed", 1f).forGetter(Rotation::getSpeed)
    ).apply(instance, Rotation::new));
    private final boolean skyboxRotation;
    private final Map<Long, Quaternionf> mapping, axis;
    private final long rotationDuration;
    private final float speed;

    public Rotation(boolean skyboxRotation, Map<Long, Quaternionf> mapping, Map<Long, Quaternionf> axis, long rotationDuration, float speed) {
        this.skyboxRotation = skyboxRotation;
        this.mapping = mapping;
        this.axis = axis;
        this.rotationDuration = rotationDuration;
        this.speed = speed;
    }

    public boolean getSkyboxRotation() {
        return skyboxRotation;
    }

    public Map<Long, Quaternionf> getMapping() {
        return this.mapping;
    }

    public Map<Long, Quaternionf> getAxis() {
        return this.axis;
    }

    public float getSpeed() {
        return this.speed;
    }

    public long getRotationDuration() {
        return rotationDuration;
    }

    public static Rotation of() {
        return new Rotation(true, Map.of(0L, new Quaternionf()), Map.of(), 24000L, 1f);
    }

    public static Rotation decorations() {
        return new Rotation(false, Map.of(0L, new Quaternionf()), Map.of(), 24000L, 1f);
    }
}

