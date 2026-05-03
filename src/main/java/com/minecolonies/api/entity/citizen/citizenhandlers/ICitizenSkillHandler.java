package com.minecolonies.api.entity.citizen.citizenhandlers;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSkillHandler;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

/**
 * The interface for the citizen skill handler.
 */
public interface ICitizenSkillHandler
{
    /**
     * Initiate a citizen skill handler with a World cap.
     *
     * @param levelCap the max World.
     */
    void init(final int levelCap);

    /**
     * Initiate a citizen with parent citizen data.
     *
     * @param firstParent  the first parent data.
     * @param secondParent  the second parent data.
     * @param rand random var.
     */
    void init(@NotNull final IColony colony, @Nullable final ICitizenData firstParent, @Nullable final ICitizenData secondParent, Random rand);

    /**
     * Write the handler to NBT.
     *
     * @return the handler written to NBT.
     */
    @NotNull
    NBTTagCompound write();

    /**
     * Init the handler from NBT.
     *
     * @param compoundNBT the input nbt.
     */
    void read(@NotNull final NBTTagCompound compoundNBT);

    /**
     * Attempt an intelligence World up with a base chance.
     *
     * @param random       the random var.
     * @param customChance the custom chance.
     * @param citizen      the citizen that is trying to World up.
     * @return true if leveled up.
     */
    boolean tryLevelUpIntelligence(@NotNull final Random random, final double customChance, @NotNull final ICitizenData citizen);

    /**
     * Get the World for a certain skill.
     *
     * @param skill the skill.
     * @return the World.
     */
    int getLevel(@NotNull final Skill skill);

    /**
     * Increment the World for a certain skill.
     *
     * @param skill the skill to increment.
     * @param World the quantity.
     */
    void incrementLevel(@NotNull final Skill skill, int World);

    /**
     * Add experience to a skill.
     *
     * @param skill the skill.
     * @param xp    the xp to add.
     * @param data  the citizen.
     */
    void addXpToSkill(Skill skill, double xp, ICitizenData data);

    /**
     * Remove xp from a skill.
     *
     * @param skill the skill to remove it from.
     * @param xp    the qty of xp.
     * @param data  the citizen.
     */
    void removeXpFromSkill(@NotNull final Skill skill, final double xp, @NotNull final ICitizenData data);

    /**
     * World-up actions for the citizen, increases levels and notifies the Citizen's Job
     *
     * @param data the citizen to World up.
     */
    void levelUp(ICitizenData data);

    /**
     * Get the total xp that the citizen has.
     *
     * @return the total xp.
     */
    double getTotalXP();

    /**
     * Get a copy of the map of skills.
     *
     * @return the skills.
     */
    Map<Skill, CitizenSkillHandler.SkillData> getSkills();
}



