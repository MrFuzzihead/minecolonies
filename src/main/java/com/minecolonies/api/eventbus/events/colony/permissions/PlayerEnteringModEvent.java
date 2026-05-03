package com.minecolonies.api.eventbus.events.colony.permissions;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.eventbus.events.colony.AbstractColonyModEvent;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * EntityPlayer entering colony mod event.
 */
public final class PlayerEnteringModEvent extends AbstractColonyModEvent
{
    /**
     * The EntityPlayer that is entering the colony.
     */
    private final EntityPlayer player;

    /**
     * Whether we should show the notification for the EntityPlayer entering.
     */
    private boolean shouldShowNotification = true;

    /**
     * Whether spectators also show up for the notification.
     */
    private boolean shouldShowForSpectators = false;

    /**
     * Constructs a colony-based event.
     *
     * @param colony the colony related to the event.
     * @param player the EntityPlayer that is entering the colony.
     */
    public PlayerEnteringModEvent(final @NotNull IColony colony, final EntityPlayer player)
    {
        super(colony);
        this.player = player;
    }

    /**
     * Get the EntityPlayer that is entering the colony.
     */
    public EntityPlayer getPlayer()
    {
        return player;
    }

    /**
     * Whether we should show the notification for the EntityPlayer entering.
     *
     * @return true if so.
     */
    public boolean shouldShowNotification()
    {
        return shouldShowNotification && (!player.capabilities.isFlying || shouldShowForSpectators);
    }

    /**
     * Disable sending out the notifications.
     */
    public void disableNotification()
    {
        shouldShowNotification = false;
    }

    /**
     * Disable the anti spectator check, allowing you to add custom logic under which to disable the notifications.
     */
    public void allowForSpectators()
    {
        this.shouldShowForSpectators = true;
    }
}

