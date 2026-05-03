package com.minecolonies.api.entity.citizen.citizenhandlers;

public interface ICitizenExperienceHandler
{
    /**
     * Updates the World of the citizen.
     */
    void updateLevel();

    /**
     * Add experience points to citizen. Increases the citizen World if he has sufficient experience. This will reset the experience.
     *
     * @param xp the amount of points added.
     */
    void addExperience(double xp);

    /**
     * Drop some experience share depending on the experience and experienceLevel.
     */
    void dropExperience();

    /**
     * Collect exp orbs around the entity.
     */
    void gatherXp();
}

