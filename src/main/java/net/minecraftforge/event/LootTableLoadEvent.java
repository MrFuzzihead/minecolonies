package net.minecraftforge.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable; // [1.7.10 bridge]

/** [1.7.10 bridge] LootTableLoadEvent - no 1.7.10 equivalent (loot tables added in 1.9) */
public class LootTableLoadEvent
{
    private final ResourceLocation name;
    private LootTable table;

    public LootTableLoadEvent(ResourceLocation name) { this.name = name; }
    public ResourceLocation getName() { return name; }
    public LootTable getTable() { return table; }
    public void setTable(LootTable t) { this.table = t; }
}


