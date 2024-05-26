# Nuit Testing Documentation

All of these tests will be run before a release, but the

## Unit Tests

The unit tests are run on every PR. They are not exhaustive of every possibility.
They also cannot test the most important parts of the mod, as this is a largely visual mod.

## Visual Tests

In the root directory, there is a folder called `testpacks/`. This folder contains several resource packs useful for
testing the mod. Below is documentation for the purpose of each of the resource packs.

### [MonoColorSkybox](../testpacks/MonoColorSkybox)

Tests the basic functionality of the Mono Colored Skybox. The pack should cause the sky to go from green to blue to red
over the course of the day.

### [SquareTexturedSkybox](../testpacks/SquareTexturedSkybox)

Tests the basic functionality of the Square Textured Skybox. The pack should cause the sky to turn into the skybox
between the times 0 and 2000 (by Minecraft tick time). All the faces should be rotated correctly.