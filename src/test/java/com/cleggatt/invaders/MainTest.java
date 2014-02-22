package com.cleggatt.invaders;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.cleggatt.invaders.Main.Params;
import com.cleggatt.invaders.Main.Params.Format;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static com.cleggatt.invaders.Main.*;

@RunWith(Enclosed.class)
public class MainTest {

    @RunWith(Parameterized.class)
    public static class ParseParamsTest {

        private final String[] args;
        private final Params expected;

        public ParseParamsTest(String[] args, Params expected) {
            this.args = args;
            this.expected = expected;
        }

        @Parameterized.Parameters
        public static Collection params() {
            return Arrays.asList(new Object[][]{
                    {new String[]{"--text"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},

                    {new String[]{"--text", "--border", "0"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, 0, 0, null, 0, null)},
                    {new String[]{"--text", "--border", "8"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 8, 0, 0, null, 0, null)},

                    {new String[]{"--text", "-tileY", "1"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},
                    {new String[]{"--text", "-tileY", "9"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 9, 1, 0, 0, null, 0, null)},

                    {new String[]{"--text", "--scale", "1"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},
                    {new String[]{"--text", "--scale", "10"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 10, 1, 1, 1, 0, 0, null, 0, null)},

                    {new String[]{"--text", "--seed", "11"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, 11L, 0, null)},

                    {new String[]{"--text", "-tileX", "1"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},
                    {new String[]{"--text", "-tileX", "12"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 12, 1, 1, 0, 0, null, 0, null)},

                    {new String[]{"--text", "-x", "1"}, new Params(Format.Text, 1, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},
                    {new String[]{"--text", "-x", "2"}, new Params(Format.Text, 2, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, null)},

                    {new String[]{"--text", "-y", "1"}, new Params(Format.Text, DEFAULT_X, 1, 1, 1, 1, 1, 0, 0, null, 0, null)},
                    {new String[]{"--text", "-y", "3"}, new Params(Format.Text, DEFAULT_X, 3, 1, 1, 1, 1, 0, 0, null, 0, null)},

                    {new String[]{"--png"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--guassian", "0"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 0, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--guassian", "16"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, 16, DEFAULT_OUTPUT_STR)},

                    {new String[]{"--png", "--border", "15"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 15, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "-tileY", "17"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 17, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--scale", "18"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 18, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--seed", "19"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, 19L, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--scale", "18"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 18, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "-tileX", "20"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 20, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "-x", "4"}, new Params(Format.Image, 4, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "-y", "5"}, new Params(Format.Image, DEFAULT_X, 5, 1, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, DEFAULT_OUTPUT_STR)},
                    {new String[]{"--png", "--output", "file.out"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 1, 0, 0, null, DEFAULT_BLUR, "file.out")},
            });
        }

        @Test
        public void testParseParams() throws ParseException {
            // Exercise
            final Params params = Main.parseParams(this.args);
            // Verify
            assertNotNull(params);
            assertEquals(expected.getFormat(), params.getFormat());
            assertEquals(expected.getX(), params.getX());
            assertEquals(expected.getY(), params.getY());
            assertEquals(expected.getScale(), params.getScale());
            assertEquals(expected.getTileX(), params.getTileX());
            assertEquals(expected.getTileY(), params.getTileY());
            assertEquals(expected.getBorder(), params.getBorder());
            assertEquals(expected.getSeed(), params.getSeed());
            assertEquals(expected.getBlurRadius(), params.getBlurRadius());
            assertEquals(expected.getOutputFile(), params.getOutputFile());
        }
    }

    @RunWith(Parameterized.class)
    public static class ParseInvalidParamsTest {

        private final String[] args;

        public ParseInvalidParamsTest(String[] args) {
            this.args = args;
        }

        @Parameterized.Parameters
        public static Collection params() {
            return Arrays.asList(new Object[][]{
                    // Invalid options without output specified
                    {new String[]{}},
                    {new String[]{"-x"}},
                    {new String[]{"--border"}},
                    {new String[]{"--guassian"}},
                    {new String[]{"-pxWidth"}},
                    {new String[]{"-pxHeight"}},
                    {new String[]{"--scale"}},
                    {new String[]{"--seed"}},
                    {new String[]{"-tileX"}},
                    {new String[]{"-tileY"}},
                    {new String[]{"-x"}},
                    {new String[]{"-y"}},
                    {new String[]{"--border", "11"}},
                    {new String[]{"--guassian", "12"}},
                    {new String[]{"-pxWidth", "13"}},
                    {new String[]{"-pxHeight", "14"}},
                    {new String[]{"--scale", "15"}},
                    {new String[]{"--seed", "16"}},
                    {new String[]{"-tileX", "17"}},
                    {new String[]{"-tileY", "18"}},
                    {new String[]{"-x", "19"}},
                    {new String[]{"-y", "20"}},
                    //  Invalid arguments type
                    {new String[]{"--text", "--border", "A"}},
                    {new String[]{"--text", "-pxWidth", "b"}},
                    {new String[]{"--text", "-pxHeight", "C"}},
                    {new String[]{"--text", "--scale", "d"}},
                    {new String[]{"--text", "--seed", "E"}},
                    {new String[]{"--text", "-tileX", "f"}},
                    {new String[]{"--text", "-tileY", "G"}},
                    {new String[]{"--text", "-x", "h"}},
                    {new String[]{"--text", "-y", "I"}},
                    {new String[]{"--png", "--border", "j"}},
                    {new String[]{"--png", "--guassian", "K"}},
                    {new String[]{"--png", "-pxWidth", "l"}},
                    {new String[]{"--png", "-pxHeight", "M"}},
                    {new String[]{"--png", "--scale", "n"}},
                    {new String[]{"--png", "--seed", "P"}},
                    {new String[]{"--png", "-tileX", "q"}},
                    {new String[]{"--png", "-tileY", "R"}},
                    {new String[]{"--png", "-x", "s"}},
                    {new String[]{"--png", "-y", "T"}},
                    // Invalid option combinations
                    {new String[]{"--text", "--png"}},
                    {new String[]{"--text", "--guassian", "1"}},
                    {new String[]{"--text", "--output", "file.out"}},
                    {new String[]{"--text", "-pxWidth", "100", "-pxHeight", "100", "-tileX", "10", "-tileY", "10", }},
                    // Invalid option arguments values - use PNG as all options are valid for it
                    {new String[]{"--png", "--border", "-1"}},
                    {new String[]{"--png", "--guassian", "-1"}},
                    {new String[]{"--png", "-tileY", "0"}},
                    {new String[]{"--png", "-tileY", "-1"}},
                    {new String[]{"--text", "-pxWidth", "0"}},
                    {new String[]{"--text", "-pxWidth", "-1"}},
                    {new String[]{"--text", "-pxHeight", "0"}},
                    {new String[]{"--text", "-pxHeight", "-1"}},
                    {new String[]{"--png", "--scale", "0"}},
                    {new String[]{"--png", "--scale", "-1"}},
                    {new String[]{"--png", "-tileX", "0"}},
                    {new String[]{"--png", "-tileX", "-1"}},
                    {new String[]{"--png", "-x", "0"}},
                    {new String[]{"--png", "-x", "-1"}},
                    {new String[]{"--png", "-y", "0"}},
                    {new String[]{"--png", "-y", "-1"}},
                    // Result in 0 tiles
                    {new String[]{"--png", "-pxWidth", "1", "-x", "1"}},
                    {new String[]{"--png", "-pxHeight", "1", "-y", "2"}},
            });
        }

        @Test(expected = ParseException.class)
        public void testParseInvalidParams() throws ParseException {
            // Exercise (and verify by exception)
            Main.parseParams(this.args);
        }
    }

    public static class InvaderDimensionTest {
        @Test(expected = ParseException.class)
        public void productOfXAndYGreaterThan63ShouldBeInvalid() throws ParseException {
            // Exercise (and verify by exception)
            Main.parseParams(new String[]{"--png", "-x", "2", "-y", "32"});
        }

        @Test
        public void productOfXAndYGreaterLessThan63ShouldBeValid() throws ParseException {
            // Exercise
            final Params params = Main.parseParams(new String[]{"--png", "-x", "2", "-y", "31"});
            // Verify
            assertNotNull(params);
        }
    }

    @RunWith(Parameterized.class)
    public static class TileCalculationTest {

        private final String[] args;
        private final int tileX;
        private final int tileY;
        private final int overallBorderX;
        private final int overallBorderY;

        public TileCalculationTest(String[] args, int tileX, int tileY, int overallBorderX, int overallBorderY) {
            this.args = args;
            this.tileX = tileX;
            this.tileY = tileY;
            this.overallBorderX = overallBorderX;
            this.overallBorderY = overallBorderY;
        }

        @Parameterized.Parameters
        public static Collection params() {
            return Arrays.asList(new Object[][]{
                    // Exact fit
                    {new String[]{"--text", "-x", "2", "-y", "3", "-b", "0", "-pxWidth", "20", "-pxHeight", "21"}, 5, 7, 20, 21},
                    // Inexact fit
                    {new String[]{"--text", "-x", "2", "-y", "3", "-b", "0", "-s", "2", "-pxWidth", "20", "-pxHeight", "21"}, 2, 3, 20, 21},
                    // With border
                    {new String[]{"--text", "-x", "2", "-y", "3", "-b", "1", "-pxWidth", "20", "-pxHeight", "21"}, 3, 4, 20, 21},
                    // Overall border exact: (2,2) (3,3)
                    {new String[]{"--text", "-x", "5", "-y", "10", "-b", "0", "-pxWidth", "14", "-pxHeight", "16"}, 1, 1, 14, 16},
                    // Overall border uneven width: (1,0) (0,0)
                    {new String[]{"--text", "-x", "5", "-y", "10", "-b", "0", "-pxWidth", "11", "-pxHeight", "10"}, 1, 1, 11, 10},
                    // Overall border uneven height:(0,0) (1,0)
                    {new String[]{"--text", "-x", "5", "-y", "10", "-b", "0", "-pxWidth", "10", "-pxHeight", "11"}, 1, 1, 10, 11},
            });
        }

        @Test
        public void testTileCalculation() throws ParseException {
            // Exercise
            final Params params = Main.parseParams(this.args);
            // Verify
            assertEquals(tileX, params.getTileX());
            assertEquals(tileY, params.getTileY());
            assertEquals(overallBorderX, params.getPxWidth());
            assertEquals(overallBorderY, params.getPxHeight());
        }
    }

    public static class HelpTextTest {
        @Test
        public void helpOptionShouldReturnNull() throws ParseException {
            // Exercise
            final Params params = Main.parseParams(new String[]{"--help"});
            // Verify
            assertNull(params);
        }
    }

    public static class SeedTest {
        @Test
        public void seedWithNonNullSeedParamShouldSetParamAsSeed() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 0, 0, 0, 0, 0, 0, 42L, 0, null);
            Main.seed(random, params);
            // Verify
            verify(random).setSeed(42L);
        }

        @Test
        public void seedWithNullSeedParamShouldDoNothing() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y,0, 0, 0, 0, 0, 0, null, 0, null);
            Main.seed(random, params);
            // Verify
            verify(random, never()).setSeed(anyLong());
        }
    }
}