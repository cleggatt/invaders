package com.cleggatt.invaders;

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
import static com.cleggatt.invaders.Main.DEFAULT_X;
import static com.cleggatt.invaders.Main.DEFAULT_Y;

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
                    {new String[]{"--text"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, null)},
                    {new String[]{"--text", "--border", "8"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 8, null)},
                    {new String[]{"--text", "--height", "9"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 9, 0, null)},
                    {new String[]{"--text", "--scale", "10"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 10, 1, 1, 0, null)},
                    {new String[]{"--text", "--seed", "11"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, 11L)},
                    {new String[]{"--text", "--width", "12"}, new Params(Format.Text, DEFAULT_X, DEFAULT_Y, 1, 12, 1, 0, null)},
                    {new String[]{"--text", "-x", "13"}, new Params(Format.Text, 13, DEFAULT_Y, 1, 1, 1, 0, null)},
                    {new String[]{"--text", "-y", "14"}, new Params(Format.Text, DEFAULT_X, 14, 1, 1, 1, 0, null)},
                    {new String[]{"--png"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, null)},
                    {new String[]{"--png", "--border", "15"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 15, null)},
                    {new String[]{"--png", "--height", "16"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 16, 0, null)},
                    {new String[]{"--png", "--scale", "17"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 17, 1, 1, 0, null)},
                    {new String[]{"--png", "--seed", "18"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 1, 1, 0, 18L)},
                    {new String[]{"--png", "--width", "19"}, new Params(Format.Image, DEFAULT_X, DEFAULT_Y, 1, 19, 1, 0, null)},
                    {new String[]{"--png", "-x", "20"}, new Params(Format.Image, 20, DEFAULT_Y, 1, 1, 1, 0, null)},
                    {new String[]{"--png", "-y", "21"}, new Params(Format.Image, DEFAULT_X, 21, 1, 1, 1, 0, null)},
            });
        }

        @Test
        public void testParseParams() {
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
                    {new String[]{"--border"}},
                    {new String[]{"--height"}},
                    {new String[]{"--scale"}},
                    {new String[]{"--seed"}},
                    {new String[]{"--width"}},
                    {new String[]{"-x"}},
                    {new String[]{"-y"}},
                    {new String[]{"--border", "11"}},
                    {new String[]{"--height", "12"}},
                    {new String[]{"--scale", "13"}},
                    {new String[]{"--seed", "14"}},
                    {new String[]{"--width", "15"}},
                    {new String[]{"-x", "16"}},
                    {new String[]{"-y", "17"}},
                    {new String[]{"--text", "--png"}},
                    {new String[]{"--text", "--border", "C"}},
                    {new String[]{"--text", "--height", "d"}},
                    {new String[]{"--text", "--scale", "E"}},
                    {new String[]{"--text", "--seed", "f"}},
                    {new String[]{"--text", "--width", "G"}},
                    {new String[]{"--text", "-x", "h"}},
                    {new String[]{"--text", "-y", "I"}},
                    {new String[]{"--png", "--border", "j"}},
                    {new String[]{"--png", "--height", "K"}},
                    {new String[]{"--png", "--scale", "l"}},
                    {new String[]{"--png", "--seed", "M"}},
                    {new String[]{"--png", "--width", "n"}},
                    {new String[]{"--png", "-x", "O"}},
                    {new String[]{"--png", "-y", "Ip"}},
            });
        }

        @Test
        public void testParseInvalidParams() {
            // Exercise
            final Params params = Main.parseParams(this.args);
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
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y,0, 0, 0, 0, 42L);
            Main.seed(random, params);
            // Verify
            verify(random).setSeed(42L);
        }

        @Test
        public void seedWithNullSeedParamShouldDoNothing() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Params params = new Params(Format.Image, DEFAULT_X, DEFAULT_Y,0, 0, 0, 0, null);
            Main.seed(random, params);
            // Verify
            verify(random, never()).setSeed(anyLong());
        }
    }
}