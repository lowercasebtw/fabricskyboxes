package io.github.amerebagatelle.mods.nuit.components;

import com.mojang.serialization.JsonOps;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RotationTest {
    @ParameterizedTest
    @ValueSource(strings = {
            """
                    {
                    }
                    """,
            """
                    {
                        "skyboxRotation": false
                    }
                    """,
            """
                    {
                        "static": [0, 90, 0]
                    }
                    """,
            """
                    {
                        "axis": [0, 90, 0]
                    }
                    """,
            """
                    {
                        "timeShift": [0, 90, 0]
                    }
                    """,
            """
                    {
                        "rotationSpeedX": 1
                    }
                    """,
            """
                    {
                        "rotationSpeedY": 0.5
                    }
                    """,
            """
                    {
                        "rotationSpeedZ": 0.25
                    }
                    """
    })
    public void testCorrectParse(String json) {
        var jsonOb = JsonTestHelper.readJson(json);
        assertDoesNotThrow(() -> Rotation.CODEC.decode(JsonOps.INSTANCE, jsonOb).getOrThrow().getFirst());
    }
}
