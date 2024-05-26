package io.github.amerebagatelle.mods.nuit.util;

import com.google.common.collect.Range;
import io.github.amerebagatelle.mods.nuit.NuitClient;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.NuitSkybox;
import io.github.amerebagatelle.mods.nuit.api.skyboxes.Skybox;
import io.github.amerebagatelle.mods.nuit.components.MinMaxEntry;
import io.github.amerebagatelle.mods.nuit.components.RGBA;
import io.github.amerebagatelle.mods.nuit.components.UVRange;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Utils {
    public static final UVRange[] TEXTURE_FACES = new UVRange[]{
            new UVRange(0, 0, 1.0F / 3.0F, 1.0F / 2.0F), // bottom
            new UVRange(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1), // north
            new UVRange(2.0F / 3.0F, 0, 1, 1.0F / 2.0F), // south
            new UVRange(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F), // top
            new UVRange(2.0F / 3.0F, 1.0F / 2.0F, 1, 1), // east
            new UVRange(0, 1.0F / 2.0F, 1.0F / 3.0F, 1) // west
    };


    /**
     * Maps input intersection to output intersection, does so by taking in input and output UV ranges and then mapping the input intersection to the output intersection.
     *
     * @param input             The input UV range
     * @param output            The output UV range
     * @param inputIntersection The input intersection
     * @return The output intersection
     */
    public static UVRange mapUVRanges(UVRange input, UVRange output, UVRange inputIntersection) {
        float u1 = (inputIntersection.getMinU() - input.getMinU()) / (input.getMaxU() - input.getMinU()) * (output.getMaxU() - output.getMinU()) + output.getMinU();
        float u2 = (inputIntersection.getMaxU() - input.getMinU()) / (input.getMaxU() - input.getMinU()) * (output.getMaxU() - output.getMinU()) + output.getMinU();
        float v1 = (inputIntersection.getMinV() - input.getMinV()) / (input.getMaxV() - input.getMinV()) * (output.getMaxV() - output.getMinV()) + output.getMinV();
        float v2 = (inputIntersection.getMaxV() - input.getMinV()) / (input.getMaxV() - input.getMinV()) * (output.getMaxV() - output.getMinV()) + output.getMinV();
        return new UVRange(u1, v1, u2, v2);
    }

    /**
     * Finds the intersection between two UV ranges
     *
     * @param first  First UV range
     * @param second Second UV range
     * @return The intersection between the two UV ranges, if none is found, null is returned
     */
    public static UVRange findUVIntersection(UVRange first, UVRange second) {
        float intersectionMinU = Math.max(first.getMinU(), second.getMinU());
        float intersectionMaxU = Math.min(first.getMaxU(), second.getMaxU());
        float intersectionMinV = Math.max(first.getMinV(), second.getMinV());
        float intersectionMaxV = Math.min(first.getMaxV(), second.getMaxV());

        if (intersectionMaxU >= intersectionMinU && intersectionMaxV >= intersectionMinV) {
            return new UVRange(intersectionMinU, intersectionMinV, intersectionMaxU, intersectionMaxV);
        } else {
            // No intersection
            return null;
        }
    }

    /**
     * @return Whether the value is within any of the minMaxEntries.
     */
    public static boolean checkRanges(double value, List<MinMaxEntry> minMaxEntries, boolean inverse) {
        return minMaxEntries.isEmpty() || (inverse ^ minMaxEntries.stream()
                .anyMatch(minMaxEntry -> Range.closed(minMaxEntry.min(), minMaxEntry.max())
                        .contains((float) value)));
    }

    /**
     * Helper method to log warnings when normalizing/debugging
     *
     * @param initialValue Initial value
     * @param finalValue   Final value
     * @param message      Desired message
     * @param <T>          Any type
     * @return finalValue
     */
    public static <T> T warnIfDifferent(T initialValue, T finalValue, String message) {
        if (!initialValue.equals(finalValue) && NuitClient.config().generalSettings.debugMode) {
            NuitClient.getLogger().warn(message);
        }
        return finalValue;
    }

    /**
     * Normalizes any tick time outside 0-23999
     *
     * @param tickTime Time in ticks
     * @return Normalized tickTime
     */
    public static int normalizeTickTime(long tickTime) {
        long result = tickTime % 24000;
        return (int) (result >= 0 ? result : result + 24000);
    }

    /**
     * Calculates the rotation in degrees for skybox rotations
     *
     * @param rotationSpeed    Rotation speed
     * @param timeShift        Time shift (by default 0, OptiFine starts at 18000)
     * @param isSkyboxRotation Whether it is a skybox rotation or decoration rotation
     * @param world            Client world
     * @return Rotation in degrees
     */
    public static double calculateRotation(double rotationSpeed, int timeShift, boolean isSkyboxRotation, ClientLevel world) {
        if (rotationSpeed != 0F) {
            long timeOfDay = world.getDayTime() + timeShift;
            double rotationFraction = timeOfDay / (24000.0D / rotationSpeed);
            double skyAngle = Mth.positiveModulo(rotationFraction, 1);
            if (isSkyboxRotation) {
                return 360D * skyAngle;
            } else {
                return 360D * world.dimensionType().timeOfDay((long) (24000 * skyAngle));
            }
        } else {
            return 0D;
        }
    }

    /**
     * Checks whether current time is within start and end time, this method also supports roll over checks
     *
     * @param currentTime The current world time
     * @param startTime   The start time
     * @param endTime     The end time
     * @return Whether current time is within start and end time
     */
    public static boolean isInTimeInterval(int currentTime, int startTime, int endTime) {
        if (currentTime < 0 || currentTime >= 24000) {
            throw new RuntimeException("Invalid current time, value must be between 0-23999: " + currentTime);
        }

        if (startTime <= endTime) {
            return currentTime >= startTime && currentTime <= endTime;
        } else {
            return currentTime >= startTime || currentTime <= endTime;
        }
    }

    /**
     * Calculates the fade alpha
     *
     * @param maxAlpha     The maximum alpha value
     * @param minAlpha     The minimum alpha value
     * @param currentTime  The current world time
     * @param startFadeIn  The fade in start time
     * @param endFadeIn    The fade in end time
     * @param startFadeOut The fade out start time
     * @param endFadeOut   The fade out end time
     * @return Fade Alpha
     */
    public static float calculateFadeAlphaValue(float maxAlpha, float minAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
        if (isInTimeInterval(currentTime, endFadeIn, startFadeOut)) {
            return maxAlpha;
        } else if (isInTimeInterval(currentTime, startFadeIn, endFadeIn)) {
            int fadeInDuration = calculateCyclicTimeDistance(startFadeIn, endFadeIn);
            int timePassedSinceFadeInStart = calculateCyclicTimeDistance(startFadeIn, currentTime);
            return minAlpha + ((float) timePassedSinceFadeInStart / fadeInDuration) * (maxAlpha - minAlpha);
        } else if (isInTimeInterval(currentTime, startFadeOut, endFadeOut)) {
            int fadeOutDuration = calculateCyclicTimeDistance(startFadeOut, endFadeOut);
            int timePassedSinceFadeOutStart = calculateCyclicTimeDistance(startFadeOut, currentTime);
            return maxAlpha + ((float) timePassedSinceFadeOutStart / fadeOutDuration) * (minAlpha - maxAlpha);
        } else {
            return minAlpha;
        }
    }

    /**
     * Calculates the interpolated alpha value between two keyframes.
     *
     * @param currentTime          The current time.
     * @param duration             The duration of the keyframes.
     * @param currentKeyFrame      The timestamp of the current keyframe.
     * @param nextKeyFrame         The timestamp of the next keyframe.
     * @param currentKeyFrameValue The alpha value at the current keyframe.
     * @param nextKeyFrameValue    The alpha value at the next keyframe.
     * @return The interpolated alpha value based on the current time and keyframes.
     */
    public static float calculateInterpolatedAlpha(long currentTime, long duration, long currentKeyFrame, long nextKeyFrame, float currentKeyFrameValue, float nextKeyFrameValue) {
        // Check if no interpolation is needed
        if (currentKeyFrameValue == nextKeyFrameValue || currentKeyFrame == nextKeyFrame) {
            return nextKeyFrameValue;
        }

        // Handle cyclical keyframes
        if (currentKeyFrame > nextKeyFrame) {
            // Calculate time remaining in the cycle
            long timeRemainingInCycle = duration - currentKeyFrame;
            // Calculate total time in the cycle
            long timeInCycle = timeRemainingInCycle + nextKeyFrame;

            // Adjust nextKeyFrame and currentTime
            nextKeyFrame = currentKeyFrame + timeInCycle;
            long timePassed = timeRemainingInCycle + currentTime;
            currentTime = currentKeyFrame + timePassed;
        }

        // Calculate duration between keyframes and time passed since currentKeyFrame
        long durationBetween = nextKeyFrame - currentKeyFrame;
        long timePassedSinceKeyFrame = currentTime - currentKeyFrame;

        // Perform interpolation calculation
        return currentKeyFrameValue + ((float) timePassedSinceKeyFrame / durationBetween) * (nextKeyFrameValue - currentKeyFrameValue);
    }

    /**
     * Finds the closest keyframes to the given current time from a map of keyframes.
     *
     * @param keyFrames   A map of keyframes (timestamps) to alpha values.
     * @param currentTime The current time for which to find the closest keyframes.
     * @return A pair of timestamps representing the closest keyframes before and after the current time.
     */
    public static @Nullable Tuple<Long, Long> findClosestKeyframes(Map<Long, Float> keyFrames, long currentTime) {
        if (keyFrames.isEmpty())
            return null;

        long closestLowerKeyFrame = Long.MIN_VALUE;
        long closestHigherKeyFrame = Long.MAX_VALUE;

        for (long keyFrame : keyFrames.keySet()) {
            if (keyFrame <= currentTime && keyFrame > closestLowerKeyFrame) {
                closestLowerKeyFrame = keyFrame;
            }
            if (keyFrame > currentTime && keyFrame < closestHigherKeyFrame) {
                closestHigherKeyFrame = keyFrame;
            }
        }

        // Handle cases where the current time is before the first keyframe or after the last keyframe and single keyframe cases
        if (closestLowerKeyFrame == Long.MIN_VALUE || closestHigherKeyFrame == Long.MAX_VALUE) {
            closestLowerKeyFrame = keyFrames.keySet().stream().max(Long::compare).orElse(Long.MIN_VALUE);
            closestHigherKeyFrame = keyFrames.keySet().stream().min(Long::compare).orElse(Long.MAX_VALUE);
        }

        return new Tuple<>(closestLowerKeyFrame, closestHigherKeyFrame);
    }

    /**
     * Calculates the cyclic distance (duration) between two time points on a cyclic timescale.
     *
     * @param startTime The first time point.
     * @param endTime   The second time point.
     * @return The cyclic distance between the two time points.
     */
    public static int calculateCyclicTimeDistance(int startTime, int endTime) {
        return (endTime - startTime + 24000) % 24000;
    }

    /**
     * Blends all fog colors using the alpha blending formula: (source * source_alpha) + (destination * (1 - source_alpha)).
     *
     * @param skyboxList      List of skyboxes to blend the fog colors from.
     * @param initialFogColor The initial fog color to be blended with the skybox fog colors.
     * @return The final blended fog color.
     */
    public static FogRGBA alphaBlendFogColors(List<Skybox> skyboxList, RGBA initialFogColor) {
        FogRGBA destination = new FogRGBA(initialFogColor);

        for (Skybox skybox : skyboxList) {
            if (skybox.isActive() && skybox instanceof NuitSkybox nuitSkybox && nuitSkybox.getProperties().isChangeFog()) {
                FogRGBA source = new FogRGBA(
                        nuitSkybox.getProperties().getFogColors().getRed(),
                        nuitSkybox.getProperties().getFogColors().getGreen(),
                        nuitSkybox.getProperties().getFogColors().getBlue(),
                        nuitSkybox.getAlpha(),
                        nuitSkybox.getProperties().isChangeFogDensity(),
                        nuitSkybox.getProperties().getFogColors().getAlpha()
                );

                float sourceAlphaInv = 1f - source.getAlpha();
                destination = new FogRGBA(
                        (source.getRed() * source.getAlpha()) + (destination.getRed() * sourceAlphaInv),
                        (source.getGreen() * source.getAlpha()) + (destination.getGreen() * sourceAlphaInv),
                        (source.getBlue() * source.getAlpha()) + (destination.getBlue() * sourceAlphaInv),
                        (source.getAlpha() * source.getAlpha()) + (destination.getAlpha() * sourceAlphaInv),
                        source.isModifyDensity(),
                        (source.getDensity() * source.getAlpha()) + (destination.getDensity() * sourceAlphaInv)
                );
            }
        }

        return destination;
    }

    /**
     * Uses weighted additive color mixing and then applies the alpha blending formula: (source * source_alpha) + (destination * (1 - source_alpha)) with the initial fog color.
     *
     * @param skyboxList      List of skyboxes to blend the fog colors from.
     * @param initialFogColor The initial fog color to be blended with the skybox fog colors.
     * @return The weighted additive color with the final blended color using the alpha blending formula along with the initial fog color.
     */
    public static RGBA weightedAdditiveBlendFogColors(List<Skybox> skyboxList, RGBA initialFogColor) {
        float[] colorSum = new float[4];
        List<RGBA> activeColors = skyboxList.stream()
                .filter(Skybox::isActive)
                .filter(NuitSkybox.class::isInstance)
                .map(NuitSkybox.class::cast)
                .filter(nuitSkybox -> nuitSkybox.getProperties().isChangeFog())
                .map(nuitSkybox -> new RGBA(nuitSkybox.getProperties().getFogColors().getRed(),
                        nuitSkybox.getProperties().getFogColors().getGreen(),
                        nuitSkybox.getProperties().getFogColors().getBlue(),
                        nuitSkybox.getAlpha()))
                .toList();
        if (activeColors.isEmpty()) {
            return null;
        }
        for (RGBA rgba : activeColors) {
            colorSum[0] += rgba.getRed() * rgba.getAlpha();
            colorSum[1] += rgba.getGreen() * rgba.getAlpha();
            colorSum[2] += rgba.getBlue() * rgba.getAlpha();
            colorSum[3] += rgba.getAlpha(); // this should never be zero.
        }
        float finalAlpha = colorSum[3];
        final RGBA activeColorsMixed = new RGBA(colorSum[0] / finalAlpha, colorSum[1] / finalAlpha, colorSum[2] / finalAlpha);

        Optional<RGBA> activeColorsHighestAlpha = activeColors.stream().max(Comparator.comparingDouble(RGBA::getAlpha));
        float activeColorsMaxAlpha = activeColorsHighestAlpha.get().getAlpha();

        float diffMul = 1f - activeColorsMaxAlpha;
        final RGBA originalFogColorModified = new RGBA(initialFogColor.getRed() * diffMul, initialFogColor.getGreen() * diffMul, initialFogColor.getBlue() * diffMul);
        final RGBA activeColorsMixedFinal = new RGBA(activeColorsMixed.getRed() * activeColorsMaxAlpha, activeColorsMixed.getGreen() * activeColorsMaxAlpha, activeColorsMixed.getBlue() * activeColorsMaxAlpha);

        return new RGBA(originalFogColorModified.getRed() + activeColorsMixedFinal.getRed(), originalFogColorModified.getGreen() + activeColorsMixedFinal.getGreen(), originalFogColorModified.getBlue() + activeColorsMixedFinal.getBlue());
    }

    /**
     * Calculates the condition alpha
     *
     * @param maxAlpha  The maximum alpha value
     * @param minAlpha  The minimum alpha value
     * @param lastAlpha The last condition alpha value
     * @param duration  The duration
     * @param in        Whether it will transition in or out
     * @return condition alpha
     */
    public static float calculateConditionAlphaValue(float maxAlpha, float minAlpha, float lastAlpha, int duration, boolean in) {
        if (duration == 0) {
            return lastAlpha;
        } else if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == minAlpha) {
            return minAlpha;
        } else {
            float alphaChange = (maxAlpha - minAlpha) / duration;
            float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
            return Mth.clamp(result, minAlpha, maxAlpha);
        }
    }

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform.
    public static <T> T loadService(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        NuitClient.getLogger().debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
