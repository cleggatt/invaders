package com.cleggatt;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;

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
        public void testInvaderGeneration() {
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
        public void testInvaderGeneration() {
            // Set up
            final Invaders invaders = new Invaders(width, height);
            // Exercise
            String textInvader = invaders.getTextInvader(invader);
            // Verify
            assertEquals(expectedTextInvader, textInvader);
        }
    }
}