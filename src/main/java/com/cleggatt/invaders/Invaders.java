package com.cleggatt.invaders;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Random;

public class Invaders {

    static Color[] COLORS = new Color[]{
            Color.red,
            Color.lightGray,
            Color.pink,
            Color.orange,
            Color.yellow,
            Color.green,
            Color.magenta,
            Color.cyan,
            Color.blue
    };

    private final Random invaderRandom;
    private final Random colourRandom;
    private final int width;
    private final int height;
    private final int scale;
    private final long maxValue;

    /**
     * @param width the width of the randomly generated tile. This is <b>half</b> the width of the final tile.
     */
    public Invaders(int width, int height, int scale, Random invaderRandom, Random colourRandom) {
        this.invaderRandom = invaderRandom;
        this.colourRandom = colourRandom;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.maxValue = new BigInteger("2", 10).pow(width * height).longValue();
    }

    public long getMaxValue() {
        return maxValue;
    }

    private long generateInvader(boolean verbose) {
        final long invader = (long)(invaderRandom.nextDouble() * maxValue) + 1;
        if (verbose) {
            System.out.print(String.format("Invader %d of %d\n", invader, maxValue));
        }
        return invader;
    }

    // @VisibleForTesting
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
        void drawPixel(int x, int y, int colour);
        T getInvader();
    }

    private Color getColor() {
        return COLORS[colourRandom.nextInt(COLORS.length)];
    }

    private void drawScaledPixel(final int x, final int y, final int xOffset, final int yOffset, final int colour, InvaderCanvas canvas) {
        for (int scaledY = (y * scale); scaledY < ((y * scale)) + scale; scaledY++) {
            for (int scaledX = (x * scale); scaledX < ((x * scale)) + scale; scaledX++) {
                canvas.drawPixel(xOffset + scaledX, yOffset + scaledY, colour);
            }
        }
    }

    private void renderInvader(boolean[][] pixels, InvaderCanvas canvas, final int xOffset, final int yOffset) {

        final int colour = getColor().getRGB();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                if (pixels[y][x]) {
                    drawScaledPixel(x, y, xOffset, yOffset, colour, canvas);
                }
            }
        }
    }

    private <T> T getInvaders(final int numWide, final int numHigh, final int border, InvaderCanvas<T> invaderCanvas) {

        boolean verbose = (numWide == 1 && numHigh == 1);

        int xOffset = border,
            yOffset = border;
        for (int y = 0; y < numHigh; y++) {
            for (int x = 0; x < numWide; x++) {
                final long value = generateInvader(verbose);
                renderInvader(getPixels(value), invaderCanvas, xOffset, yOffset);

                xOffset = xOffset + ((width * 2 * scale) + (border * 2));
            }
            xOffset = border;
            yOffset = yOffset + (height * scale) + (border * 2);
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
            lineWidth = (scaledWidth * 2 * numWide) + (numWide * border * 2);
            buffer = createBuffer(numWide, numHigh, border);
        }

        private StringBuffer createBuffer(final int numWide, final int numHigh, final int border) {

            final int numLines = (scaledHeight * numHigh) + (numHigh * border * 2);
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
        public void drawPixel(final int x, final int y, final int colour) {
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

    private static class ImageCanvas implements InvaderCanvas<BufferedImage> {

        private final BufferedImage image;

        private ImageCanvas(int width, int height, int scale, int numWide, int numHigh, int border) {
            int imageWidth = (width * 2 * numWide * scale) + (numWide * border * 2);
            int imageHeight = (height * numHigh * scale) + (numHigh * border * 2);

            image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void drawPixel(int x, int y, int colour) {
            image.setRGB(x, y, colour);
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
}