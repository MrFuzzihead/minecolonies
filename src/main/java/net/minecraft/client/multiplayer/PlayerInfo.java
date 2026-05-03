package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;

/**
 * [1.7.10] Compatibility stub for 1.21 PlayerInfo (player connection info in multiplayer).
 */
public class PlayerInfo
{
    private final GameProfile profile;

    public PlayerInfo(GameProfile profile)
    {
        this.profile = profile;
    }

    public GameProfile getProfile() { return profile; }
    public String getName() { return profile != null ? profile.getName() : ""; }
    public java.util.UUID getId() { return profile != null ? profile.getId() : null; }
}

