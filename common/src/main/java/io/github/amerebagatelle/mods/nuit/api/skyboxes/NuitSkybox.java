package io.github.amerebagatelle.mods.nuit.api.skyboxes;

import io.github.amerebagatelle.mods.nuit.skybox.Conditions;
import io.github.amerebagatelle.mods.nuit.skybox.Decorations;
import io.github.amerebagatelle.mods.nuit.skybox.Properties;

public interface NuitSkybox extends Skybox {
    float getAlpha();

    float updateAlpha();

    Properties getProperties();

    Conditions getConditions();

    Decorations getDecorations();
}

