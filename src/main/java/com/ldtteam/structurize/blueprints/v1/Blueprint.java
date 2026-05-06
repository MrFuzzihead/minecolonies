package com.ldtteam.structurize.blueprints.v1;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.block.state.BlockState;
import java.nio.file.Path;
import java.util.Map;

/**
 * [1.7.10 stub] Blueprint - provides both 1.7.10-style and 1.21-style API
 * to allow compilation of mixed-version backport code.
 */
public class Blueprint {

    /** Get the primary block offset (anchor position) */
    public int[] getPrimaryBlockOffset() { return new int[]{0, 0, 0}; }

    /** 1.7.10-style: get Block at position */
    public Block getBlockAt(int[] pos) { return null; }

    /** 1.7.10-style: get metadata at position */
    public int getMetaAt(int[] pos) { return 0; }

    /** 1.21-style: get BlockState at position */
    public BlockState getBlockState(int[] pos) { return null; }

    /** Get tile entity NBT data at world position */
    public NBTTagCompound getTileEntityData(int[] worldPos, int[] localPos) { return null; }

    /** Get the structure pack name */
    public String getPackName() { return ""; }

    /** Get the file path */
    public Path getFilePath() { return null; }

    /** Get the file name */
    public String getFileName() { return ""; }

    /** Get block info as map */
    public Map<int[], Object> getBlockInfoAsMap() { return java.util.Collections.emptyMap(); }

    /** Subtract offset from position */
    public int[] subtract(int[] other) { return new int[]{0, 0, 0}; }

    public String toString() { return "Blueprint"; }
}

