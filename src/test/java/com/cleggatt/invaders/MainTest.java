package com.cleggatt.invaders;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class MainTest {

    public static class ParseParamsTest {
        @Test
        public void textParamShouldReturnSingleText() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--text"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Text, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void textParamWithScaleShouldReturnScaledText() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--text", "--scale", "3"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Text, params.getFormat());
            assertEquals(3, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void textWithTileParamShouldReturnTiledImage() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--text", "--width",  "4", "--height", "5", "--border", "2"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Text, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(4, params.getNumWide());
            assertEquals(5, params.getNumHigh());
            assertEquals(2, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngParamShouldReturnSingleImage() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--png"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Image, params.getFormat());
            assertEquals(1,params.getScale(), 1);
            assertEquals(1,params.getNumWide(), 1);
            assertEquals(1,params.getNumHigh(), 1);
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngParamShouldReturnScaledImage() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--png", "--scale", "3"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Image, params.getFormat());
            assertEquals(3, params.getScale());
            assertEquals(1, params.getNumWide());
            assertEquals(1, params.getNumHigh());
            assertEquals(0, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void pngWithTileParamShouldReturnTiledImage() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--png", "--width",  "4", "--height", "5", "--border", "2"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Image, params.getFormat());
            assertEquals(1, params.getScale());
            assertEquals(4, params.getNumWide());
            assertEquals(5, params.getNumHigh());
            assertEquals(2, params.getBorder());
            assertEquals(null, params.getSeed());
        }

        @Test
        public void seedShouldBeParsed() {
            // Exercise
            final Main.Params params = Main.parseParams(new String[]{"--png", "--seed", "42"});
            // Verify
            assertNotNull(params);
            assertEquals(Main.Params.Format.Image, params.getFormat());
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
            final Main.Params params = Main.parseParams(this.args);
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
            Main.Params params = new Main.Params(Main.Params.Format.Image, 0, 0, 0, 0, 42L);
            Main.seed(random, params);
            // Verify
            verify(random).setSeed(42L);
        }

        @Test
        public void seedWithNullSeedParamShouldDoNothing() {
            // Set up
            Random random = mock(Random.class);
            // Exercise
            Main.Params params = new Main.Params(Main.Params.Format.Image, 0, 0, 0, 0, null);
            Main.seed(random, params);
            // Verify
            verify(random, never()).setSeed(anyLong());
        }
    }
}