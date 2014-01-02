package com.cleggatt.invaders;


import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class Main {

    private Main() {
    }

    private static Options options = new Options();
    static {
        options.addOption("t", "text", false, "generate invader as text");
        options.addOption("p", "png", false, "generate invader as PNG");
        options.addOption("s", "scale", true, "scaling factor for individual invaders (default: 1)");
        options.addOption("w", "width", true, "number of invaders wide (default: 1)");
        options.addOption("h", "height", true, "number of invaders high (default: 1)");
        options.addOption("b", "border", true, "border width  (default: 0)");
        options.addOption("seed", true, "random seed for invader generation");
    }

    // @VisibleForTesting
    static class Params {
        enum Format {
            Text, Image
        }

        private final Format format;
        private final int scale;
        private final int numWide;
        private final int numHigh;
        private final int border;
        private final Long seed;

        Format getFormat() {
            return format;
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

        Params(Format format, int scale, int numWide, int numHigh, int border, Long seed) {
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

        int scale;
        int width;
        int height;
        int border;
        try {
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

        // TODO There is no need to enforce this
        // width and height are required together or not at all
        if ((cmd.hasOption('w') && !cmd.hasOption('h')) || (!cmd.hasOption('w') && cmd.hasOption('h'))) {
            return null;
        }

        // TODO Make border around invidual tiles and then this can always be provided
        // border is only allowed when tiling
        if (cmd.hasOption('b') && !(cmd.hasOption('w') && cmd.hasOption('h'))) {
            return null;
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

        return new Params(fmt, scale, width, height, border, seed);
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

        final Invaders invader = new Invaders(4, 8, params.getScale(), seed(new Random(), params), new Random());

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