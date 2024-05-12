package io.github.amerebagatelle.mods.nuit.skyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.textured.MultiTexturedSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.vanilla.EndSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.vanilla.OverworldSkybox;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class SkyboxType<T extends Skybox> {
    public static final Codec<ResourceLocation> SKYBOX_ID_CODEC;
    public static final ResourceKey<Registry<SkyboxType<? extends Skybox>>> SKYBOX_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(NuitClient.MOD_ID, "skybox_type"));

    // Vanilla skyboxes
    public static final SkyboxType<OverworldSkybox> OVERWORLD;
    public static final SkyboxType<EndSkybox> END;

    // FSB skyboxes
    public static final SkyboxType<MonoColorSkybox> MONO_COLOR_SKYBOX;
    public static final SkyboxType<SquareTexturedSkybox> SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<MultiTexturedSkybox> MULTI_TEXTURED_SKYBOX;

    static {
        SKYBOX_ID_CODEC = Codec.STRING.xmap((s) -> {
            if (!s.contains(":")) {
                return new ResourceLocation(NuitClient.MOD_ID, s.replace('-', '_'));
            }
            return new ResourceLocation(s.replace('-', '_'));
        }, (id) -> {
            if (id.getNamespace().equals(NuitClient.MOD_ID)) {
                return id.getPath().replace('_', '-');
            }
            return id.toString().replace('_', '-');
        });


        // Vanilla skyboxes
        OVERWORLD = SkyboxType.Builder.create(OverworldSkybox.class, "overworld").add(1, OverworldSkybox.CODEC).build();
        END = SkyboxType.Builder.create(EndSkybox.class, "end").add(1, EndSkybox.CODEC).build();

        // FSB skyboxes
        MONO_COLOR_SKYBOX = SkyboxType.Builder.create(MonoColorSkybox.class, "monocolor").add(1, MonoColorSkybox.CODEC).build();
        SQUARE_TEXTURED_SKYBOX = SkyboxType.Builder.create(SquareTexturedSkybox.class, "square-textured").add(1, SquareTexturedSkybox.CODEC).build();
        MULTI_TEXTURED_SKYBOX = SkyboxType.Builder.create(MultiTexturedSkybox.class, "multi-textured").add(1, MultiTexturedSkybox.CODEC).build();
    }

    private final BiMap<Integer, Codec<T>> codecBiMap;
    private final String name;
    private SkyboxType(BiMap<Integer, Codec<T>> codecBiMap, String name) {
        this.codecBiMap = codecBiMap;
        this.name = name;
    }

    public static void register(Consumer<SkyboxType<?>> function) {
        function.accept(OVERWORLD);
        function.accept(END);
        function.accept(MONO_COLOR_SKYBOX);
        function.accept(SQUARE_TEXTURED_SKYBOX);
        function.accept(MULTI_TEXTURED_SKYBOX);
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation createId() {
        return this.createIdFactory().apply(NuitClient.MOD_ID);
    }


    public Function<String, ResourceLocation> createIdFactory() {
        return (ns) -> new ResourceLocation(ns, this.getName().replace('-', '_'));
    }

    public Codec<T> getCodec(int schemaVersion) {
        return Objects.requireNonNull(this.codecBiMap.get(schemaVersion), String.format("Unsupported schema version '%d' for skybox type %s", schemaVersion, this.name));
    }

    public static class Builder<T extends Skybox> {
        private final ImmutableBiMap.Builder<Integer, Codec<T>> builder = ImmutableBiMap.builder();
        private String name;

        private Builder() {
        }

        public static <S extends Skybox> Builder<S> create(@SuppressWarnings("unused") Class<S> clazz, String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        public static <S extends Skybox> Builder<S> create(String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        public Builder<T> add(int schemaVersion, Codec<T> codec) {
            Preconditions.checkNotNull(codec, "codec was null");
            this.builder.put(schemaVersion, codec);
            return this;
        }

        public SkyboxType<T> build() {
            return new SkyboxType<>(this.builder.build(), this.name);
        }
    }
}
