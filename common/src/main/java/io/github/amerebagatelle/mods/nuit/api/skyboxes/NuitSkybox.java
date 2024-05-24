package io.github.amerebagatelle.mods.nuit.api.skyboxes;

import io.github.amerebagatelle.mods.nuit.components.Conditions;
import io.github.amerebagatelle.mods.nuit.components.Decorations;
import io.github.amerebagatelle.mods.nuit.components.Properties;

public interface NuitSkybox extends Skybox {
    float getAlpha();

    float updateAlpha();

    Properties getProperties();

    Conditions getConditions();

    Decorations getDecorations();
}

