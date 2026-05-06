package net.minecraftforge.event.world;

import net.minecraft.world.World;
import net.minecraft.block.Block;

/** [1.7.10 bridge] BlockEvent - maps to Forge 1.7.10 block events */
public class BlockEvent
{
    public final World world;
    public final int x, y, z;

    public BlockEvent(World w, int x, int y, int z) { this.world = w; this.x = x; this.y = y; this.z = z; }
    public World getLevel() { return world; }
    public int[] getPos() { return new int[]{x, y, z}; }
    public Object getState() { return null; }

    public static class BreakEvent extends BlockEvent
    {
        public final net.minecraft.entity.player.EntityPlayer player;
        public int expToDrop = 0;
        public BreakEvent(World w, int x, int y, int z, net.minecraft.entity.player.EntityPlayer p)
        {
            super(w, x, y, z);
            this.player = p;
        }
        public net.minecraft.entity.player.EntityPlayer getPlayer() { return player; }
        public int getExpToDrop() { return expToDrop; }
        public void setExpToDrop(int exp) { this.expToDrop = exp; }
    }

    public static class PlaceEvent extends BlockEvent
    {
        public final Block block;
        public final net.minecraft.entity.player.EntityPlayer entity;
        public final Object worldObj;
        public PlaceEvent(World w, int x, int y, int z, Block block, net.minecraft.entity.player.EntityPlayer player)
        {
            super(w, x, y, z);
            this.block = block;
            this.entity = player;
            this.worldObj = w;
        }
        public net.minecraft.entity.player.EntityPlayer getPlayer() { return entity; }
        public Block getBlock() { return block; }
        public boolean isCancelable() { return true; }
        public boolean isCanceled() { return false; }
        public void setCanceled(boolean c) {}
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
        public Object getPlacedBlock() { return null; }
    }

    public static class FarmlandTrampleEvent extends BlockEvent
    {
        public boolean canceled = false;
        public FarmlandTrampleEvent(World w, int x, int y, int z) { super(w, x, y, z); }
        public boolean isCanceled() { return canceled; }
        public void setCanceled(boolean v) { this.canceled = v; }
    }
}



