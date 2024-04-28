package io.github.amerebagatelle.mods.nuit.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class CodecUtils {

    public static Codec<Long> getClampedLong(long min, long max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.LONG.xmap(f -> Mth.clamp(f, min, max), Function.identity());
    }

    public static Codec<Integer> getClampedInteger(int min, int max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.INT.xmap(f -> Mth.clamp(f, min, max), Function.identity());
    }

    public static Codec<Float> getClampedFloat(float min, float max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.FLOAT.xmap(f -> Mth.clamp(f, min, max), Function.identity());
    }

    public static Codec<Double> getClampedDouble(double min, double max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        }
        return Codec.DOUBLE.xmap(f -> Mth.clamp(f, min, max), Function.identity());
    }
}
