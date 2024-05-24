package io.github.amerebagatelle.mods.nuit.skybox;

import com.mojang.serialization.JsonOps;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetadataTest {
    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                        "schemaVersion": 1,
                        "type": "monocolor"
                    }
                    """,
            """
                    {
                        "schemaVersion": 1,
                        "type": "square-textured"
                    }
                    """,
            """
                    {
                        "schemaVersion": 1,
                        "type": "multi-textured"
                    }
                    """,
            """
                    {
                        "schemaVersion": 1,
                        "type": "overworld"
                    }
                    """,
            """
                    {
                        "schemaVersion": 1,
                        "type": "end"
                    }
                    """
    })
    public void testCorrectParse(String json) {
        var jsonOb = JsonTestHelper.readJson(json);
        assertDoesNotThrow(() -> Metadata.CODEC.decode(JsonOps.INSTANCE, jsonOb).getOrThrow().getFirst());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                    }
                    """,
            """
                    {
                        "schemaVersion": 1
                    }
                    """,
            """
                    {
                        "type": "overworld"
                    }
                    """
    })
    public void testIncorrectParse(String json) {
        var jsonOb = JsonTestHelper.readJson(json);
        assertThrows(IllegalStateException.class, () -> Metadata.CODEC.decode(JsonOps.INSTANCE, jsonOb).getOrThrow().getFirst());
    }
}
