package com.cleggatt;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class InvadersTest {

    @RunWith(Parameterized.class)
    public static class GetPixelsTest {

        private final int width;
        private final int height;
        private final long invader;
        private final boolean[][] expectedPixels;

        public GetPixelsTest(int width, int height, long invader, boolean[][] expectedPixels) {
            this.width = width;
            this.height = height;
            this.invader = invader;
            this.expectedPixels = expectedPixels;
        }

        @Parameterized.Parameters
        public static Collection primeNumbers() {
            return Arrays.asList(new Object[][]{
                    {1, 1, (long) 0b1, new boolean[][]{{true, true}}},
                    {2, 1, (long) 0b10, new boolean[][]{{false, true, true, false}}},
                    {2, 2, (long) 0b0110, new boolean[][]{{false, true, true, false}, {true, false, false, true}}}
            });
        }

        @Test
        public void testGetPixels() {
            // Set up
            final Invaders invaders = new Invaders(width, height);
            // Exercise
            final boolean[][] pixels = invaders.getPixels(invader);
            // Verify
            assertArrayEquals(expectedPixels, pixels);
        }
    }

    @RunWith(Parameterized.class)
    public static class GetTextInvaderTest {

        private final int width;
        private final int height;
        private final long invader;
        private final String expectedTextInvader;

        public GetTextInvaderTest(int width, int height, long invader, String expectedTextInvader) {
            this.width = width;
            this.height = height;
            this.invader = invader;
            this.expectedTextInvader = expectedTextInvader;
        }

        @Parameterized.Parameters
        public static Collection primeNumbers() {
            return Arrays.asList(new Object[][]{
                    {1, 1, (long) 0b1, "**\n"},
                    {2, 1, (long) 0b10, " ** \n"},
                    {2, 2, (long) 0b0110, " ** \n*  *\n"}
            });
        }

        @Test
        public void testGetTextInvader() {
            // Set up
            final Invaders invaders = new Invaders(width, height);
            // Exercise
            final String textInvader = invaders.getTextInvader(invader);
            // Verify
            assertEquals(expectedTextInvader, textInvader);
        }
    }

    public static class GetImageInvaderTest {

        @Test
        public void testInvaderGeneration() {
            // Set up
            final Invaders invaders = new Invaders(2, 2);
            // Exercise
            final BufferedImage image = invaders.getImageInvader(0b0110);
            // Verify
            final BufferedImage expected = new BufferedImage(4, 2, BufferedImage.TYPE_INT_ARGB);
            expected.setRGB(1, 0, Color.GREEN.getRGB());
            expected.setRGB(2, 0, Color.GREEN.getRGB());
            expected.setRGB(0, 1, Color.GREEN.getRGB());
            expected.setRGB(3, 1, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }

        @Test
        public void testScaledInvaderGeneration() {
            // Set up
            final Invaders invaders = new Invaders(2, 2, 2);
            // Exercise
            final BufferedImage image = invaders.getImageInvader(0b0110);
            // Verify
            final BufferedImage expected = new BufferedImage(8, 4, BufferedImage.TYPE_INT_ARGB);
            expected.setRGB(2, 0, Color.GREEN.getRGB());
            expected.setRGB(3, 0, Color.GREEN.getRGB());
            expected.setRGB(4, 0, Color.GREEN.getRGB());
            expected.setRGB(5, 0, Color.GREEN.getRGB());
            expected.setRGB(2, 1, Color.GREEN.getRGB());
            expected.setRGB(3, 1, Color.GREEN.getRGB());
            expected.setRGB(4, 1, Color.GREEN.getRGB());
            expected.setRGB(5, 1, Color.GREEN.getRGB());
            expected.setRGB(0, 2, Color.GREEN.getRGB());
            expected.setRGB(1, 2, Color.GREEN.getRGB());
            expected.setRGB(6, 2, Color.GREEN.getRGB());
            expected.setRGB(7, 2, Color.GREEN.getRGB());
            expected.setRGB(0, 3, Color.GREEN.getRGB());
            expected.setRGB(1, 3, Color.GREEN.getRGB());
            expected.setRGB(6, 3, Color.GREEN.getRGB());
            expected.setRGB(7, 3, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }
    }

    private static void assertImageEquals(BufferedImage expected, BufferedImage actual) {
        assertEquals("image width", expected.getWidth(), actual.getWidth());
        assertEquals("image height", expected.getHeight(), actual.getHeight());
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                if (expected.getRGB(x, y) != actual.getRGB(x, y)) {
                    fail(String.format("Images not equal at (%d,%d). Expected %d, actual %d", x, y, expected.getRGB(x, y), actual.getRGB(x, y)));
                }
            }
        }
    }
}