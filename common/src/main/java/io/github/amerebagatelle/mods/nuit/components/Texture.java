package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * Encapsulates the location of a texture, the
 * minimum u coordinate, maximum u coordinate,
 * minimum v coordinate and maximum v coordinate.
 */
public class Texture extends UVRange {
    public static final Codec<Texture> CODEC = ResourceLocation.CODEC.xmap(Texture::new, Texture::getTextureId);
    private final ResourceLocation textureId;

    public Texture(ResourceLocation textureId, float minU, float minV, float maxU, float maxV) {
        super(minU, minV, maxU, maxV);
        this.textureId = textureId;
    }

    public Texture(ResourceLocation textureId) {
        this(textureId, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public ResourceLocation getTextureId() {
        return this.textureId;
    }
}