package com.cleggatt.invaders;


import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class Main {

    // @VisibleForTesting
    static final int DEFAULT_X = 4;
    private static final String DEFAULT_X_STR = String.valueOf(DEFAULT_X);
    // @VisibleForTesting
    static final int DEFAULT_Y = 8;
    private static final String DEFAULT_Y_STR = String.valueOf(DEFAULT_Y);

    private Main() {
    }

    private static Options options = new Options();
    static {
        options.addOption("t", "text", false, "generate as text");
        options.addOption("p", "png", false, "generate as PNG");

        options.addOption("x", true, String.format("number of un-mirrored, un-scaled pixels on the X axis of a tile (default: %d)", DEFAULT_X));
        options.addOption("y", true, String.format("number of un-scaled pixels on the Y axis of a tile (default: %d)", DEFAULT_Y));

        options.addOption("s", "scale", true, "scaling factor for a tile (default: 1)");

        options.addOption("w", "width", true, "number of tiles wide (default: 1)");
        options.addOption("h", "height", true, "number of tiles high (default: 1)");

        options.addOption("b", "border", true, "border width for a tile (default: 0)");

        options.addOption("seed", true, "random seed for tile generation");
    }

    // @VisibleForTesting
    static class Params {
        enum Format {
            Text, Image
        }

        private final Format format;
        private final int x;
        private final int y;
        private final int scale;
        private final int numWide;
        private final int numHigh;
        private final int border;
        private final Long seed;

        Format getFormat() {
            return format;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        int getScale() {
            return scale;
        }

        int getNumWide() {
            return numWide;
        }

        int getNumHigh() {
            return numHigh;
        }

        int getBorder() {
            return border;
        }

        public Long getSeed() {
            return seed;
        }

        Params(Format format, int x, int y, int scale, int numWide, int numHigh, int border, Long seed) {
            this.x = x;
            this.y = y;
            this.format = format;
            this.scale = scale;
            this.numWide = numWide;
            this.numHigh = numHigh;
            this.border = border;
            this.seed = seed;
        }
    }

    // @VisibleForTesting
    static Params parseParams(String[] args) {

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }

        int x;
        int y;
        int scale;
        int width;
        int height;
        int border;
        try {
            x = Integer.parseInt(cmd.getOptionValue('x', DEFAULT_X_STR));
            y = Integer.parseInt(cmd.getOptionValue('y', DEFAULT_Y_STR));
            scale = Integer.parseInt(cmd.getOptionValue('s', "1"));
            width = Integer.parseInt(cmd.getOptionValue('w', "1"));
            height = Integer.parseInt(cmd.getOptionValue('h', "1"));
            border = Integer.parseInt(cmd.getOptionValue('b', "0"));
        } catch (NumberFormatException e) {
            return null;
        }

        Long seed = null;
        if (cmd.hasOption("seed")) {
            try {
                seed = Long.parseLong(cmd.getOptionValue("seed"));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Params.Format fmt;

        if (cmd.hasOption('t')) {
            if (cmd.hasOption('p')) {
                return null;
            }
            fmt = Params.Format.Text;
        } else if (cmd.hasOption('p')) {
            fmt = Params.Format.Image;
        } else {
            return null;
        }

        return new Params(fmt, x, y, scale, width, height, border, seed);
    }

    // @VisibleForTesting
    static Random seed(Random random, Params params) {
        if (params.getSeed() != null) {
            random.setSeed(params.getSeed());
        }
        return random;
    }

    public static void main(String[] args) {

        Params params = parseParams(args);
        if (params == null) {
            new HelpFormatter().printHelp("Invaders", options);
            System.exit(1);
        }

        final Invaders invader = new Invaders(params.getX(), params.getY(), params.getScale(), seed(new Random(), params), new Random());

        switch (params.getFormat()) {
            case Text:
                System.out.println(invader.getTextInvaders(params.getNumWide(), params.getNumWide(), params.getBorder()));
                break;
            case Image:
                final BufferedImage image = invader.getImageInvaders(params.getNumWide(), params.getNumWide(), params.getBorder());
                final File output = new File("invader.png");
                try {
                    ImageIO.write(image, "PNG", output);
                    System.out.print(String.format("Saved to %s\n", output.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
        }
    }
}