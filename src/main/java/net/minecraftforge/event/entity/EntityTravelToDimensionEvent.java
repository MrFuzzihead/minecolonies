package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;

/** [1.7.10 bridge] EntityTravelToDimensionEvent */
public class EntityTravelToDimensionEvent
{
    private final Entity entity;
    private final int dimension;
    private boolean canceled = false;

    public EntityTravelToDimensionEvent(Entity e, int dim) { this.entity = e; this.dimension = dim; }
    public Entity getEntity() { return entity; }
    public int getDimension() { return dimension; }
    public boolean isCanceled() { return canceled; }
    public void setCanceled(boolean v) { this.canceled = v; }
}

