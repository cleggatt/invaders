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
        public static Collection primeNumbers() {
            return Arrays.asList(new Object[][]{
                    {new String[]{"--text"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, null, 0)},
                    {new String[]{"--text", "--border", "8"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 8, null, 0)},
                    {new String[]{"--text", "--height", "9"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 9, 0, null, 0)},
                    {new String[]{"--text", "--scale", "10"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 10, 1, 1, 0, null, 0)},
                    {new String[]{"--text", "--seed", "11"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, 11L, 0)},
                    {new String[]{"--text", "--width", "12"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 12, 1, 0, null, 0)},
                    {new String[]{"--text", "-x", "2"}, new Params(Format.Text, 2, DEFAULT_Y, 1, 1, 1, 0, null, 0)},
                    {new String[]{"--text", "-y", "3"}, new Params(Format.Text, DEFAULT_X, 3, 1, 1, 1, 0, null, 0)},
                    {new String[]{"--png"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "--border", "15"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 15, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "--guassian", "16"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, null, 16)},
                    {new String[]{"--png", "--height", "17"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 17, 0, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "--scale", "18"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 18, 1, 1, 0, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "--seed", "19"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, 19L, DEFAULT_BLUR)},
                    {new String[]{"--png", "--width", "20"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 20, 1, 0, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "-x", "4"}, new Params(Format.Image, 4, DEFAULT_Y, 1, 1, 1, 0, null, DEFAULT_BLUR)},
                    {new String[]{"--png", "-y", "5"}, new Params(Format.Image, DEFAULT_X, 5, 1, 1, 1, 0, null, DEFAULT_BLUR)},
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
            assertEquals(expected.getNumWide(), params.getNumWide());
            assertEquals(expected.getNumHigh(), params.getNumHigh());
            assertEquals(expected.getBorder(), params.getBorder());
            assertEquals(expected.getSeed(), params.getSeed());
            assertEquals(expected.getBlurRadius(), params.getBlurRadius());
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
                    {new String[]{}},
                    {new String[]{"-x"}},
                    {new String[]{"--border"}},
                    {new String[]{"--guassian"}},
                    {new String[]{"--height"}},
                    {new String[]{"--scale"}},
                    {new String[]{"--seed"}},
                    {new String[]{"--width"}},
                    {new String[]{"-x"}},
                    {new String[]{"-y"}},
                    {new String[]{"--border", "11"}},
                    {new String[]{"--guassian", "12"}},
                    {new String[]{"--height", "13"}},
                    {new String[]{"--scale", "14"}},
                    {new String[]{"--seed", "15"}},
                    {new String[]{"--width", "16"}},
                    {new String[]{"-x", "17"}},
                    {new String[]{"-y", "18"}},
                    {new String[]{"--text", "--png"}},
                    {new String[]{"--text", "--guassian", "1"}},
                    {new String[]{"--text", "--border", "a"}},
                    {new String[]{"--text", "--height", "B"}},
                    {new String[]{"--text", "--scale", "c"}},
                    {new String[]{"--text", "--seed", "D"}},
                    {new String[]{"--text", "--width", "e"}},
                    {new String[]{"--text", "-x", "f"}},
                    {new String[]{"--text", "-y", "G"}},
                    {new String[]{"--png", "--border", "h"}},
                    {new String[]{"--png", "--guassian", "I"}},
                    {new String[]{"--png", "--height", "j"}},
                    {new String[]{"--png", "--scale", "K"}},
                    {new String[]{"--png", "--seed", "l"}},
                    {new String[]{"--png", "--width", "M"}},
                    {new String[]{"--png", "-x", "n"}},
                    {new String[]{"--png", "-y", "P"}},
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
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 0, 0, 0, 0, 42L, 0);
            Main.seed(random, params);
            // Verify
            verify(random).setSeed(42L);
        }

        @Test
        public void seedWithNullSeedParamShouldDoNothing() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y,0, 0, 0, 0, null, 0);
            Main.seed(random, params);
            // Verify
            verify(random, never()).setSeed(anyLong());
        }
    }
}