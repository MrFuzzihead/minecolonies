package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** [1.7.10 bridge] EntityJoinLevelEvent - maps to EntityJoinWorldEvent */
public class EntityJoinLevelEvent
{
    private final Entity entity;
    private final World world;
    private boolean canceled = false;

    public EntityJoinLevelEvent(Entity e, World w) { this.entity = e; this.world = w; }
    public Entity getEntity() { return entity; }
    public World getLevel() { return world; }
    public boolean isCanceled() { return canceled; }
    public void setCanceled(boolean v) { this.canceled = v; }
}

