package com.cleggatt;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class Invaders {

    private final int width;
    private final int height;
    private final long maxValue;

    // TODO Comment width is doubled!
    public Invaders(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxValue = new BigInteger("2", 10).pow(width * height).longValue();
    }

    public long getMaxValue() {
        return maxValue;
    }

    long generateInvader() {
        // TODO Comment secure random to get full spread of longs
        final SecureRandom random = new SecureRandom();
        return (long) (random.nextDouble() * maxValue) + 1;
    }

    boolean[][] getPixels(long value) {

        boolean[][] pixels = new boolean[height][width * 2];

        long pos = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = (value & pos) > 0;
                pos = pos << 1;
            }
            for (int x = width; x < (width * 2) ; x++) {
                pixels[y][x] = pixels[y][((width * 2) - 1) - x];
            }
        }

        return pixels;
    }

    private StringBuffer createTextCanvas() {
        // We allocate an extra character for each line to allow for the line feed
        StringBuffer buffer = new StringBuffer((width * height * 2) + height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                buffer.append(' ');
            }
            buffer.append('\n');
        }
        return buffer;
    }

    private String getTextInvader(boolean[][] pixels) {
        final StringBuffer textCanvas = createTextCanvas();
        renderInvader(pixels, new InvaderCanvas() {
            @Override
            public void drawPixel(int x, int y) {
                int pos = 0;
                pos = pos + (y * width * 2); // Number of lines down
                pos = pos + y; // Allow for line feeds
                pos = pos + x; // Position on line
                textCanvas.setCharAt(pos, '*');
            }
        });

        return textCanvas.toString();
    }

    private interface InvaderCanvas {
        void drawPixel(int x, int y);
    }

    private void renderInvader(boolean[][] pixels, InvaderCanvas canvas) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < (width * 2); x++) {
                if (pixels[y][x]) {
                    canvas.drawPixel(x, y);
                }
            }
        }
    }

    String getTextInvader(long value) {
        boolean[][] pixels = getPixels(value);
        return getTextInvader(pixels);
    }

    public static void main(String[] args) {
        Invaders invader = new Invaders(4, 8);

        long value = invader.generateInvader();

        System.out.print("Invader " + value + " of " + invader.getMaxValue() + "\n");
        System.out.println(invader.getTextInvader(value));
    }
}