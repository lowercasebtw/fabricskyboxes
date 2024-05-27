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
            Codec.FLOAT.optionalFieldOf("rotationSpeedX", 0F).forGetter(Rotation::getRotationSpeedX),
            Codec.FLOAT.optionalFieldOf("rotationSpeedY", 0F).forGetter(Rotation::getRotationSpeedY),
            Codec.FLOAT.optionalFieldOf("rotationSpeedZ", 0F).forGetter(Rotation::getRotationSpeedZ)
    ).apply(instance, Rotation::new));
    private final boolean skyboxRotation;
    private final Map<Long, Quaternionf> keyframes;
    private final Vector3i timeShift;
    private final float rotationSpeedX;
    private final float rotationSpeedY;
    private final float rotationSpeedZ;

    public Rotation(boolean skyboxRotation, Map<Long, Quaternionf> keyframes, Vector3i timeShift, float rotationSpeedX, float rotationSpeedY, float rotationSpeedZ) {
        this.skyboxRotation = skyboxRotation;
        this.keyframes = keyframes;
        this.timeShift = timeShift;
        this.rotationSpeedX = rotationSpeedX;
        this.rotationSpeedY = rotationSpeedY;
        this.rotationSpeedZ = rotationSpeedZ;
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

    public float getRotationSpeedX() {
        return rotationSpeedX;
    }

    public float getRotationSpeedY() {
        return rotationSpeedY;
    }

    public float getRotationSpeedZ() {
        return rotationSpeedZ;
    }

    public static Rotation of() {
        return new Rotation(true, Map.of(0L, new Quaternionf()), new Vector3i(0, 0, 0), 0, 0, 0);
    }

    public static Rotation decorations() {
        return new Rotation(false, Map.of(0L, new Quaternionf()), new Vector3i(0, 0, 0), 0, 0, 1);
    }
}

