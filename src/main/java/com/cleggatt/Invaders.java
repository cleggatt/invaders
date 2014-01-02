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

    private interface InvaderCanvas<T> {
        void drawPixel(int x, int y);
        T getInvader();
    }

    private void drawScaledPixel(final int x, final int y, InvaderCanvas canvas) {
        for (int scaledY = (y * scale); scaledY < ((y * scale)) + scale; scaledY++) {
            for (int scaledX = (x * scale); scaledX < ((x * scale)) + scale; scaledX++) {
                canvas.drawPixel(scaledX, scaledY);
            }
        }
    }

    private void renderInvader(boolean[][] pixels, InvaderCanvas canvas, int xOffset, int yOffset) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                if (pixels[y][x]) {
                    drawScaledPixel(xOffset + x, yOffset + y, canvas);
                }
            }
        }
    }

    private <T> T getInvaders(int numWide, int numHigh, InvaderCanvas<T> invaderCanvas) {

        boolean verbose = (numWide == 1 && numHigh == 1);

        int xOffset = 0, yOffset = 0;
        for (int y = 0; y < numHigh; y++) {
            for (int x = 0; x < numWide; x++) {
                final long value = generateInvader(verbose);
                renderInvader(getPixels(value), invaderCanvas, xOffset, yOffset);
                xOffset += (width * 2);
            }
            xOffset = 0;
            yOffset += height;
        }

        return invaderCanvas.getInvader();
    }

    private static class TextCanvas implements InvaderCanvas<String> {

        private final int scaledHeight;
        private final int scaledWidth;
        private int lineWidth;
        private final StringBuffer buffer;

        private TextCanvas(int width, int height, int scale, int numWide, int numHigh) {
            scaledHeight = height * scale;
            scaledWidth = width * scale;
            lineWidth = scaledWidth * 2 * numWide;
            buffer = createBuffer(height, scale, numWide, numHigh);
        }

        private StringBuffer createBuffer(final int height, final int scale, final int numWide, final int numHigh) {

            final int numLines = scaledHeight * numHigh;
            final int lineBreaks = scaledHeight;

            int bufferSize = (lineWidth * numLines) + lineBreaks;

            final StringBuffer buffer = new StringBuffer(bufferSize);

            int pixelsInLine = scaledWidth * 2 * numWide;
            for (int y = 0; y < numLines; y++) {
                for (int x = 0; x < pixelsInLine; x++) {
                    buffer.append(' ');
                }
                buffer.append('\n');
            }

            return buffer;
        }

        @Override
        public void drawPixel(int x, int y) {
            int pos = 0;
            pos = pos + (y * lineWidth); // Number of lines down
            pos = pos + y; // Allow for line feeds
            pos = pos + x; // Position on line
            buffer.setCharAt(pos, '*');
        }

        @Override
        public String getInvader() {
            return buffer.toString();
        }
    }

    private class ImageCanvas implements InvaderCanvas<BufferedImage> {

        private final BufferedImage image;

        private ImageCanvas(int width, int height, int scale, int numWide, int numHigh) {
            image = new BufferedImage(width * 2 * numWide * scale, height * numHigh * scale, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void drawPixel(int x, int y) {
            image.setRGB(x, y, Color.GREEN.getRGB());
        }

        @Override
        public BufferedImage getInvader() {
            return image;
        }
    }

    public String getTextInvader(int numWide, int numHigh) {
        return getInvaders(numWide, numHigh, new TextCanvas(width, height, scale, numWide, numHigh));
    }

    public BufferedImage getImageInvaders(int numWide, int numHigh) {
        return getInvaders(numWide, numHigh, new ImageCanvas(width, height, scale, numWide, numHigh));
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

        int width;
        int height;
        int scale;
        try {
            width = Integer.parseInt(cmd.getOptionValue('w', "1"));
            height = Integer.parseInt(cmd.getOptionValue('h', "1"));
            scale = Integer.parseInt(cmd.getOptionValue('s', "1"));
        } catch (NumberFormatException e) {
            return null;
        }

        if ((cmd.hasOption('w') && !cmd.hasOption('h')) || (!cmd.hasOption('w') && cmd.hasOption('h'))) {
            return null;
        }

        Params.Format fmt;

        if (cmd.hasOption('t')) {
            if (cmd.hasOption('p')) {
                return null;
            }
            fmt = Params.Format.Text;
        } else if (cmd.hasOption('p')) {
            if (cmd.hasOption('t')) {
                return null;
            }
            fmt = Params.Format.Image;
        } else {
            return null;
        }

        return new Params(fmt, scale, width, height);
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
                System.out.println(invader.getTextInvader(params.getNumWide(), params.getNumWide()));
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