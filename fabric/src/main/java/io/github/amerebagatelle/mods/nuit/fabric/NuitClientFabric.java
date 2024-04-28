package io.github.amerebagatelle.mods.nuit.fabric;

import io.github.amerebagatelle.mods.nuit.NuitClient;
import net.fabricmc.api.ClientModInitializer;

public class NuitClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NuitClient.init();
    }
}