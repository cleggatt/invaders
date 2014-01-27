Invaders [![Build Status](https://secure.travis-ci.org/cleggatt/invaders.png)](http://travis-ci.org/cleggatt/invaders) [![Coverage Status](https://coveralls.io/repos/cleggatt/invaders/badge.png?branch=master)](https://coveralls.io/r/cleggatt/invaders?branch=master)
========

Generate random space invaders. The program works by generating a random block of pixels (4x6 by default), and then
mirroring it on the Y axis.

Invaders can be produced as text (on stdout), or as a PNG image (to a file).

Usage
-----

`java -jar invaders-1.0-standalone.jar [-b <arg>] [-guassian <arg>] [-help] [-o <arg>] [-p] [-pxHeight <arg>] [-pxWidth <arg>] [-s <arg>] [-seed <arg>] [-t] [-tileX <arg>] [-tileY <arg>] [-x <arg>] [-y <arg>]`

### Basic options

* `-help`: Display help text.
* `-p,--png`: Generate output as a PNG. Cannot be specified with `text`.
* `-t,--text`: Generate output as text. When this option is specified, a pixel (when specifying other options) will mean a single character. Cannot be specified with `png`.
* `-x <arg>`: The number of un-mirrored, un-scaled pixels on the X axis of a tile (default: 4)
* `-y <arg>`: The number of un-scaled pixels on the Y axis of a tile (default: 6)
* `s,--scale: <arg>`: The scaling factor for a tile (default: 1). Tiles will be scaled by this number after all pixels have been generated.
* `seed <arg>`: The random seed for tile generation. Specifying the same random seed will result in the same invaders being generated.

### Tiling options

* `-b,--border <arg>`: The border width for a tile (default: 0). This number of pixels will be left clear around each tile.
* `-tileX <arg>`: The number of tiles to create along the X axis (default: 1). Cannot be specified with `pxWidth`.
* `-tileY <arg>`: The  number of tiles to create along the Y axis  (default: 1). Cannot be specified with `pxHeight`.
* `-pxHeight <arg>`: The height (in pixels) of the final output. The number of tiles will be calculated to give this height. Cannot be specified with `tileY`.
* `-pxWidth <arg>`      the width (in pixels) of the final output. The number of tiles will be calculated to give this width. Cannot be specified with `tileX`.

### Image options

* `-guassian <arg>`: The guassian blur radius to be used (image only, default: 3). Cannot be specified with `text`.
* `-o,--output <arg>`: The output file name (image only, default: invader.png). Cannot be specified with `text`.

Building
--------

The project uses [Gradle](http://www.gradle.org/). So long as you have a Java 7 or higher installed, you can just
run `./gradlew standalone` and a standalone jar file will be generated as `./build/libs/invaders-1.0-standalone.jar`.