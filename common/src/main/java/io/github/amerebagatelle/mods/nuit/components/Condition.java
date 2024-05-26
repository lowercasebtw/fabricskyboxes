package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class Condition<T> {
    private final boolean excludes;
    private final List<T> entries;

    public Condition(boolean excludes, List<T> entries) {
        this.excludes = excludes;
        this.entries = entries;
    }

    public boolean isExcludes() {
        return excludes;
    }

    public List<T> getEntries() {
        return entries;
    }

    public static <T> Condition<T> of() {
        return new Condition<>(false, ObjectArrayList.of());
    }

    public static <T> Codec<Condition<T>> create(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("excludes", false).forGetter(Condition::isExcludes),
                codec.listOf().optionalFieldOf("entries", ObjectArrayList.of()).forGetter(Condition::getEntries)
        ).apply(instance, Condition::new));
    }
}
