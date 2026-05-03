package com.minecolonies.api.eventbus.events.colony.citizens;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.eventbus.events.colony.AbstractColonyModEvent;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Event for when a citizen was removed from the colony.
 */
public final class CitizenRemovedModEvent extends AbstractColonyModEvent
{
    /**
     * The id of the citizen.
     */
    private final int citizenId;

    // [1.7.10] Entity.RemovalReason does not exist; replaced with String reason
    @NotNull
    private final String reason;

    /**
     * Citizen removed event.
     *
     * @param colony    the colony related to the event.
     * @param citizenId the id of the citizen.
     * @param reason    the reason the citizen was removed.
     */
    public CitizenRemovedModEvent(final @NotNull IColony colony, final int citizenId, final @NotNull String reason)
    {
        super(colony);
        this.citizenId = citizenId;
        this.reason = reason;
    }

    public int getCitizenId() { return citizenId; }

    @NotNull
    public String getRemovalReason() { return reason; }
}

