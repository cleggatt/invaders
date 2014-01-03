package com.cleggatt.invaders;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class InvadersTest {

    private static Random greenRandom() {
        final Random mock = mock(Random.class);
        stub(mock.nextInt(Invaders.COLORS.length)).toReturn(5);
        return(mock);
    }

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
                    {2, 2, (long) 0b0110, new boolean[][]{{false, true, true, false}, {true, false, false, true}}},
                    {2, 3, (long) 0b100110, new boolean[][]{{false, true, true, false}, {true, false, false, true}, {false, true, true, false}}}
            });
        }

        @Test
        public void testGetPixels() {
            // Set up
            final Invaders invaders = new Invaders(width, height, 1, mock(Random.class), mock(Random.class));
            // Exercise
            final boolean[][] pixels = invaders.getPixels(invader);
            // Verify
            assertArrayEquals(expectedPixels, pixels);
        }
    }

    @RunWith(Parameterized.class)
    public static class TextInvaderTest {

        private final int width;
        private final int height;
        private final long invader;
        private final String expectedTextInvader;

        public TextInvaderTest(int width, int height, long invader, String expectedTextInvader) {
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
                    {2, 2, (long) 0b0110, " ** \n*  *\n"},
                    {2, 3, (long) 0b100110, " ** \n*  *\n ** \n"}
            });
        }

        @Test
        public void testGetTextInvader() {
            // Set up
            Random random = mock(Random.class);
            final Invaders invaders = new Invaders(width, height, 1, random, greenRandom());

            double value = getRandomDoubleToGenerate(invader, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final String textInvader = invaders.getTextInvaders(1, 1, 0);
            // Verify
            assertEquals(expectedTextInvader, textInvader);
        }
    }

    public static class ScaledTextInvaderTest {
        @Test
        public void square() {
            // Set up
            Random random = mock(Random.class);
            final Invaders invaders = new Invaders(2, 2, 2, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b0110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final String textInvader = invaders.getTextInvaders(1, 1, 0);

            // Verify
            // @formatter:off
            assertEquals(
                    "  ****  \n" +
                    "  ****  \n" +
                    "**    **\n" +
                    "**    **\n", textInvader);
            // @formatter:on
        }

        @Test
        public void nonSquare() {
            // Set up
            Random random = mock(Random.class);
            final Invaders invaders = new Invaders(2, 3, 2, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b100110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final String textInvader = invaders.getTextInvaders(1, 1, 0);

            // Verify
            // @formatter:off
            assertEquals(
                    "  ****  \n" +
                    "  ****  \n" +
                    "**    **\n" +
                    "**    **\n" +
                    "  ****  \n" +
                    "  ****  \n", textInvader);
            // @formatter:on

        }
    }

    public static class ImageInvaderTest {
        @Test
        public void square() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 1, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b0110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(1, 1, 0);

            // Verify
            final BufferedImage expected = createBlackImage(4, 2);
            expected.setRGB(1, 0, Color.GREEN.getRGB());
            expected.setRGB(2, 0, Color.GREEN.getRGB());
            expected.setRGB(0, 1, Color.GREEN.getRGB());
            expected.setRGB(3, 1, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }

        @Test
        public void nonSquare() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 3, 1, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b100110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(1, 1, 0);

            // Verify
            final BufferedImage expected = createBlackImage(4, 3);
            expected.setRGB(1, 0, Color.GREEN.getRGB());
            expected.setRGB(2, 0, Color.GREEN.getRGB());
            expected.setRGB(0, 1, Color.GREEN.getRGB());
            expected.setRGB(3, 1, Color.GREEN.getRGB());
            expected.setRGB(1, 2, Color.GREEN.getRGB());
            expected.setRGB(2, 2, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }
    }

    public static class ScaledImageInvaderTest {
        @Test
        public void square() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 2, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b0110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(1, 1, 0);

            // Verify
            final BufferedImage expected = createBlackImage(8, 4);
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

        @Test
        public void nonSquare() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 3, 2, random, greenRandom());

            double value = getRandomDoubleToGenerate(0b100110, invaders.getMaxValue());
            stub(random.nextDouble()).toReturn(value);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(1, 1, 0);

            // Verify
            final BufferedImage expected = createBlackImage(8, 6);
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

            expected.setRGB(2, 4, Color.GREEN.getRGB());
            expected.setRGB(3, 4, Color.GREEN.getRGB());
            expected.setRGB(4, 4, Color.GREEN.getRGB());
            expected.setRGB(5, 4, Color.GREEN.getRGB());

            expected.setRGB(2, 5, Color.GREEN.getRGB());
            expected.setRGB(3, 5, Color.GREEN.getRGB());
            expected.setRGB(4, 5, Color.GREEN.getRGB());
            expected.setRGB(5, 5, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }
    }

    public static class TiledTest {

        @Test
        public void text() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 1, random, greenRandom());

            double valueOne = getRandomDoubleToGenerate(0b0001, invaders.getMaxValue());
            double valueTwo = getRandomDoubleToGenerate(0b0010, invaders.getMaxValue());
            double valueThree = getRandomDoubleToGenerate(0b1001, invaders.getMaxValue());
            double valueFour = getRandomDoubleToGenerate(0b1010, invaders.getMaxValue());
            when(random.nextDouble()).thenReturn(valueOne, valueTwo, valueThree, valueFour);

            // Exercise
            final String textInvader = invaders.getTextInvaders(2, 2, 0);

            // Verify
            // @formatter:off
            assertEquals(
                    "*  * ** \n" +
                    "        \n" +
                    "*  * ** \n" +
                    " **  ** \n", textInvader);
            // @formatter:on
        }

        @Test
        public void image() {
            // Set up
            Random random = mock(Random.class);
            Random colourRandom = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 1, random, colourRandom);

            double valueOne = getRandomDoubleToGenerate(0b0001, invaders.getMaxValue());
            double valueTwo = getRandomDoubleToGenerate(0b0010, invaders.getMaxValue());
            double valueThree = getRandomDoubleToGenerate(0b1001, invaders.getMaxValue());
            double valueFour = getRandomDoubleToGenerate(0b1010, invaders.getMaxValue());
            when(random.nextDouble()).thenReturn(valueOne, valueTwo, valueThree, valueFour);

            when(colourRandom.nextInt(Invaders.COLORS.length)).thenReturn(1, 5, 8, 2);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(2, 2, 0);

            // Verify
            final BufferedImage expected = createBlackImage(8, 4);

            expected.setRGB(0, 0, Invaders.COLORS[1].getRGB());
            expected.setRGB(3, 0, Invaders.COLORS[1].getRGB());

            expected.setRGB(5, 0, Invaders.COLORS[5].getRGB());
            expected.setRGB(6, 0, Invaders.COLORS[5].getRGB());

            expected.setRGB(0, 2, Invaders.COLORS[8].getRGB());
            expected.setRGB(3, 2, Invaders.COLORS[8].getRGB());
            expected.setRGB(1, 3, Invaders.COLORS[8].getRGB());
            expected.setRGB(2, 3, Invaders.COLORS[8].getRGB());

            expected.setRGB(5, 2, Invaders.COLORS[2].getRGB());
            expected.setRGB(6, 2, Invaders.COLORS[2].getRGB());
            expected.setRGB(5, 3, Invaders.COLORS[2].getRGB());
            expected.setRGB(6, 3, Invaders.COLORS[2].getRGB());

            assertImageEquals(expected, image);
        }

    }

    public static class BorderTest {

        @Test
        public void text() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 1, random, greenRandom());

            double valueOne = getRandomDoubleToGenerate(0b0001, invaders.getMaxValue());
            double valueTwo = getRandomDoubleToGenerate(0b0010, invaders.getMaxValue());
            double valueThree = getRandomDoubleToGenerate(0b1001, invaders.getMaxValue());
            double valueFour = getRandomDoubleToGenerate(0b1010, invaders.getMaxValue());
            when(random.nextDouble()).thenReturn(valueOne, valueTwo, valueThree, valueFour);

            // Exercise
            final String textInvader = invaders.getTextInvaders(2, 2, 2);

            // Verify
            // @formatter:off
            assertEquals(
                    // 1st Row
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "*  *" + "  " /**/ + "  " + " ** " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    // 2nd row
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "*  *" + "  " /**/ + "  " + " ** " + "  " + "\n" +
                    "  " + " ** " + "  " /**/ + "  " + " ** " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n" +
                    "  " + "    " + "  " /**/ + "  " + "    " + "  " + "\n"
                    // End
                    , textInvader);
            // @formatter:on
        }

        @Test
        public void scaledText() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 2, random, greenRandom());

            double valueOne = getRandomDoubleToGenerate(0b0001, invaders.getMaxValue());
            double valueTwo = getRandomDoubleToGenerate(0b0010, invaders.getMaxValue());
            double valueThree = getRandomDoubleToGenerate(0b1001, invaders.getMaxValue());
            double valueFour = getRandomDoubleToGenerate(0b1010, invaders.getMaxValue());
            when(random.nextDouble()).thenReturn(valueOne, valueTwo, valueThree, valueFour);

            // Exercise
            final String textInvader = invaders.getTextInvaders(2, 2, 2);

            // Verify
            // @formatter:off
            assertEquals(
                    // 1st row
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "**    **" + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "**    **" + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    // 2nd row
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "**    **" + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "**    **" + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "  ****  " + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "  ****  " + "  " /**/ + "  " + "  ****  " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n" +
                    "  " + "        " + "  " /**/ + "  " + "        " + "  " + "\n",
                    // End
                    textInvader);
            // @formatter:on
        }

        @Test
        public void image() {
            // Set up
            Random random = mock(Random.class);

            final Invaders invaders = new Invaders(2, 2, 1, random, greenRandom());

            double valueOne = getRandomDoubleToGenerate(0b0001, invaders.getMaxValue());
            double valueTwo = getRandomDoubleToGenerate(0b0010, invaders.getMaxValue());
            double valueThree = getRandomDoubleToGenerate(0b1001, invaders.getMaxValue());
            double valueFour = getRandomDoubleToGenerate(0b1010, invaders.getMaxValue());
            when(random.nextDouble()).thenReturn(valueOne, valueTwo, valueThree, valueFour);

            // Exercise
            final BufferedImage image = invaders.getImageInvaders(2, 2, 2);

            // Verify
            final BufferedImage expected = createBlackImage(16, 12);
            expected.setRGB(2, 2, Color.GREEN.getRGB());
            expected.setRGB(5, 2, Color.GREEN.getRGB());

            expected.setRGB(11, 2, Color.GREEN.getRGB());
            expected.setRGB(12, 2, Color.GREEN.getRGB());

            expected.setRGB(2, 8, Color.GREEN.getRGB());
            expected.setRGB(5, 8, Color.GREEN.getRGB());
            expected.setRGB(3, 9, Color.GREEN.getRGB());
            expected.setRGB(4, 9, Color.GREEN.getRGB());

            expected.setRGB(11, 8, Color.GREEN.getRGB());
            expected.setRGB(12, 8, Color.GREEN.getRGB());
            expected.setRGB(11, 9, Color.GREEN.getRGB());
            expected.setRGB(12, 9, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }
    }

    private static double getRandomDoubleToGenerate(long desiredValue, long maxValue ) {
        return (((double) desiredValue) - 1) / maxValue;
    }

    private static BufferedImage createBlackImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height,  BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.black);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

        return image;
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