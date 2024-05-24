package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public record MinMaxEntry(float min, float max) {
    public static final Codec<MinMaxEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(MinMaxEntry::min),
            Codec.FLOAT.fieldOf("max").forGetter(MinMaxEntry::max)
    ).apply(instance, MinMaxEntry::new));

    public MinMaxEntry {
        if (min > max) {
            throw new IllegalStateException("Maximum value is lower than the minimum value:\n" + this);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
