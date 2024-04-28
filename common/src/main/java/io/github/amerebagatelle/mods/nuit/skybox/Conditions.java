package io.github.amerebagatelle.mods.nuit.skybox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
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
    public static final Conditions DEFAULT = new Builder().build();
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

    public static class Builder {
        private final List<ResourceLocation> biomes = Lists.newArrayList();
        private final List<ResourceLocation> worlds = Lists.newArrayList();
        private final List<ResourceLocation> dimensions = Lists.newArrayList();
        private final List<ResourceLocation> effects = Lists.newArrayList();
        private final List<Weather> weathers = Lists.newArrayList();
        private final List<MinMaxEntry> yRanges = Lists.newArrayList();
        private final List<MinMaxEntry> zRanges = Lists.newArrayList();
        private final List<MinMaxEntry> xRanges = Lists.newArrayList();

        public Builder biomes(Collection<ResourceLocation> biomeIds) {
            this.biomes.addAll(biomeIds);
            return this;
        }

        public Builder worlds(Collection<ResourceLocation> worldIds) {
            this.worlds.addAll(worldIds);
            return this;
        }

        public Builder dimensions(Collection<ResourceLocation> dimensionIds) {
            this.dimensions.addAll(dimensionIds);
            return this;
        }

        public Builder effects(Collection<ResourceLocation> effectIds) {
            this.effects.addAll(effectIds);
            return this;
        }

        public Builder weather(Collection<Weather> weathers) {
            this.weathers.addAll(weathers);
            return this;
        }

        public Builder yRanges(Collection<MinMaxEntry> heights) {
            this.yRanges.addAll(heights);
            return this;
        }

        public Builder zRanges(Collection<MinMaxEntry> zRanges) {
            this.zRanges.addAll(zRanges);
            return this;
        }

        public Builder xRanges(Collection<MinMaxEntry> xRanges) {
            this.xRanges.addAll(xRanges);
            return this;
        }

        public Builder biomes(ResourceLocation... biomeIds) {
            return this.biomes(Lists.newArrayList(biomeIds));
        }

        public Builder worlds(ResourceLocation... worldIds) {
            return this.worlds(Lists.newArrayList(worldIds));
        }

        public Builder weather(Weather... weathers) {
            return this.weather(Lists.newArrayList(weathers));
        }

        public Builder xRanges(MinMaxEntry... xRanges) {
            return this.xRanges(Lists.newArrayList(xRanges));
        }

        public Builder yRanges(MinMaxEntry... yRanges) {
            return this.yRanges(Lists.newArrayList(yRanges));
        }

        public Builder zRanges(MinMaxEntry... zRanges) {
            return this.zRanges(Lists.newArrayList(zRanges));
        }

        public Conditions build() {
            return new Conditions(this.biomes, this.worlds, this.dimensions, this.effects, this.weathers, this.xRanges, this.yRanges, this.zRanges);
        }
    }
}
