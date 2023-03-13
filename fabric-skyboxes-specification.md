---
layout: editorial
---

# Fabric skyboxes specification

This specification defines a format for a set of rules for the purpose of custom sky rendering. These rules can be categorized into 10 groups. See the side panel for the table of contents.

There are going to be examples along the way, and at the very bottom of the page, there are going to be full examples of the different types of skyboxes to showcase the structure of a complete file, as well as template files to help you to quickly start your own pack.

## 1. Schema version

The current version is 2.

```
"schemaVersion": 2
```

## 2. Type

There are 2 main types of skyboxes: monocolor and textured skyboxes.

### 2.1 Monocolor skyboxes

|     Name    |                       Description                      |
| :---------: | :----------------------------------------------------: |
| `monocolor` | Replaces the vanilla sky color with your chosen color. |

```
"type": "monocolor"
```

### 2.2 Textured skyboxes

|                   Name                   |                                       Description                                      |
| :--------------------------------------: | :------------------------------------------------------------------------------------: |
|             `square-textured`            |      uses 6 separate 1:1 aspect ratio texture files for the 6 sides of the skybox      |
|        `animated-square-textured`        | uses multiple sets of 6 separate 1:1 aspect ratio texture files for an animated skybox |
|      `single-sprite-square-textured`     |               uses a single 3:2 aspect ratio texture file for the skybox               |
| `single-sprite-animated-square-textured` |       uses multiple single 3:2 aspect ratio texture files for an animated skybox       |

```
"type": "single-sprite-square-textured"
```

Example for `(animated-)square-textured` skybox

<figure><img src=".gitbook/assets/separate-sprites.png" alt=""><figcaption></figcaption></figure>

Example for `single-sprite-(animated-)square-textured` skybox

<figure><img src=".gitbook/assets/single-sprite.png" alt=""><figcaption></figcaption></figure>

## 3. Color

This should only be used- and must be used when using the `monocolor` type of skybox.

|   Name  |                                       Description                                       | Required | Default |
| :-----: | :-------------------------------------------------------------------------------------: | :------: | :-----: |
|  `red`  |      Specifies the amount of red color to be used. Must be a value between 0 and 1.     |     ✅    |    -    |
| `green` |     Specifies the amount of green color to be used. Must be a value between 0 and 1.    |     ✅    |    -    |
|  `blue` |     Specifies the amount of blue color to be used. Must be a value between 0 and 1.     |     ✅    |    -    |
| `alpha` | Specifies the amount of alpha transparency to be used. Must be a value between 0 and 1. |     ❌    |  `1.0`  |

```
"color": {"red": 0.84, "green": 0.91, "blue": 0.72, "alpha": 1.0}
```

## 4. Texture

This should only be used- and must be used when using the 2 NON-animated skybox types.

|    Name    |                                                                                       Description                                                                                      |
| :--------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  `texture` |                                         Used for the `single-sprite-square-textured` skybox type. Specifies the directory for the texture file.                                        |
| `textures` | Used for the `square-textured` skybox type. Specifies the directory for the texture files to be used for the `top`, `bottom`, `east`, `west`, `north` and `south` sides of the skybox. |

```
"texture": "fabricskyboxes:sky/skybox.png"
```

OR

```
"textures": {
	"top": "fabricskyboxes:sky/skybox_top.png",
	"bottom": "fabricskyboxes:sky/skybox_bottom.png",
	"east": "fabricskyboxes:sky/skybox_east.png",
	"west": "fabricskyboxes:sky/skybox_west.png",
	"north": "fabricskyboxes:sky/skybox_north.png",
	"south": "fabricskyboxes:sky/skybox_south.png"
	}
```

## 5. Animation textures

This should only be used- and must be used when using the 2 animated skybox types. Depending on which type of skybox you use, you will need to specify the textures differently.

Here's how to do it for `animated-square-textured`, using a 3 frame animation as an example.

```
"animationTextures":
	[
		{
			"top": "fabricskyboxes:sky/skybox_frame1_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame1_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame1_east.png",
			"west": "fabricskyboxes:sky/skybox_frame1_west.png",
			"north": "fabricskyboxes:sky/skybox_frame1_north.png",
			"south": "fabricskyboxes:sky/skybox_frame1_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame2_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame2_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame2_east.png",
			"west": "fabricskyboxes:sky/skybox_frame2_west.png",
			"north": "fabricskyboxes:sky/skybox_frame2_north.png",
			"south": "fabricskyboxes:sky/skybox_frame2_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame3_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame3_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame3_east.png",
			"west": "fabricskyboxes:sky/skybox_frame3_west.png",
			"north": "fabricskyboxes:sky/skybox_frame3_north.png",
			"south": "fabricskyboxes:sky/skybox_frame3_south.png"
		}
	]
```

And for the `single-sprite-animated-square-textured` type.

```
"animationTextures": [
	"fabricskyboxes:sky/skybox_frame1.png",
	"fabricskyboxes:sky/skybox_frame2.png",
	"fabricskyboxes:sky/skybox_frame3.png"
]
```

## 6. FPS

This should only be used- and must be used when using the 2 animated skybox types.

|  Name |                        Description                        |
| :---: | :-------------------------------------------------------: |
| `fps` | Specifies the number of frames to be rendered per second. |

```
"fps": 1
```

## 7. Blend

Specifies how the skybox should blend on top of the previously rendered sky layer. The first layer is the vanilla skybox. All fields are optional. The default blend method is `add`.

There are 2 types of blending methods to use. The traditional blending modes like the ones found in image editing software, and a custom OpenGL method.

|  Name  |                                                                       Description                                                                       |
| :----: | :-----------------------------------------------------------------------------------------------------------------------------------------------------: |
| `type` | Specifies the type of the blend. Valid types are: `add`, `subtract`, `multiply`, `screen`, `replace`, `alpha`, `dodge`, `burn`, `darken` and `lighten`. |

```
"blend": {"type" : "alpha"}
```

OR

|         Name        |                                           Description                                           | Default |
| :-----------------: | :---------------------------------------------------------------------------------------------: | :-----: |
|      `sFactor`      |                            Specifies the OpenGL source factor to use.                           |         |
|      `dFactor`      |                         Specifies the OpenGL destination factor to use.                         |         |
|      `equation`     |                           Specifies the OpenGL blend equation to use.                           |         |
|  `redAlphaEnabled`  |  Specifies whether alpha state will be used in red shader color or predetermined value of 1.0.  | `false` |
| `greenAlphaEnabled` | Specifies whether alpha state will be used in green shader color or predetermined value of 1.0. | `false` |
|  `blueAlphaEnabled` |  Specifies whether alpha state will be used in blue shader color or predetermined value of 1.0. | `false` |
|    `alphaEnabled`   |    Specifies whether alpha state will be used in shader color or predetermined value of 1.0.    | `false` |

```
"blend": {
	"sFactor": 0,
	"dFactor": 769,
	"equation": 32774,
	"redAlphaEnabled": true,
	"greenAlphaEnabled": true,
	"blueAlphaEnabled": true,
	"alphaEnabled": false
}
```

More information on custom blend can be found in the [blend documentation](https://github.com/AMereBagatelle/fabricskyboxes/blob/1.19.x-dev/docs/blend.md).

## 8. Properties

Specifies common properties used by all types of skyboxes.

|           Name          |                                                                                                            Description                                                                                                           | Required |                         Default                        |
| :---------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :------: | :----------------------------------------------------: |
|        `priority`       | Specifies the order which skybox will be rendered. If there are multiple skyboxes with identical priority, those skyboxes are not re-ordered therefore being dependant of Vanilla's alphabetical namespaced identifiers loading. |     ❌    |                           `0`                          |
|          `fade`         |                                                                    Specifies the time of day in ticks that the skybox should start and end fading in and out.                                                                    |     ✅    |                            -                           |
|      `shouldRotate`     |                                                                                      Specifies whether the skybox should rotate on its axis.                                                                                     |     ❌    |                         `false`                        |
|        `rotation`       |                                                                                      Specifies the rotation speed and angles of the skybox.                                                                                      |     ❌    | `[0,0,0]` for `static`/`axis`, `1` for `rotationSpeed` |
|  `transitionInDuration` |                                   Specifies the duration in ticks that skybox will fade in when valid conditions are changed. The value must be within 1 and 8760000 (365 days \* 24000 ticks).                                  |     ❌    |                          `20`                          |
| `transitionOutDuration` |                                  Specifies the duration in ticks that skybox will fade out when valid conditions are changed. The value must be within 1 and 8760000 (365 days \* 24000 ticks).                                  |     ❌    |                          `20`                          |
|       `changeFog`       |                                                                                     Specifies whether the skybox should change the fog color.                                                                                    |     ❌    |                         `false`                        |
|       `fogColors`       |                                                                    Specifies the colors to be used for rendering fog. Only has an effect if changeFog is true.                                                                   |     ❌    |                   `0` for each value                   |
|       `sunSkyTint`      |                                                                            Specifies whether the skybox should disable sunrise/set sky color tinting.                                                                            |     ❌    |                         `true`                         |
|       `inThickFog`      |                                                                                   Specifies whether the skybox should be rendered in thick fog.                                                                                  |     ❌    |                         `true`                         |
|        `maxAlpha`       |                                                                         Specifies the alpha transparency of the skybox. The value must be within 0 and 1.                                                                        |     ❌    |                          `1.0`                         |

```
"properties": {
	"priority": 1,
	"fade": {
		"startFadeIn": 1000,
		"endFadeIn": 2000,
		"startFadeOut": 3000,
		"endFadeOut": 4000,
		"alwaysOn": false
		},
	"shouldRotate": true,
	"rotation": {
		"rotationSpeed": 0.866,
		"static": [0.0, 0.0, 0.0],
		"axis": [0.0, -180.0, 0.0]
	},
	"transitionInDuration": 200,
	"transitionOutDuration": 300,
	"changeFog": true,
	"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
	"sunSkyTint": true,
	"inThickFog": true,
	"maxAlpha": 0.9
}
```

As you can see, `fade`, `rotation` (and its `static` and `axis` components) have multiple object within, so let's take a look at those in more detail. For `fogColors`, refer to [Color](fabric-skyboxes-specification.md#3.-color) for the specification.

### 8.1 Fade object

Stores a list of four integers which specify the time in ticks to start and end fading the skybox in and out.

|      Name      |                        Description                       |   Required   | Default |
| :------------: | :------------------------------------------------------: | :----------: | :-----: |
|  `startFadeIn` |  The times in ticks when a skybox will start to fade in. | <p>✅<br></p> |    -    |
|   `endFadeIn`  |   The times in ticks when a skybox will end fading in.   |       ✅      |    -    |
| `startFadeOut` | The times in ticks when a skybox will start to fade out. |       ✅      |    -    |
|  `endFadeOut`  |   The times in ticks when a skybox will end fading out.  |       ✅      |    -    |
|   `alwaysOn`   |  Whether the skybox should always be at full visibility. |       ❌      | `false` |

Conversion table

| Time in Ticks | Clock Time |
| :-----------: | :--------: |
|       0       |    6 AM    |
|      6000     |    12 AM   |
|     12000     |    6 PM    |
|     24000     |    12 PM   |

### 8.2 Rotation Object

Specifies the speed, static- and axis of rotation for a skybox. This object is used by both [Properties](fabric-skyboxes-specification.md#8.-properties) and [Decorations](fabric-skyboxes-specification.md#10.-decorations). Properties only affects the skybox rotation, and Decorations only affect the sun, moon and stars rotation. All fields are optional.

|       Name      |                                Description                               |  Default  |
| :-------------: | :----------------------------------------------------------------------: | :-------: |
|     `static`    |                 Specifies the static rotation in degrees                 | `[0,0,0]` |
|      `axis`     |                  Specifies the axis rotation in degrees                  | `[0,0,0]` |
| `rotationSpeed` | Specifies the speed of the skybox rotation, in rotations per 24000 ticks |    `1`    |

Static rotation should be thought of as the initial, "default" rotation of the skybox, before any active rotation is applied, and axis is the actual, well axis- around which the skybox will visibly revolve around. The speed defines how many times will the sky rotate per full, in-game day.

### 8.3 Float vector

Specifies a list of three floating-point literals to represent degrees of rotation.

To get a better understanding on how to define these degrees for static and axis, take a look at the image below.

<figure><img src=".gitbook/assets/axis.png" alt=""><figcaption></figcaption></figure>

There's also a tool made with Blender, that will let you see and make adjustments to both static and axis in real time and see how it affects the rotation of the skybox. This tool supports both Fabric skyboxes and Optifine. There are written instructions on how to use the tool in the blend file itself. To use this tool, you will only need a very basic understanding on how to do very basic things in Blender, such as move the camera around, or navigate the interface. These skills can be learned on YouTube in less than 10 minutes.

{% file src=".gitbook/assets/skybox rotation.blend" %}

Blender is a free, open source software. Download at [https://www.blender.org/](https://www.blender.org/)

## 9. Conditions

Specifies when and where a skybox should render. All fields are optional.

|     Name     |                                                               Description                                                              |
| :----------: | :------------------------------------------------------------------------------------------------------------------------------------: |
|   `worlds`   |                              Specifies a list of worlds sky effects that the skybox should be rendered in.                             |
| `dimensions` |                                  Specifies a list of dimension that the skybox should be rendered in.                                  |
|   `weather`  | Specifies a list of weather conditions that the skybox should be rendered in. Valid entries are `clear`, `rain`, `thunder` and `snow`. |
|   `biomes`   |                                    Specifies a list of biomes that the skybox should be rendered in.                                   |
|   `xRanges`  |                               Specifies a list of coordinates that the skybox should be rendered between.                              |
|   `yRanges`  |                               Specifies a list of coordinates that the skybox should be rendered between.                              |
|   `zRanges`  |                               Specifies a list of coordinates that the skybox should be rendered between.                              |
|    `loop`    |                                  Specifies the loop object that the skybox should be rendered between.                                 |
|   `effects`  |                          Specifies a list of player status effects during which the skybox should be rendered.                         |

```
"conditions": {
	"worlds": ["minecraft:overworld"],
	"dimensions": ["my_datapack:custom_world"],
	"weather": ["rain", "thunder"],
	"biomes": ["plains", "forest", "river"],
	"xRanges": [{"min": -100.0, "max": 100.0}],
	"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
	"zRanges": [{"min": -150.0, "max": 150.0}],
	"loop": {
		"days": 8,
		"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
	"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
}
```

Similarly to Properties, some conditions have multiple objects within. Let's take a look at them.

### 9.1 MinMax Entry Object

These objects are used by the `x-y-z range` objects, and the `loop` object. Multiple `MinMax` entries can be specified within one range.

|  Name |               Description              |
| :---: | :------------------------------------: |
| `min` | Specifies the minimum value, inclusive |
| `max` | Specifies the maximum value, exclusive |

These `MinMax` ranges should be thought of as sections on a number line, rather than incremental steps, like blocks in Minecraft. This means, that you can specify them to a decimal point precision.

To show a concrete example, let's take a look at the day ranges of a loop object.

```
"loop": {
	"days": 8,
	"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
}
```

<figure><img src=".gitbook/assets/ranges.png" alt=""><figcaption></figcaption></figure>

For the purposes of x-y-z ranges, this means that if you want 2 skyboxes to transition when crossing a certain coordinate threshold, you will need to look out for a "gap", as seen in the image above. To avoid this gap, where no sky is shown, you will need to specify the ranges something like this:

**sky1.json**

```
"yRanges": [{"min": -128.0, "max": 150.0}
```

**sky2.json**

```
"yRanges": [{"min": 150.0001, "max": 200.0}
```

The reason why we don't write `"min": 150.0` in **sky2.json**, is because then both sky 1 and 2 would overlap and show when standing on Y=150. This peculiarity is really only noticeable on the Y coordinate, as it is easy to align the player on exact whole coordinates, but it can also happen on the X and Z coordinates as well, it's just less likely. Even with the method show above, there is still a gap in-between the 2 skyboxes, but it's very unlikely you will manage to align the player that precisely.

### 9.2 Loop object

|   Name   |                    Description                   |
| :------: | :----------------------------------------------: |
|  `days`  |     Specifies the length of the loop in days.    |
| `ranges` | Specifies the days where the skybox is rendered. |

The loop object's start and end points are determined by the fade times in the given skybox. The loop starts at `startFadeIn`, and ends at `endFadeOut` after the specified number of days. To give a concrete example, let's use these parameters:

```
"properties": {
	"fade": {
		"startFadeIn": 1000,
		"endFadeIn": 2000,
		"startFadeOut": 3000,
		"endFadeOut": 4000
		}
	},
"conditions": {
	"loop": {
		"days": 8,
		"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
	}
}
```

In this scenario, the loop starts at `/time set 1000`. Then if we `/time add 195000` (8×24000+3000), that is when the loop will end, and it will start again the next day at time 1000.

See also [MinMax Entry Object](fabric-skyboxes-specification.md#9.1-minmax-entry-object) for examples on the implementation.

## 10. Decorations

Stores all specifications for the stars, sun and moon configuration. For optimum results, the moon texture should mimic the vanilla moon texture. The Default value stores the overworld sun and moon textures and sets all enabled to true. All fields are optional.

|     Name    |                                Description                               |                                 Default                                 |
| :---------: | :----------------------------------------------------------------------: | :---------------------------------------------------------------------: |
|    `sun`    |  Specifies the location of the texture to be used for rendering the sun. |      Default sun texture (`minecraft:textures/environment/sun.png`)     |
|    `moon`   | Specifies the location of the texture to be used for rendering the moon. | Default moon texture (`minecraft:textures/environment/moon_phases.png`) |
|  `showSun`  |               Specifies whether the sun should be rendered.              |                                  `true`                                 |
|  `showMoon` |              Specifies whether the moon should be rendered.              |                                  `true`                                 |
| `showStars` |                Specifies whether stars should be rendered.               |                                  `true`                                 |
|  `rotation` |                Specifies the rotation of the decorations.                |          `[0,0,0]` for `static`/`axis`, `1` for `rotationSpeed`         |

```
"decorations": {
	"sun": "minecraft:textures/environment/sun.png",
	"moon": "minecraft:textures/environment/moon_phases.png",
	"showSun": true,
	"showMoon": true,
	"showStars": true,
	"rotation": {
		"rotationSpeed": 0.5,
		"static": [0.0, 0.0, 0.0],
		"axis": [0.0, 0.0, 90.0]
	}
}
```

Rotation in Decorations only affects the sun, moon and stars, and not the skybox. To see how to implement the rotation, check [Rotation Object](fabric-skyboxes-specification.md#8.2-rotation-object) and [Float Vector](fabric-skyboxes-specification.md#8.2.1-float-vector).

It is worth knowing, that it is possible to specify unique rotation for the sun, moon and stars all individually, if they are set to show `true` in 3 separate json files, and the other 2 decorations are set to show `false`.

## Examples and templates

<details>

<summary>square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "square-textured",
	"textures": {
		"top": "fabricskyboxes:sky/skybox_top.png",
		"bottom": "fabricskyboxes:sky/skybox_bottom.png",
		"east": "fabricskyboxes:sky/skybox_east.png",
		"west": "fabricskyboxes:sky/skybox_west.png",
		"north": "fabricskyboxes:sky/skybox_north.png",
		"south": "fabricskyboxes:sky/skybox_south.png"
	},
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"shouldRotate": true,
		"rotation": {
			"rotationSpeed": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeed": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

<details>

<summary>animated-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "animated-square-textured",
	"animationTextures": [
		{
			"top": "fabricskyboxes:sky/skybox_frame1_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame1_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame1_east.png",
			"west": "fabricskyboxes:sky/skybox_frame1_west.png",
			"north": "fabricskyboxes:sky/skybox_frame1_north.png",
			"south": "fabricskyboxes:sky/skybox_frame1_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame2_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame2_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame2_east.png",
			"west": "fabricskyboxes:sky/skybox_frame2_west.png",
			"north": "fabricskyboxes:sky/skybox_frame2_north.png",
			"south": "fabricskyboxes:sky/skybox_frame2_south.png"
		},
		{
			"top": "fabricskyboxes:sky/skybox_frame3_top.png",
			"bottom": "fabricskyboxes:sky/skybox_frame3_bottom.png",
			"east": "fabricskyboxes:sky/skybox_frame3_east.png",
			"west": "fabricskyboxes:sky/skybox_frame3_west.png",
			"north": "fabricskyboxes:sky/skybox_frame3_north.png",
			"south": "fabricskyboxes:sky/skybox_frame3_south.png"
		}
	],
	"fps": 4,
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"shouldRotate": true,
		"rotation": {
			"rotationSpeed": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeed": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

<details>

<summary>single-sprite-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "single-sprite-square-textured",
	"texture": "fabricskyboxes:sky/skybox.png",
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"shouldRotate": true,
		"rotation": {
			"rotationSpeed": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeed": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

<details>

<summary>single-sprite-animated-square-textured</summary>

```
{
	"schemaVersion": 2,
	"type": "single-sprite-animated-square-textured",
	"animationTextures": [
		"fabricskyboxes:sky/skybox_frame1.png",
		"fabricskyboxes:sky/skybox_frame2.png",
		"fabricskyboxes:sky/skybox_frame3.png"
	],
	"fps": 4,
	"blend": {"type" : "alpha"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"shouldRotate": true,
		"rotation": {
			"rotationSpeed": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeed": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

<details>

<summary>monocolor</summary>

```
{
	"schemaVersion": 2,
	"type": "monocolor",
	"color": {"red": 0.84, "green": 0.91, "blue": 0.72, "alpha": 1.0},
	"blend": {"type" : "add"},
	"properties": {
		"priority": 1,
		"fade": {
			"startFadeIn": 1000,
			"endFadeIn": 2000,
			"startFadeOut": 3000,
			"endFadeOut": 4000,
			"alwaysOn": false
			},
		"shouldRotate": true,
		"rotation": {
			"rotationSpeed": 0.866,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, -180.0, 0.0]
		},
		"transitionInDuration": 200,
		"transitionOutDuration": 300,
		"changeFog": true,
		"fogColors": {"red": 0.846678, "green": 0.902068, "blue": 0.974044, "alpha": 1.0},
		"sunSkyTint": true,
		"inThickFog": true,
		"maxAlpha": 0.9
	},
	"conditions": {
		"worlds": ["minecraft:overworld"],
		"dimensions": ["my_datapack:custom_world"],
		"weather": ["rain", "thunder"],
		"biomes": ["plains", "forest", "river"],
		"xRanges": [{"min": -100.0, "max": 100.0}],
		"yRanges": [{"min": -128.0, "max": 150.0}, {"min": 200.0, "max": 320.0}],
		"zRanges": [{"min": -150.0, "max": 150.0}],
		"loop": {
			"days": 8,
			"ranges": [{"min": 0, "max": 4}, {"min": 5, "max": 8}]
		},
		"effects": ["minecraft:jump_boost", "minecraft:speed", "minecraft:slowness"]
	},
	"decorations": {
		"sun": "minecraft:textures/environment/sun.png",
		"moon": "minecraft:textures/environment/moon_phases.png",
		"showSun": true,
		"showMoon": true,
		"showStars": true,
		"rotation": {
			"rotationSpeed": 0.5,
			"static": [0.0, 0.0, 0.0],
			"axis": [0.0, 0.0, 90.0]
		}
	}
}
```

</details>

{% file src=".gitbook/assets/template_square.zip" %}

{% file src=".gitbook/assets/template_square_anim.zip" %}

{% file src=".gitbook/assets/template_single_sprite.zip" %}

{% file src=".gitbook/assets/template_single_sprite_anim.zip" %}

{% file src=".gitbook/assets/template_monocolor.zip" %}
