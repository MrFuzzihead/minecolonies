package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/** [1.7.10 bridge] PlayerInteractEvent - maps to Forge 1.7.10 PlayerInteractEvent */
public class PlayerInteractEvent
{
    public final EntityPlayer entityPlayer;
    public final World world;
    public final int x, y, z;

    public PlayerInteractEvent(EntityPlayer p, World w, int x, int y, int z)
    {
        this.entityPlayer = p;
        this.world = w;
        this.x = x; this.y = y; this.z = z;
    }
    public EntityPlayer getEntity() { return entityPlayer; }
    public World getLevel() { return world; }

    public static class RightClickBlock extends PlayerInteractEvent
    {
        public RightClickBlock(EntityPlayer p, World w, int x, int y, int z) { super(p, w, x, y, z); }
    }

    public static class LeftClickBlock extends PlayerInteractEvent
    {
        public LeftClickBlock(EntityPlayer p, World w, int x, int y, int z) { super(p, w, x, y, z); }
    }
}

