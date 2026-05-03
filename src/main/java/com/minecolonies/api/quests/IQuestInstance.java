package com.minecolonies.api.quests;

import com.minecolonies.api.colony.IColony;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] INBTSerializable -> manual read/write
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Quest instance
 */
public interface IQuestInstance
{
    // [1.7.10] INBTSerializable replaced by explicit methods
    void readFromNBT(NBTTagCompound compound);
    NBTTagCompound writeToNBT(NBTTagCompound compound);

    /**
     * Triggered when the quest is accepted
     *
     * @param EntityPlayer player accepting
     */
    void onStart(final EntityPlayer player, final IColony colony);

    /**
     * Get the id of the quest giver.
     * Server only!
     *
     * @return the id of the quest giver.
     */
    IQuestGiver getQuestGiver();

    /**
     * Id of questgiver.
     * @return the id.
     */
    int getQuestGiverId();

    /**
     * Check if the expiration date relative to the colony day count was reached.
     * @param colony the colony to check the validity for.
     * @return true if so.
     */
    boolean isValid(IColony colony);

    /**
     * Get the quest id of the quest.
     * @return the id.
     */
    ResourceLocation getId();

    /**
     * On deletion of the quest.
     */
    void onDeletion();

    /**
     * Advance the quest objective to this id.
     * @param EntityPlayer the EntityPlayer advancing this objective.
     * @param nextObjective the id to advance it to.
     * @return the next objective instance.
     */
    IObjectiveInstance advanceObjective(final EntityPlayer player, int nextObjective);

    /**
     * On question completion call.
     */
    void onCompletion();

    /**
     * Get the current objective index.
     * @return the index number.
     */
    int getObjectiveIndex();

    /**
     * Get one of the other participants by index.
     * @param target the target participant id.
     * @return the quest participant.
     */
    IQuestParticipant getParticipant(int target);

    /**
     * Get the full list of quest participants.
     * @return the list of participants.
     */
    List<Integer> getParticipants();

    /**
     * Get the id of the current task holder in the quest.
     * @return the quest participant.
     */
    int getQuestTarget();

    /**
     * Get the objective data of the current objective.
     * @return the data.
     */
    @Nullable
    IObjectiveInstance getCurrentObjectiveInstance();

    /**
     * Get the colony matching the quest.
     * @return the colony.
     */
    IColony getColony();

    /**
     * Get the EntityPlayer UUID that accepted the quest.
     * @return the EntityPlayer uuid.
     */
    UUID getAssignedPlayer();

    /**
     * Simple advance objective by one.
     * @param EntityPlayer the EntityPlayer involved.
     */
    void advanceObjective(EntityPlayer player);

    /**
     * On world load trigger.
     */
    void onWorldLoad();
}
