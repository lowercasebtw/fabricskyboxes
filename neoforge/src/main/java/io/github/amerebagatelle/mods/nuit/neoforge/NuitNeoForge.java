package io.github.amerebagatelle.mods.nuit.neoforge;

import io.github.amerebagatelle.mods.nuit.NuitClient;
import net.neoforged.fml.common.Mod;

@Mod(NuitClient.MOD_ID)
public final class NuitNeoForge {
    public NuitNeoForge() {
        NuitClient.init();
    }
}