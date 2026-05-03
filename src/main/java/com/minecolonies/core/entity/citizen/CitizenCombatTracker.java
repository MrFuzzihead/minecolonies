package com.minecolonies.core.entity.citizen;

// [1.7.10] CombatTracker does not exist in 1.7.10; this class is stubbed
/**
 * [1.7.10] Stub for CitizenCombatTracker - CombatTracker does not exist in 1.7.10.
 */
public class CitizenCombatTracker
{
    private final EntityCitizen citizen;

    public CitizenCombatTracker(final EntityCitizen citizen)
    {
        this.citizen = citizen;
    }

    public String getDeathMessage()
    {
        return citizen.getCitizenData() != null ? citizen.getCitizenData().getName() + " died" : "A citizen died";
    }
}
