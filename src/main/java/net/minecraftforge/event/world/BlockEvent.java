package net.minecraftforge.event.world;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraft.block.Block;

/** [1.7.10 bridge] BlockEvent stub — shadows the real Forge class at compile time */
public class BlockEvent extends Event
{
    public final World world;
    public final int x, y, z;
    public final Block block;
    public final int blockMetadata;

    public BlockEvent(World w, int x, int y, int z)
    {
        this.world = w; this.x = x; this.y = y; this.z = z;
        this.block = null; this.blockMetadata = 0;
    }

    public BlockEvent(World w, int x, int y, int z, Block block, int meta)
    {
        this.world = w; this.x = x; this.y = y; this.z = z;
        this.block = block; this.blockMetadata = meta;
    }

    @Cancelable
    public static class BreakEvent extends BlockEvent
    {
        private final net.minecraft.entity.player.EntityPlayer player;
        public int expToDrop = 0;

        public BreakEvent(World w, int x, int y, int z, Block block, int meta, net.minecraft.entity.player.EntityPlayer p)
        {
            super(w, x, y, z, block, meta);
            this.player = p;
        }

        public net.minecraft.entity.player.EntityPlayer getPlayer() { return player; }
        public int getExpToDrop() { return expToDrop; }
        public void setExpToDrop(int exp) { this.expToDrop = exp; }
    }

    @Cancelable
    public static class PlaceEvent extends BlockEvent
    {
        /** The block being placed (same as inherited {@code block}). */
        public final Block placedBlock;
        /** The player placing the block. */
        public final net.minecraft.entity.player.EntityPlayer player;
        /** Alias kept for legacy call-sites that use {@code event.entity}. */
        public final net.minecraft.entity.player.EntityPlayer entity;

        public PlaceEvent(World w, int x, int y, int z, Block block, net.minecraft.entity.player.EntityPlayer p)
        {
            super(w, x, y, z, block, 0);
            this.placedBlock = block;
            this.player = p;
            this.entity = p;
        }

        public net.minecraft.entity.player.EntityPlayer getPlayer() { return player; }
        public Block getBlock() { return block; }
    }

    public static class EntityPlaceEvent extends BlockEvent
    {
        private final net.minecraft.entity.Entity entity;
        public EntityPlaceEvent(World w, int x, int y, int z, net.minecraft.entity.Entity entity)
        {
            super(w, x, y, z);
            this.entity = entity;
        }
        public net.minecraft.entity.Entity getEntity() { return entity; }
    }

    public static class FarmlandTrampleEvent extends BlockEvent
    {
        public FarmlandTrampleEvent(World w, int x, int y, int z) { super(w, x, y, z); }
    }
}
