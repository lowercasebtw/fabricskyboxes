package io.github.amerebagatelle.mods.nuit.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static <K extends Number, V> Codec<Map<K, V>> unboundedMapFixed(Class<K> clazz, Codec<V> valueCodec, Map<K, V> mapImpl) {
        return Codec.unboundedMap(Codec.STRING, valueCodec).xmap(
                map -> map.entrySet().stream().collect(Collectors.toMap(
                        entry -> {
                            var key = entry.getKey();
                            if (clazz == Integer.class) {
                                return clazz.cast(Integer.parseInt(key));
                            } else if (clazz == Long.class) {
                                return clazz.cast(Long.parseLong(key));
                            } else if (clazz == Double.class) {
                                return clazz.cast(Double.parseDouble(key));
                            } else if (clazz == Float.class) {
                                return clazz.cast(Float.parseFloat(key));
                            } else if (clazz == Short.class) {
                                return clazz.cast(Short.parseShort(key));
                            } else if (clazz == Byte.class) {
                                return clazz.cast(Byte.parseByte(key));
                            } else {
                                throw new IllegalArgumentException("Unsupported number class: " + clazz);
                            }
                        },
                        Map.Entry::getValue,
                        (a, b) -> a,
                        () -> mapImpl
                )),
                map -> map.entrySet().stream().collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ))
        );
    }
}
