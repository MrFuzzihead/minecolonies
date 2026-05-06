package net.minecraft.world.level.storage.loot;

import com.google.gson.GsonBuilder;

/** [1.7.10 bridge] Deserializers - no 1.7.10 equivalent */
public class Deserializers
{
    public static GsonBuilder createLootTableSerializer()
    {
        return new GsonBuilder();
    }
}

