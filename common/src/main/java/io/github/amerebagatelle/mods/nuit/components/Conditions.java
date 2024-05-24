package io.github.amerebagatelle.mods.nuit.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class Conditions {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(Conditions::getBiomes),
            ResourceLocation.CODEC.listOf().optionalFieldOf("worlds", ImmutableList.of()).forGetter(Conditions::getWorlds),
            ResourceLocation.CODEC.listOf().optionalFieldOf("dimensions", ImmutableList.of()).forGetter(Conditions::getDimensions),
            ResourceLocation.CODEC.listOf().optionalFieldOf("effects", ImmutableList.of()).forGetter(Conditions::getEffects),
            Weather.CODEC.listOf().optionalFieldOf("weather", ImmutableList.of()).forGetter(Conditions::getWeathers),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("xRanges", ImmutableList.of()).forGetter(Conditions::getXRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("yRanges", ImmutableList.of()).forGetter(Conditions::getYRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("zRanges", ImmutableList.of()).forGetter(Conditions::getZRanges)
    ).apply(instance, Conditions::new));
    public static final Conditions DEFAULT = new Conditions(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    private final List<ResourceLocation> biomes;
    private final List<ResourceLocation> worlds;
    private final List<ResourceLocation> dimensions;
    private final List<ResourceLocation> effects;
    private final List<Weather> weathers;
    private final List<MinMaxEntry> yRanges;
    private final List<MinMaxEntry> zRanges;
    private final List<MinMaxEntry> xRanges;

    public Conditions(List<ResourceLocation> biomes, List<ResourceLocation> worlds, List<ResourceLocation> dimensions, List<ResourceLocation> effects, List<Weather> weathers, List<MinMaxEntry> xRanges, List<MinMaxEntry> yRanges, List<MinMaxEntry> zRanges) {
        this.biomes = biomes;
        this.worlds = worlds;
        this.dimensions = dimensions;
        this.effects = effects;
        this.weathers = weathers;
        this.xRanges = xRanges;
        this.yRanges = yRanges;
        this.zRanges = zRanges;
    }

    public List<ResourceLocation> getBiomes() {
        return this.biomes;
    }

    public List<ResourceLocation> getWorlds() {
        return this.worlds;
    }

    public List<ResourceLocation> getDimensions() {
        return dimensions;
    }

    public List<ResourceLocation> getEffects() {
        return effects;
    }

    public List<Weather> getWeathers() {
        return this.weathers;
    }

    public List<MinMaxEntry> getYRanges() {
        return this.yRanges;
    }

    public List<MinMaxEntry> getXRanges() {
        return this.xRanges;
    }

    public List<MinMaxEntry> getZRanges() {
        return this.zRanges;
    }
}
