package io.github.amerebagatelle.mods.nuit.skyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.textured.MultiTexturedSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.vanilla.EndSkybox;
import io.github.amerebagatelle.mods.nuit.skyboxes.vanilla.OverworldSkybox;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;

public class SkyboxType<T extends Skybox> {
    public static final Codec<ResourceLocation> SKYBOX_ID_CODEC;
    public static final Registrar<SkyboxType<? extends Skybox>> REGISTRY = RegistrarManager.get(NuitClient.MOD_ID).<SkyboxType<? extends Skybox>>builder(new ResourceLocation(NuitClient.MOD_ID, "skybox_type")).syncToClients().build();

    // Vanilla skyboxes
    public static final RegistrySupplier<SkyboxType<OverworldSkybox>> OVERWORLD_SKYBOX;
    public static final RegistrySupplier<SkyboxType<EndSkybox>>END_SKYBOX;

    // FSB skyboxes
    public static final RegistrySupplier<SkyboxType<MonoColorSkybox>> MONO_COLOR_SKYBOX;
    public static final RegistrySupplier<SkyboxType<SquareTexturedSkybox>> SQUARE_TEXTURED_SKYBOX;
    public static final RegistrySupplier<SkyboxType<MultiTexturedSkybox>> MULTI_TEXTURE_SKYBOX;

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
        OVERWORLD_SKYBOX = register(SkyboxType.Builder.create(OverworldSkybox.class, "overworld").add(1, OverworldSkybox.CODEC).build());
        END_SKYBOX = register(SkyboxType.Builder.create(EndSkybox.class, "end").add(1, EndSkybox.CODEC).build());

        // FSB skyboxes
        MONO_COLOR_SKYBOX = register(SkyboxType.Builder.create(MonoColorSkybox.class, "monocolor").add(1, MonoColorSkybox.CODEC).build());
        SQUARE_TEXTURED_SKYBOX = register(SkyboxType.Builder.create(SquareTexturedSkybox.class, "square-textured").add(1, SquareTexturedSkybox.CODEC).build());
        MULTI_TEXTURE_SKYBOX = register(SkyboxType.Builder.create(MultiTexturedSkybox.class, "multi-textured").add(1, MultiTexturedSkybox.CODEC).build());
    }

    private final BiMap<Integer, Codec<T>> codecBiMap;
    private final String name;
    private SkyboxType(BiMap<Integer, Codec<T>> codecBiMap, String name) {
        this.codecBiMap = codecBiMap;
        this.name = name;
    }

    public static void initRegistry() {
        if (REGISTRY == null) {
            System.err.println("[Nuit] Registry not loaded?");
        }
    }

    private static <T extends Skybox> RegistrySupplier<SkyboxType<T>> register(SkyboxType<T> type) {
        return REGISTRY.register(type.createId(NuitClient.MOD_ID), () -> type);
    }

    public String getName() {
        return this.name;
    }

    public ResourceLocation createId(String namespace) {
        return this.createIdFactory().apply(namespace);
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

        public SkyboxType<T> buildAndRegister(String namespace) {
            return REGISTRY.register(new ResourceLocation(namespace, this.name.replace('-', '_')), this::build).get();
        }
    }
}
