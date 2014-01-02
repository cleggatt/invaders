package com.cleggatt;

import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.awt.*;
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

    /**
     * @param width the width of the randomly generated tile. This is <b>half</b> the width of the final tile.
     */
    public Invaders(int width, int height) {
        this(width, height, 1);
    }

    /**
     * @param width the width of the randomly generated tile. This is <b>half</b> the width of the final tile.
     */
    public Invaders(int width, int height, int scale) {
        this(width, height, scale, new SecureRandom());
    }

    /**
     * @param width the width of the randomly generated tile. This is <b>half</b> the width of the final tile.
     */
    // We use SecureRandom to ensure we get the full range of long values (which won't be returned by Random)
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

    boolean[][] getPixels(final long value) {

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

    private void drawScaledPixel(final int x, final int y, final int xOffset, final int yOffset, InvaderCanvas canvas) {
        for (int scaledY = (y * scale); scaledY < ((y * scale)) + scale; scaledY++) {
            for (int scaledX = (x * scale); scaledX < ((x * scale)) + scale; scaledX++) {
                canvas.drawPixel(xOffset + scaledX, yOffset + scaledY);
            }
        }
    }

    private void renderInvader(boolean[][] pixels, InvaderCanvas canvas, final int xOffset, final int yOffset) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                if (pixels[y][x]) {
                    drawScaledPixel(x, y, xOffset, yOffset, canvas);
                }
            }
        }
    }

    private <T> T getInvaders(final int numWide, final int numHigh, final int border, InvaderCanvas<T> invaderCanvas) {

        boolean verbose = (numWide == 1 && numHigh == 1);

        int xOffset = 0, yOffset = 0;
        for (int y = 0; y < numHigh; y++) {
            for (int x = 0; x < numWide; x++) {
                final long value = generateInvader(verbose);
                renderInvader(getPixels(value), invaderCanvas, xOffset, yOffset);

                xOffset = xOffset + ((width * 2 * scale) + border);
            }
            xOffset = 0;
            yOffset = yOffset + (height * scale) + border;
        }

        return invaderCanvas.getInvader();
    }

    private static class TextCanvas implements InvaderCanvas<String> {

        private final int scaledHeight;
        private final int scaledWidth;
        private int lineWidth;
        private final StringBuffer buffer;

        private TextCanvas(int width, int height, int scale, int numWide, int numHigh, int border) {
            scaledHeight = height * scale;
            scaledWidth = width * scale;
            lineWidth = (scaledWidth * 2 * numWide) + ((numWide - 1) * border);
            buffer = createBuffer(numWide, numHigh, border);
        }

        private StringBuffer createBuffer(final int numWide, final int numHigh, final int border) {

            final int numLines = (scaledHeight * numHigh) + ((numHigh - 1) * border);
            final int lineBreaks = scaledHeight;

            int bufferSize = (lineWidth * numLines) + lineBreaks;

            final StringBuffer buffer = new StringBuffer(bufferSize);

            for (int y = 0; y < numLines; y++) {
                for (int x = 0; x < lineWidth; x++) {
                    buffer.append(' ');
                }
                buffer.append('\n');
            }

            return buffer;
        }

        @Override
        public void drawPixel(final int x, final int y) {
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

        private ImageCanvas(int width, int height, int scale, int numWide, int numHigh, int border) {
            int imageWidth = (width * 2 * numWide * scale) + ((numWide - 1) * border);
            int imageHeight = (height * numHigh * scale) + ((numHigh - 1) * border);

            image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
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

    public String getTextInvaders(final int numWide, final int numHigh, final int border) {
        return getInvaders(numWide, numHigh, border, new TextCanvas(width, height, scale, numWide, numHigh, border));
    }

    public BufferedImage getImageInvaders(final int numWide, final int numHigh, final int border) {
        return getInvaders(numWide, numHigh, border, new ImageCanvas(width, height, scale, numWide, numHigh, border));
    }

    private static Options options = new Options();

    static {
        options.addOption("t", "text", false, "generate invader as text");
        options.addOption("p", "png", false, "generate invader as PNG");
        options.addOption("s", "scale", true, "scaling factor");
        options.addOption("w", "width", true, "number of tiles wide");
        options.addOption("h", "height", true, "number of tiles high");
        options.addOption("b", "border", true, "border width");
    }

    static class Params {

        enum Format {
            Text, Image
        }

        private final Format format;
        private final int scale;
        private final int numWide;
        private final int numHigh;
        private final int border;

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

        Params(Format format, int scale, int numWide, int numHigh, int border) {
            this.format = format;
            this.scale = scale;
            this.numWide = numWide;
            this.numHigh = numHigh;
            this.border = border;
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

        // width and height are required togther or not at all
        if ((cmd.hasOption('w') && !cmd.hasOption('h')) || (!cmd.hasOption('w') && cmd.hasOption('h'))) {
            return null;
        }

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
            if (cmd.hasOption('t')) {
                return null;
            }
            fmt = Params.Format.Image;
        } else {
            return null;
        }

        return new Params(fmt, scale, width, height, border);
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