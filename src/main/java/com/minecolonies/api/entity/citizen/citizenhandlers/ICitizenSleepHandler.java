package com.minecolonies.api.entity.citizen.citizenhandlers;

// [1.7.10] int[] -> int x,y,z

public interface ICitizenSleepHandler
{
    /**
     * Is the citizen a sleep?
     *
     * @return true when a sleep.
     */
    boolean isAsleep();

    /**
     * Attempts a sleep interaction with the citizen and the given bed.
     *
     * @param bedLocation The possible location to sleep.
     * @return if successful.
     */
    boolean trySleep(int[] bedLocation);

    /**
     * Called when the citizen wakes up.
     */
    void onWakeUp();

    /**
     * Get the bed location of the citizen.
     *
     * @return the bed location.
     */
    int[] getBedLocation();

    /**
     * Whether we should start to go sleeping
     *
     * @return true if should sleep
     */
    boolean shouldGoSleep();
}


