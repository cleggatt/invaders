package com.cleggatt.invaders;


import org.apache.commons.cli.*;
import org.jdesktop.swingx.image.GaussianBlurFilter;

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
    static final int DEFAULT_Y = 6;
    private static final String DEFAULT_Y_STR = String.valueOf(DEFAULT_Y);
    // @VisibleForTesting
    static final int DEFAULT_BLUR = 3;
    private static final String DEFAULT_BLUR_STR = String.valueOf(DEFAULT_BLUR);

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

        options.addOption("guassian", true, String.format("guassian blur radius (image only, default: %d)", DEFAULT_BLUR));
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
        private final int blurRadius;

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

        Long getSeed() {
            return seed;
        }

        int getBlurRadius() {
            return blurRadius;
        }

        Params(Format format, int x, int y, int scale, int numWide, int numHigh, int border, Long seed, int blurRadius) {
            this.x = x;
            this.y = y;
            this.format = format;
            this.scale = scale;
            this.numWide = numWide;
            this.numHigh = numHigh;
            this.border = border;
            this.seed = seed;
            this.blurRadius = blurRadius;
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

        // TODO Validate X and Y are less than a long
        Long seed = null;
        if (cmd.hasOption("seed")) {
            try {
                seed = Long.parseLong(cmd.getOptionValue("seed"));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Params.Format fmt;
        int blurRadius = 0;

        if (cmd.hasOption('t')) {
            if (cmd.hasOption('p') || cmd.hasOption("guassian")) {
                return null;
            }
            fmt = Params.Format.Text;
        } else if (cmd.hasOption('p')) {
            fmt = Params.Format.Image;

            try {
                blurRadius = Integer.parseInt(cmd.getOptionValue("guassian", DEFAULT_BLUR_STR));

            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }

        return new Params(fmt, x, y, scale, width, height, border, seed, blurRadius);
    }

    // @VisibleForTesting
    static Random seed(Random random, Params params) {
        if (params.getSeed() != null) {
            random.setSeed(params.getSeed());
        }
        return random;
    }

    static BufferedImage blur(BufferedImage src, int blurRadius) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        GaussianBlurFilter gaussianFilter = new GaussianBlurFilter(blurRadius);
        gaussianFilter.filter(src, dst);
        return dst;
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
                final BufferedImage image = blur(invader.getImageInvaders(params.getNumWide(), params.getNumWide(), params.getBorder()), params.getBlurRadius());
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