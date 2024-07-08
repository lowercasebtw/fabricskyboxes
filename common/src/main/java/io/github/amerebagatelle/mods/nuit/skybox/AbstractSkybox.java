package io.github.amerebagatelle.mods.nuit.skybox;

import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.components.Conditions;
import io.github.amerebagatelle.mods.nuit.components.Properties;
import io.github.amerebagatelle.mods.nuit.components.Weather;
import io.github.amerebagatelle.mods.nuit.util.Utils;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

import java.util.Map;
import java.util.Objects;

/**
 * All classes that implement {@link AbstractSkybox} should
 * have a default constructor as it is required when checking
 * the type of the skybox.
 */
public abstract class AbstractSkybox implements NuitSkybox {

    /**
     * The current alpha for the skybox. Expects all skyboxes extending this to accommodate this.
     * This variable is responsible for fading in/out skyboxes.
     */
    public transient float alpha;
    protected Properties properties = Properties.of();
    protected Conditions conditions = Conditions.of();

    protected boolean unexpectedConditionTransition = false;
    protected long lastTime = -2;
    protected float conditionAlpha = 0f;

    /**
     * Why don't we use the fade's keyframes map directly?
     * Because findClosestKeyframes has a O(n) time complexity operation. Adding to that will just make it worse.
     */
    private final Map<Long, Float> cachedKeyFrames = new Long2FloatOpenHashMap();


    protected AbstractSkybox() {
    }

    protected AbstractSkybox(Properties properties, Conditions conditions) {
        this.properties = properties;
        this.conditions = conditions;
    }

    @Override
    public void tick(ClientLevel clientWorld) {
        this.updateAlpha();
    }

    /**
     * Calculates the alpha value for the current time and conditions and returns it.
     *
     * @return The new alpha value.
     */
    @Override
    public float updateAlpha() {
        long currentTime = Objects.requireNonNull(Minecraft.getInstance().level).getDayTime() % this.properties.getFade().getDuration();

        boolean condition = this.checkConditions();

        float fadeAlpha = 1f;
        if (this.properties.getFade().isAlwaysOn()) {
            this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
        } else {
            if (this.properties.getFade().getDuration() <= NuitClient.config().generalSettings.fadeCacheDuration) {
                fadeAlpha = this.cachedKeyFrames.computeIfAbsent(currentTime, time -> {
                    Tuple<Long, Long> keyFrames = Utils.findClosestKeyframes(this.properties.getFade().getKeyFrames(), time).orElseThrow();
                    return Utils.calculateInterpolatedAlpha(
                            time,
                            this.properties.getFade().getDuration(),
                            keyFrames.getA(),
                            keyFrames.getB(),
                            this.properties.getFade().getKeyFrames().get(keyFrames.getA()),
                            this.properties.getFade().getKeyFrames().get(keyFrames.getB())
                    );
                });
            } else {
                Tuple<Long, Long> keyFrames = Utils.findClosestKeyframes(this.properties.getFade().getKeyFrames(), currentTime).orElseThrow();
                fadeAlpha = Utils.calculateInterpolatedAlpha(
                        currentTime,
                        this.properties.getFade().getDuration(),
                        keyFrames.getA(),
                        keyFrames.getB(),
                        this.properties.getFade().getKeyFrames().get(keyFrames.getA()),
                        this.properties.getFade().getKeyFrames().get(keyFrames.getB())
                );
            }

            if ((this.lastTime == currentTime - 1 || this.lastTime == currentTime) && !this.unexpectedConditionTransition) { // Check if time is ticking or if time is same (doDaylightCycle gamerule)
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, condition ? this.properties.getTransitionInDuration() : this.properties.getTransitionOutDuration(), condition);
            } else {
                this.unexpectedConditionTransition = true;
                this.conditionAlpha = Utils.calculateConditionAlphaValue(1f, 0f, this.conditionAlpha, NuitClient.config().generalSettings.unexpectedTransitionDuration, condition);
                if (this.unexpectedConditionTransition && (this.conditionAlpha == 0f || this.conditionAlpha == 1f)) {
                    this.unexpectedConditionTransition = false;
                }
            }
        }

        this.alpha = fadeAlpha * this.conditionAlpha;
        this.lastTime = currentTime;

        return this.alpha;
    }

    /**
     * @return Whether all conditions were met
     */
    protected boolean checkConditions() {
        return this.checkDimensions() && this.checkWorlds() && this.checkBiomes() &&
                this.checkXRanges() && this.checkYRanges() && this.checkZRanges() &&
                this.checkWeather() && this.checkEffects();
    }

    /**
     * @return Whether the current biomes and dimensions are valid for this skybox.
     */
    protected boolean checkBiomes() {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = Objects.requireNonNull(client.level);
        LocalPlayer player = Objects.requireNonNull(client.player);

        if (this.conditions.getBiomes().getEntries().isEmpty()) {
            return true;
        }

        boolean isExcluded = this.conditions.getBiomes().isExcludes();
        ResourceLocation biomeKey = level.getBiome(player.blockPosition()).unwrapKey().orElseThrow().location();
        boolean isDefault = this.conditions.getBiomes().getEntries().contains(DefaultHandler.DEFAULT);

        return isExcluded ^ (this.conditions.getBiomes().getEntries().contains(biomeKey) || (isDefault && DefaultHandler.checkFallbackBiomes()));
    }

    /**
     * @return Whether the current dimension identifier is valid for this skybox
     */
    protected boolean checkDimensions() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return this.conditions.getDimensions().getEntries().isEmpty() || this.conditions.getBiomes().isExcludes() ^ (
                this.conditions.getDimensions().getEntries().contains(client.level.dimension().location()) ||
                        this.conditions.getDimensions().getEntries().contains(DefaultHandler.DEFAULT) && DefaultHandler.checkFallbackDimensions());
    }

    /**
     * @return Whether the current dimension sky effect is valid for this skybox
     */
    protected boolean checkWorlds() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);
        return this.conditions.getWorlds().getEntries().isEmpty() || this.conditions.getBiomes().isExcludes() ^ (
                this.conditions.getWorlds().getEntries().contains(client.level.dimensionType().effectsLocation()) ||
                        this.conditions.getWorlds().getEntries().contains(DefaultHandler.DEFAULT) && DefaultHandler.checkFallbackWorlds());
    }

    /*
		Check if an effect that should prevent skybox from showing
     */
    protected boolean checkEffects() {
        Minecraft client = Minecraft.getInstance();
        Objects.requireNonNull(client.level);

        Camera camera = client.gameRenderer.getMainCamera();

        if (this.conditions.getEffects().getEntries().isEmpty()) {
            // Vanilla checks
            boolean thickFog = client.level.effects().isFoggyAt(Mth.floor(camera.getPosition().x()), Mth.floor(camera.getPosition().y())) || client.gui.getBossOverlay().shouldCreateWorldFog();
            if (thickFog) {
                // Render skybox in thick fog, enabled by default
                return this.properties.getFog().isShowInDenseFog();
            }

            FogType cameraSubmersionType = camera.getFluidInCamera();
            if (cameraSubmersionType == FogType.POWDER_SNOW || cameraSubmersionType == FogType.LAVA)
                return false;

            return !(camera.getEntity() instanceof LivingEntity livingEntity) || (!livingEntity.hasEffect(MobEffects.BLINDNESS) && !livingEntity.hasEffect(MobEffects.DARKNESS));

        } else {
            if (camera.getEntity() instanceof LivingEntity livingEntity) {
                return (this.conditions.getEffects().isExcludes() ^ this.conditions.getEffects().getEntries().stream().noneMatch(identifier -> client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).get(identifier) != null && livingEntity.hasEffect(client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).wrapAsHolder(client.level.registryAccess().registryOrThrow(Registries.MOB_EFFECT).get(identifier)))));
            }
        }
        return true;
    }

    /**
     * @return Whether the current x values are valid for this skybox.
     */
    protected boolean checkXRanges() {
        double playerX = Objects.requireNonNull(Minecraft.getInstance().player).getX();
        return Utils.checkRanges(playerX, this.conditions.getXRanges().getEntries(), this.conditions.getXRanges().isExcludes());
    }

    /**
     * @return Whether the current y values are valid for this skybox.
     */
    protected boolean checkYRanges() {
        double playerY = Objects.requireNonNull(Minecraft.getInstance().player).getY();
        return Utils.checkRanges(playerY, this.conditions.getYRanges().getEntries(), this.conditions.getYRanges().isExcludes());
    }

    /**
     * @return Whether the current z values are valid for this skybox.
     */
    protected boolean checkZRanges() {
        double playerZ = Objects.requireNonNull(Minecraft.getInstance().player).getZ();
        return Utils.checkRanges(playerZ, this.conditions.getZRanges().getEntries(), this.conditions.getZRanges().isExcludes());
    }

    /**
     * @return Whether the current weather is valid for this skybox.
     */
    protected boolean checkWeather() {
        if (this.conditions.getWeathers().getEntries().isEmpty()) {
            return true;
        }

        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        Biome.Precipitation precipitation = world.getBiome(player.blockPosition()).value().getPrecipitationAt(player.blockPosition());

        boolean isExcluded = this.conditions.getWeathers().isExcludes();
        boolean isThundering = world.isThundering();
        boolean isRaining = world.isRaining();
        boolean isSnowing = isRaining && precipitation == Biome.Precipitation.SNOW;
        boolean isBiomeRaining = isRaining && precipitation == Biome.Precipitation.RAIN;
        boolean isClear = !isRaining && !isThundering;

        return (isExcluded ^ this.conditions.getWeathers().getEntries().contains(Weather.THUNDER) && isThundering) ||
                (isExcluded ^ this.conditions.getWeathers().getEntries().contains(Weather.RAIN) && isRaining && !isThundering) ||
                (isExcluded ^ this.conditions.getWeathers().getEntries().contains(Weather.SNOW) && isSnowing) ||
                (isExcluded ^ this.conditions.getWeathers().getEntries().contains(Weather.BIOME_RAIN) && isBiomeRaining) ||
                (isExcluded ^ this.conditions.getWeathers().getEntries().contains(Weather.CLEAR) && isClear);
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public Conditions getConditions() {
        return this.conditions;
    }

    @Override
    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public int getLayer() {
        return this.properties.getLayer();
    }

    @Override
    public boolean isActive() {
        return this.getAlpha() != 0F;
    }

    @Override
    public String toString() {
        return String.format("[layer=%s, alpha=%s, dimension=%s, world=%s, biomes=%s, xranges=%s, yranges=%s, zranges=%s, weather=%s, effects=%s]", getProperties().getLayer(), getAlpha(), checkDimensions(), checkWorlds(), checkBiomes(), checkXRanges(), checkYRanges(), checkZRanges(), checkWeather(), checkEffects());
    }
}
