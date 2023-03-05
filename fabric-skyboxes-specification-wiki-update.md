# Fabric skyboxes specification (wiki update)

This specification defines a format for a set of rules for the purpose of custom sky rendering.

These rules can be categorized into 10 groups. Below you can see a table of contents of these groups.

There are going to be examples along the way, and at the very bottom of the page, there's going to be a full example to showcase the structure of a complete file.

## Table of contents:

1. Schema version
2. Type
   1. monocolor
   2. textured
3. Color \*
4. Texture \*
5. Animation textures \*
6. FPS \*\*
7. Blend
8. Properties
   1. Fade object
   2. Rotation Object
      1. Float Vector
   3. Fog color
9. Conditions
   1. MinMax Entry Object
   2. Loop Object
10. Decorations

Full example



\* "color", "texture" and "animationTextures" are mutually exclusive based on the "type" of the skybox.

\*\* "fps" is only to be used for the 2 animated skybox types.

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

Example for (animated-)square-textured skybox

<figure><img src=".gitbook/assets/separate-sprites.png" alt=""><figcaption></figcaption></figure>

Example for single-sprite-(animated-)square-textured skybox

<figure><img src=".gitbook/assets/single-sprite.png" alt=""><figcaption></figcaption></figure>

## 3. Color

This should only be used when using the monocolor type of skybox.

|   Name  |                                       Description                                       | Required | Default |
| :-----: | :-------------------------------------------------------------------------------------: | :------: | :-----: |
|  `red`  |      Specifies the amount of red color to be used. Must be a value between 0 and 1.     |     ✅    |    -    |
| `green` |     Specifies the amount of green color to be used. Must be a value between 0 and 1.    |     ✅    |    -    |
|  `blue` |     Specifies the amount of blue color to be used. Must be a value between 0 and 1.     |     ✅    |    -    |
| `alpha` | Specifies the amount of alpha transparency to be used. Must be a value between 0 and 1. |     ❌    |   1.0   |

```
"color": {"red": 0.84, "green": 0.91, "blue": 0.72, "alpha": 1.0}
```

## 4. Texture

This should only be used when using the 2 NON-animated skybox types.

|    Name    |                                                                                Description                                                                               |
| :--------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  `texture` |                                   Used for the single-sprite-square-textured skybox type. Specifies the directory for the texture file.                                  |
| `textures` | Used for the square-textured skybox type. Specifies the directory for the texture files to be used for the top, bottom, east, west, north and south sides of the skybox. |

```
"texture": "fabricskyboxes:sky/skybox.png"
```

OR

```
"textures": {
		"top": 		"fabricskyboxes:sky/skybox_top.png",
		"bottom": 	"fabricskyboxes:sky/skybox_bottom.png",
		"east": 	"fabricskyboxes:sky/skybox_east.png",
		"west": 	"fabricskyboxes:sky/skybox_west.png",
		"north": 	"fabricskyboxes:sky/skybox_north.png",
		"south": 	"fabricskyboxes:sky/skybox_south.png"
		}
```

## 5. Animation textures

This should only be used when using the 2 animated skybox types. Depending on which type of skybox you use, you will need to specify the textures differently.

Here's how to do it for animated-square-textured, using a 3 frame animation as an example.

```
"animationTextures":
	[
		{
			"top": 		"fabricskyboxes:sky/skybox_frame1_top.png",
			"bottom": 	"fabricskyboxes:sky/skybox_frame1_bottom.png",
			"east": 	"fabricskyboxes:sky/skybox_frame1_east.png",
			"west": 	"fabricskyboxes:sky/skybox_frame1_west.png",
			"north": 	"fabricskyboxes:sky/skybox_frame1_north.png",
			"south": 	"fabricskyboxes:sky/skybox_frame1_south.png"
		},
		{
			"top": 		"fabricskyboxes:sky/skybox_frame2_top.png",
			"bottom": 	"fabricskyboxes:sky/skybox_frame2_bottom.png",
			"east": 	"fabricskyboxes:sky/skybox_frame2_east.png",
			"west": 	"fabricskyboxes:sky/skybox_frame2_west.png",
			"north": 	"fabricskyboxes:sky/skybox_frame2_north.png",
			"south": 	"fabricskyboxes:sky/skybox_frame2_south.png"
		},
		{
			"top": 		"fabricskyboxes:sky/skybox_frame3_top.png",
			"bottom": 	"fabricskyboxes:sky/skybox_frame3_bottom.png",
			"east": 	"fabricskyboxes:sky/skybox_frame3_east.png",
			"west": 	"fabricskyboxes:sky/skybox_frame3_west.png",
			"north": 	"fabricskyboxes:sky/skybox_frame3_north.png",
			"south": 	"fabricskyboxes:sky/skybox_frame3_south.png"
		}
	]
```

And for the single-sprite-animated-square-textured type.

```
"animationTextures": [
		"fabricskyboxes:sky/skybox_frame1.png",
		"fabricskyboxes:sky/skybox_frame1.png",
		"fabricskyboxes:sky/skybox_frame1.png"
]
```

## 6. FPS

This should only be used when using the 2 animated skybox types.

|  Name |                        Description                        |
| :---: | :-------------------------------------------------------: |
| `fps` | Specifies the number of frames to be rendered per second. |

```
"fps": 1
```

## 7. Blend

