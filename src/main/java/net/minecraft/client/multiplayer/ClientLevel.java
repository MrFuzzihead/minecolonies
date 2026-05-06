package net.minecraft.client.multiplayer;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.world.World;

/**
 * [1.7.10] Compatibility shim for 1.21 ClientLevel (client-side world).
 * In 1.7.10, this is net.minecraft.client.multiplayer.WorldClient.
 */
public class ClientLevel extends World
{
    public ClientLevel()
    {
        super(null, null, null, 0.0f, false);
    }
}

