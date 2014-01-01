package com.cleggatt;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Invaders {

    private final SecureRandom random;
    private final int width;
    private final int height;
    private final int scale;
    private final long maxValue;

    // TODO Comment width is doubled!
    public Invaders(int width, int height) {
        this(width, height, 1);
    }

    // TODO Comment width is doubled!
    public Invaders(int width, int height, int scale) {
        // We use SecureRandom to ensure we get the full range of long values (which won't be returned by Random)
        this(width, height, scale, new SecureRandom());
    }

    Invaders(int width, int height, int scale, SecureRandom random) {
        this.random = random;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.maxValue = new BigInteger("2", 10).pow(width * height).longValue();
    }

    public long getMaxValue() {
        return maxValue;
    }

    private long generateInvader(boolean verbose) {
        final long invader = (long) (random.nextDouble() * maxValue) + 1;
        if (verbose) {
            System.out.print(String.format("Invader %d of %d\n", invader, getMaxValue()));
        }
        return invader;
    }

    boolean[][] getPixels(long value) {

        final boolean[][] pixels = new boolean[height][width * 2];

        long pos = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = (value & pos) > 0;
                pos = pos << 1;
            }
            for (int x = width; x < (width * 2); x++) {
                pixels[y][x] = pixels[y][((width * 2) - 1) - x];
            }
        }

        return pixels;
    }

    private interface InvaderCanvas {
        void drawPixel(int x, int y);
    }

    private void renderInvader(boolean[][] pixels, InvaderCanvas canvas) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                if (pixels[y][x]) {
                    drawScaledPixel(x, y, canvas);
                }
            }
        }
    }

    private void drawScaledPixel(final int x, final int y, InvaderCanvas canvas) {
        for (int scaledY = (y * scale); scaledY < ((y * scale)) + scale; scaledY++) {
            for (int scaledX = (x * scale); scaledX < ((x * scale)) + scale; scaledX++) {
                canvas.drawPixel(scaledX, scaledY);
            }
        }
    }

    private StringBuffer createTextCanvas() {
        final int scaledHeight = height * scale;
        final int scaledWidth = width * scale;

        // We allocate an extra character for each line to allow for the line feed
        final StringBuffer buffer = new StringBuffer((scaledWidth * scaledHeight * 2) + scaledHeight);
        for (int y = 0; y < scaledHeight; y++) {
            for (int x = 0; x < (scaledWidth * 2); x++) {
                buffer.append(' ');
            }
            buffer.append('\n');
        }
        return buffer;
    }

    private String getTextInvader(boolean[][] pixels) {
        final int scaledWidth = width * scale;

        final StringBuffer buffer = createTextCanvas();
        renderInvader(pixels, new InvaderCanvas() {
            @Override
            public void drawPixel(int x, int y) {
                int pos = 0;
                pos = pos + (y * scaledWidth * 2); // Number of lines down
                pos = pos + y; // Allow for line feeds
                pos = pos + x; // Position on line
                buffer.setCharAt(pos, '*');
            }
        });

        return buffer.toString();
    }

    public String getTextInvader() {
        final long value = generateInvader(true);
        final boolean[][] pixels = getPixels(value);
        return getTextInvader(pixels);
    }

    private class ImageCanvas implements InvaderCanvas {

        private final BufferedImage image;
        int currentX, currentY;

        private ImageCanvas(BufferedImage image) {
            this.image = image;
            currentX = currentY = 0;
        }

        private void nextInvader() {
            currentX += (width * 2);
        }

        private void nextLine() {
            currentX = 0;
            currentY += height;
        }

        @Override
        public void drawPixel(int x, int y) {
            image.setRGB(currentX + x, currentY + y, Color.GREEN.getRGB());
        }
    }

    public BufferedImage getImageInvaders(int numWide, int numHigh) {

        final BufferedImage image = new BufferedImage(width * 2 * numWide * scale, height * numHigh * scale, BufferedImage.TYPE_INT_ARGB);
        final ImageCanvas imageCanvas = new ImageCanvas(image);

        for (int y = 0; y < numHigh; y++) {
            for (int x = 0; x < numWide; x++) {
                final long value = generateInvader(false);
                renderInvader(getPixels(value), imageCanvas);
                imageCanvas.nextInvader();
            }
            imageCanvas.nextLine();
        }

        return image;
    }

    private static Options options = new Options();

    static {
        options.addOption("t", "text", false, "generate invader as text");
        options.addOption("p", "png", false, "generate invader as PNG");
        options.addOption("s", "scale", true, "scaling factor");
        options.addOption("w", "width", true, "number of tiles wide (PNG only)");
        options.addOption("h", "height", true, "number of tiles high (PNG only)");
    }

    static class Params {

        enum Format {
            Text, Image
        }

        private Format format;
        private int scale;
        private int numWide;
        private int numHigh;

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

        Params(Format format, int scale, int numWide, int numHigh) {
            this.format = format;
            this.scale = scale;
            this.numWide = numWide;
            this.numHigh = numHigh;
        }
    }

    static Params parseParams(String[] args) {

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }

        int scale;
        try {
            scale = Integer.parseInt(cmd.getOptionValue('s', "1"));
        } catch (NumberFormatException e) {
            return null;
        }

        if (cmd.hasOption('t')) {
            if (cmd.hasOption('p') || cmd.hasOption('w') || cmd.hasOption('h')) {
                return null;
            }

            return new Params(Params.Format.Text, scale, 1, 1);
        } else if (cmd.hasOption('p')) {

            if ((cmd.hasOption('w') && !cmd.hasOption('h')) || (!cmd.hasOption('w') && cmd.hasOption('h'))) {
                return null;
            }

            int width;
            int height;
            try {
                width = Integer.parseInt(cmd.getOptionValue('w', "1"));
                height = Integer.parseInt(cmd.getOptionValue('h', "1"));
            } catch (NumberFormatException e) {
                return null;
            }

            return new Params(Params.Format.Image, scale, width, height);
        }

        return null;
    }

    public static void main(String[] args) {

        Params params = parseParams(args);
        if (params == null) {
            new HelpFormatter().printHelp("Invaders", options);
            System.exit(1);
        }

        final Invaders invader = new Invaders(4, 8, params.getScale());

        switch (params.format) {
            case Text:
                System.out.println(invader.getTextInvader());
                break;
            case Image:
                final BufferedImage image = invader.getImageInvaders(params.getNumWide(), params.getNumWide());
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