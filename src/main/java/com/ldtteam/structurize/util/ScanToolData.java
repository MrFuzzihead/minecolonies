package com.ldtteam.structurize.util;

import java.util.Optional;

/**
 * [1.7.10 backport stub] ScanToolData with Slot inner class.
 * The backported structurize jar doesn't include Slot; this stub provides it for compilation.
 */
public class ScanToolData
{
    public static class Slot
    {
        public String getName() { return ""; }
        public Box getBox() { return new Box(); }
    }

    public static class Box
    {
        public Optional<int[]> getAnchor() { return Optional.empty(); }
        public int[] getPos1() { return new int[]{0, 0, 0}; }
        public int[] getPos2() { return new int[]{0, 0, 0}; }
    }
}

