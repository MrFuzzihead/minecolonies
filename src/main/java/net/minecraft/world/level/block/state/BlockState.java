package net.minecraft.world.level.block.state;

/** [1.7.10 stub] BlockState is represented as int metadata in 1.7.10, but used as class in some files */
public class BlockState
{
    private final net.minecraft.block.Block block;
    private final int meta;

    public BlockState(net.minecraft.block.Block block, int meta)
    {
        this.block = block;
        this.meta = meta;
    }

    public net.minecraft.block.Block getBlock() { return block; }
    public int getMeta() { return meta; }

    public boolean isAir() { return block == net.minecraft.init.Blocks.air; }

    public <T extends Comparable<T>> BlockState setValue(Object property, T value) { return this; }
    public <T extends Comparable<T>> T getValue(Object property) { return null; }
    public boolean is(net.minecraft.block.Block b) { return block == b; }
}

