package com.minecolonies.api.quests;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Quest objective interface for deliveries.
 */
public interface IQuestDeliveryObjective extends IDialogueObjectiveTemplate
{
    /**
     * Check if the objective is ready to move on.
     * @param colonyQuest the objective belongs to.
     * @return true if so.
     */
    boolean hasItem(final EntityPlayer player, final IQuestInstance colonyQuest);

    /**
     * Attempt to resolve an objective.
     * @param colonyQuest the objective belongs to.
     * @return true if so.
     */
    boolean tryDiscountItem(final EntityPlayer player, final IQuestInstance colonyQuest);

    /**
     * Dialogue tree when the conditions are fulfilled.
     * @return the dialogue to play.
     */
    IDialogueObjectiveTemplate.DialogueElement getReadyDialogueTree();
}

