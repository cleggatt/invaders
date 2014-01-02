package com.cleggatt;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.awt.Color;
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
            final BufferedImage expected = new BufferedImage(4, 2, BufferedImage.TYPE_INT_ARGB);
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
            final BufferedImage expected = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
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
            final BufferedImage expected = new BufferedImage(8, 6, BufferedImage.TYPE_INT_ARGB);
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
            final BufferedImage expected = new BufferedImage(8, 4, BufferedImage.TYPE_INT_ARGB);

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
                    "*  *" + "  " + " ** \n" +
                    "    " + "  " + "    \n" +
                    // Start X border
                    "    " + "  " + "    \n" +
                    "    " + "  " + "    \n" +
                    // End X border
                    "*  *" + "  " + " ** \n" +
                    " ** " + "  " + " ** \n", textInvader);
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
                    "**    **" + "  " + "  ****  \n" +
                    "**    **" + "  " + "  ****  \n" +
                    "        " + "  " + "        \n" +
                    "        " + "  " + "        \n" +
                    // Start Y border
                    "        " + "  " + "        \n" +
                    "        " + "  " + "        \n" +
                    // End Y border
                    "**    **" + "  " + "  ****  \n" +
                    "**    **" + "  " + "  ****  \n" +
                    "  ****  " + "  " + "  ****  \n" +
                    "  ****  " + "  " + "  ****  \n", textInvader);
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
            final BufferedImage expected = new BufferedImage(10, 6, BufferedImage.TYPE_INT_ARGB);
            expected.setRGB(0, 0, Color.GREEN.getRGB());
            expected.setRGB(3, 0, Color.GREEN.getRGB());

            expected.setRGB(7, 0, Color.GREEN.getRGB());
            expected.setRGB(8, 0, Color.GREEN.getRGB());

            expected.setRGB(0, 4, Color.GREEN.getRGB());
            expected.setRGB(3, 4, Color.GREEN.getRGB());
            expected.setRGB(1, 5, Color.GREEN.getRGB());
            expected.setRGB(2, 5, Color.GREEN.getRGB());

            expected.setRGB(7, 4, Color.GREEN.getRGB());
            expected.setRGB(8, 4, Color.GREEN.getRGB());
            expected.setRGB(7, 5, Color.GREEN.getRGB());
            expected.setRGB(8, 5, Color.GREEN.getRGB());

            assertImageEquals(expected, image);
        }
    }

    public static class ParseParamsTest {
        @Test
        public void textParamShouldReturnSingleText() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--text"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Text, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void textParamWithScaleShouldReturnScaledText() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--text", "--scale", "3"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Text, params.getFormat());
            assertEquals(3, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void textWithTileParamShouldReturnTiledImage() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--text", "--width",  "4", "--height", "5", "--border", "2"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Text, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(4, params.getNumWide());
            assertEquals(5, params.getNumHigh());
            assertEquals(2, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngParamShouldReturnSingleImage() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--png"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Image, params.getFormat());
            assertEquals(1,params.getScale(), 1);
            assertEquals(1,params.getNumWide(), 1);
            assertEquals(1,params.getNumHigh(), 1);
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngParamShouldReturnScaledImage() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--png", "--scale", "3"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Image, params.getFormat());
            assertEquals(3, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngWithTileParamShouldReturnTiledImage() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--png", "--width",  "4", "--height", "5", "--border", "2"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Image, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(4, params.getNumWide());
            assertEquals(5, params.getNumHigh());
            assertEquals(2, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void seedShouldBeParsed() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(new String[]{"--png", "--seed", "42"});
            // Verify
            assertNotNull(params);
            assertEquals(Invaders.Params.Format.Image, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(Long.valueOf(42), params.getSeed());
        }
    }

    @RunWith(Parameterized.class)
    public static class ParseInvalidParamsTest {

        private final String[] args;

        public ParseInvalidParamsTest(String[] args) {
            this.args = args;
        }

        @Parameterized.Parameters
        public static Collection primeNumbers() {
            return Arrays.asList(new Object[][]{
                    {new String[]{"-x"}},
                    {new String[]{"--scale"}},
                    {new String[]{"--width"}},
                    {new String[]{"--height"}},
                    {new String[]{"--border"}},
                    {new String[]{"--seed"}},
                    {new String[]{"--width", "5"}},
                    {new String[]{"--height", "5"}},
                    {new String[]{"--border", "5"}},
                    {new String[]{"--seed", "42"}},
                    {new String[]{"--width", "5", "--height", "5"}},
                    {new String[]{"--text", "--png"}},
                    {new String[]{"--text", "--scale", "X"}},
                    {new String[]{"--text", "--seed", "X"}},
                    {new String[]{"--text", "--width", "5"}},
                    {new String[]{"--text", "--height", "5"}},
                    {new String[]{"--text", "--border", "5"}},
                    {new String[]{"--text", "--width", "X", "--height", "5", "--border", "2"}},
                    {new String[]{"--text", "--width", "5", "--height", "X", "--border", "2"}},
                    {new String[]{"--text", "--width", "5", "--height", "5", "--border", "X"}},
                    {new String[]{"--png", "--scale", "X"}},
                    {new String[]{"--png", "--seed", "X"}},
                    {new String[]{"--png", "--width", "5"}},
                    {new String[]{"--png", "--height", "5"}},
                    {new String[]{"--png", "--border", "5"}},
                    {new String[]{"--png", "--width", "X", "--height", "5", "--border", "2"}},
                    {new String[]{"--png", "--width", "5", "--height", "X", "--border", "2"}},
                    {new String[]{"--png", "--width", "5", "--height", "5", "--border", "X"}},
            });
        }

        @Test
        public void testParseInvalidParams() {
            // Exercise
            final Invaders.Params params = Invaders.parseParams(this.args);
            // Verify
            assertNull(params);
        }
    }

    public static class SeedTest {
        @Test
        public void seedParamShouldBeSetAsSeed() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Invaders.Params params = new Invaders.Params(Invaders.Params.Format.Image, 0, 0, 0, 0, 42L);
            Invaders.seed(random, params);
            // Verify
            verify(random).setSeed(42L);
        }

        @Test
        public void nullSeedParamShouldBeIgnored() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Invaders.Params params = new Invaders.Params(Invaders.Params.Format.Image, 0, 0, 0, 0, null);
            Invaders.seed(random, params);
            // Verify
            verify(random, never()).setSeed(anyLong());
        }
    }

    private static double getRandomDoubleToGenerate(long desiredValue, long maxValue ) {
        return (((double) desiredValue) - 1) / maxValue;
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