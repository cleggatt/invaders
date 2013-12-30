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
import java.util.jar.Manifest;

public class Invaders {

    private final int width;
    private final int height;
    private final int scale;
    private final long maxValue;

    // TODO Comment width is doubled!
    public Invaders(int width, int height) {
        this(width, height, 1);
    }

    public Invaders(int width, int height, int scale) {
        this.width = width;
        this.height = height;
        this.scale = scale;
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

    private StringBuffer createTextCanvas() {
        // We allocate an extra character for each line to allow for the line feed
        final StringBuffer buffer = new StringBuffer((width * height * 2) + height);
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

    private BufferedImage createBufferedImageCanvas() {
        return new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_ARGB);
    }

    private BufferedImage getImageInvader(boolean[][] pixels) {
        final BufferedImage imageCanvas = createBufferedImageCanvas();
        renderInvader(pixels, new InvaderCanvas() {
            @Override
            public void drawPixel(int x, int y) {
                imageCanvas.setRGB(x, y, Color.GREEN.getRGB());
            }
        });

        if (scale == 1) {
            return imageCanvas;
        }

        final int w = imageCanvas.getWidth();
        final int h = imageCanvas.getHeight();

        final AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        final BufferedImage scaled = scaleOp.filter(imageCanvas, new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB));
        return scaled;
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
        final boolean[][] pixels = getPixels(value);
        return getTextInvader(pixels);
    }

    BufferedImage getImageInvader(long value) {
        final boolean[][] pixels = getPixels(value);
        return getImageInvader(pixels);
    }

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("t", "text", false, "generate invader as text");
        options.addOption("p", "png", false, "generate invader as PNG");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Invaders", options);
            System.exit(1);
        }

        final Invaders invader = new Invaders(4, 8, 3);
        final long value = invader.generateInvader();

        if (cmd.getOptions().length != 1) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Invaders", options);
        } else if (cmd.hasOption("t")) {
            System.out.print(String.format("Invader %d of %d\n", value, invader.getMaxValue()));
            System.out.println(invader.getTextInvader(value));
        } else if (cmd.hasOption("p")) {
            final BufferedImage image = invader.getImageInvader(value);
            try {
                final File output = new File("/home/chris/IdeaProjects/invaders/invader.png");
                ImageIO.write(image, "PNG", output);
                System.out.print(String.format("Invader %d of %d\n", value, invader.getMaxValue()));
                System.out.print(String.format("Saved to %s\n", output.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Invaders", options);
        }
    }
}