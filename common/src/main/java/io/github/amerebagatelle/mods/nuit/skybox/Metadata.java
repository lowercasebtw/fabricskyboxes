package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.mods.nuit.skyboxes.SkyboxType;
import net.minecraft.resources.ResourceLocation;

public class Metadata {
    public static final Codec<Metadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("schemaVersion").forGetter(Metadata::getSchemaVersion),
            SkyboxType.SKYBOX_ID_CODEC.fieldOf("type").forGetter(Metadata::getType)
    ).apply(instance, Metadata::new));

    private final int schemaVersion;
    private final ResourceLocation type;

    public Metadata(int schemaVersion, ResourceLocation type) {
        this.schemaVersion = schemaVersion;
        this.type = type;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public ResourceLocation getType() {
        return this.type;
    }
}

