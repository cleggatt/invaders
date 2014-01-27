package com.cleggatt.invaders;


import org.apache.commons.cli.*;
import org.jdesktop.swingx.image.GaussianBlurFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public final class Main {
    // VisibleForTesting
    static final int DEFAULT_X = 4;
    private static final String DEFAULT_X_STR = String.valueOf(DEFAULT_X);
    // VisibleForTesting
    static final int DEFAULT_Y = 6;
    private static final String DEFAULT_Y_STR = String.valueOf(DEFAULT_Y);
    // VisibleForTesting
    static final int DEFAULT_BLUR = 3;
    private static final String DEFAULT_BLUR_STR = String.valueOf(DEFAULT_BLUR);
    // VisibleForTesting
    static final String DEFAULT_OUTPUT_STR = "invader.png";

    private Main() {
    }

    private static Options options = new Options();

    static {
        options.addOption("help", false, "display this help and exit");

        options.addOption("t", "text", false, "generate as text");
        options.addOption("p", "png", false, "generate as PNG");

        options.addOption("x", true, String.format("number of un-mirrored, un-scaled pixels on the X axis of a tile (default: %d)", DEFAULT_X));
        options.addOption("y", true, String.format("number of un-scaled pixels on the Y axis of a tile (default: %d)", DEFAULT_Y));

        options.addOption("s", "scale", true, "scaling factor for a tile (default: 1)");

        options.addOption("tileX", true, "number of tiles to create along the X axis (default: 1)");
        options.addOption("tileY", true, "number of tiles to create along the Y axis  (default: 1)");

        options.addOption("pxWidth", true, "the width (in pixels) of the final image");
        options.addOption("pxHeight", true, "the height (in pixels) of the final image");

        options.addOption("b", "border", true, "border width for a tile (default: 0)");

        options.addOption("seed", true, "random seed for tile generation");

        options.addOption("guassian", true, String.format("guassian blur radius (image only, default: %d)", DEFAULT_BLUR));

        options.addOption("o", "output", true, String.format("output file name (image only, default: %s)", DEFAULT_OUTPUT_STR));
    }

    // VisibleForTesting
    static class Params {
        enum Format {
            Text, Image
        }

        private final Format format;
        private final int x;
        private final int y;
        private final int scale;
        private final int tileX;
        private final int tileY;
        private final int border;
        private final Long seed;
        private final int blurRadius;
        private final String outputFile;

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

        int getTileX() {
            return tileX;
        }

        int getTileY() {
            return tileY;
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

        public String getOutputFile() {
            return outputFile;
        }

        Params(Format format, int x, int y, int scale, int tileX, int tileY, int border, Long seed, int blurRadius, String outputFile) {
            this.x = x;
            this.y = y;
            this.format = format;
            this.scale = scale;
            this.tileX = tileX;
            this.tileY = tileY;
            this.border = border;
            this.seed = seed;
            this.blurRadius = blurRadius;
            this.outputFile = outputFile;
        }
    }

    private static class InvaderCommandLine {

        private final CommandLine cmd;

        private InvaderCommandLine(String[] args) throws ParseException {
            try {
                cmd = new BasicParser().parse(options, args);
            } catch (ParseException e) {
                throw new ParseException(optErr(e.getMessage()));
            }
        }

        private int getInt(char option, int min, String defaultValue) throws ParseException {
            return getIntArgument("-", String.valueOf(option), min, defaultValue);
        }

        private int getInt(String option, int min, String defaultValue) throws ParseException {
            return getIntArgument("--", option, min, defaultValue);
        }

        private int getIntArgument(String prefix, String option, int min, String defaultValue) throws ParseException {
            final String argument = cmd.getOptionValue(option, defaultValue);
            int value;
            try {
                value = Integer.parseInt(argument);
            } catch (NumberFormatException e) {
                throw new ParseException(argErr(prefix, option, argument));
            }
            if (value < min) {
                throw new ParseException(argErr(prefix, option, argument));
            }
            return value;
        }

        private Long getLong(String option) throws ParseException {
            final String argument = cmd.getOptionValue(option);
            try {
                return Long.parseLong(argument);
            } catch (NumberFormatException e) {
                throw new ParseException(argErr("--", option, argument));
            }
        }

        public String getString(String option, String defaultValue) {
            return cmd.getOptionValue(option, defaultValue);
        }

        private boolean hasOption(char opt) {
            return cmd.hasOption(opt);
        }

        private boolean hasOption(String opt) {
            return cmd.hasOption(opt);
        }

    }

    // VisibleForTesting
    static Params parseParams(String[] args) throws ParseException {

        InvaderCommandLine cmd = new InvaderCommandLine(args);

        if (cmd.hasOption("help")) {
            return null;
        }

        int x = cmd.getInt('x', 1, DEFAULT_X_STR);
        int y = cmd.getInt('y', 1, DEFAULT_Y_STR);
        int scale = cmd.getInt('s', 1, "1");
        int border = cmd.getInt('b', 0, "1");

        if (x * y > 62) {
            throw new ParseException(err("invalid arguments for '-x' and '-y'\nTheir products must be less than 63"));
        }

        Long seed = null;
        if (cmd.hasOption("seed")) {
            seed = cmd.getLong("seed");
        }

        int tileX = 1;
        int tileY = 1;
        if (cmd.hasOption("tileX") || cmd.hasOption("tileY")) {
            if (cmd.hasOption("pxWidth") || cmd.hasOption("pxHeight")) {
                throw new ParseException(optErr("Option 'pxWidth' or 'pxHeight' cannot be specified with option 'tileX' or 'tileY'"));
            }
           tileX = cmd.getInt("tileX", 1, "1");
           tileY = cmd.getInt("tileY", 1, "1");
        } else {
            if (cmd.hasOption("pxWidth")) {
                int pxWidth = cmd.getInt("pxWidth", 1, "1");
                tileX = pxWidth / (((x + border) * 2) * scale);
                if (tileX < 1) {
                    throw new ParseException(optErr("Option 'pxWidth' must be wide enough for at least 1 tile"));
                }
            }
            if (cmd.hasOption("pxHeight")) {
                int pxHeight = cmd.getInt("pxHeight", 1, "1");
                tileY = pxHeight / ((y + (border * 2)) * scale);
                if (tileY < 1) {
                    throw new ParseException(optErr("Option 'pxHeight' must be high enough for at least 1 tile"));
                }
            }
        }

        Params.Format fmt;
        int blurRadius = 0;
        String output = null;

        if (cmd.hasOption('t')) {
            if (cmd.hasOption('p')) {
                throw new ParseException(optErr("Option 'png' cannot be specified with option 'text'"));
            }
            if (cmd.hasOption("guassian")) {
                throw new ParseException(optErr("Option 'guassian' cannot be specified with option 'text'"));
            }
            if (cmd.hasOption("output")) {
                throw new ParseException(optErr("Option 'output' cannot be specified with option 'text'"));
            }
            fmt = Params.Format.Text;
        } else if (cmd.hasOption('p')) {
            fmt = Params.Format.Image;
            blurRadius = cmd.getInt("guassian", 0, DEFAULT_BLUR_STR);
            output = cmd.getString("output", DEFAULT_OUTPUT_STR);
        } else {
            throw new ParseException(optErr("Option 'text' or option 'png' must be specified"));
        }

        return new Params(fmt, x, y, scale, tileX, tileY, border, seed, blurRadius, output);
    }

    private static String optErr(String err) {
        return err(String.format("invalid option -- %s", err));
    }

    private static String argErr(String prefix, String opt, String arg) {
        return err(String.format("invalid argument '%s' for '%s%s'", arg, prefix, opt));
    }

    private static String err(String err) {
        return String.format("invaders: %s\nTry 'invaders --help' for more information.", err);
    }

    // VisibleForTesting
    static Random seed(Random random, Params params) {
        if (params.getSeed() != null) {
            random.setSeed(params.getSeed());
        }
        return random;
    }

    private static BufferedImage blur(BufferedImage src, int blurRadius) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        GaussianBlurFilter gaussianFilter = new GaussianBlurFilter(blurRadius);
        gaussianFilter.filter(src, dst);
        return dst;
    }

    public static void main(String[] args) {

        Params params = null;
        try {
            params = parseParams(args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (params == null) {
            new HelpFormatter().printHelp("invaders", options, true);
            System.exit(0);
        }

        final Invaders invader = new Invaders(params.getX(), params.getY(), params.getScale(), seed(new Random(), params), new Random());

        switch (params.getFormat()) {
            case Text:
                System.out.println(invader.getTextInvaders(params.getTileX(), params.getTileX(), params.getBorder()));
                break;
            case Image:
                // TODO Center image if the pixel count leaves a remainder
                final BufferedImage image = blur(invader.getImageInvaders(params.getTileX(), params.getTileY(), params.getBorder()), params.getBlurRadius());
                // TODO Validate file details
                final File output = new File(params.getOutputFile());
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