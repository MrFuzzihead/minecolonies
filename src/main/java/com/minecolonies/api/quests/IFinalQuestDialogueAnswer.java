package com.minecolonies.api.quests;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Terminal type answer. Will close the interaction.
 */
public interface IFinalQuestDialogueAnswer extends IQuestDialogueAnswer
{

    /**
     * Apply the objective to colony quest. This only applies to the terminal ones!
     * @param EntityPlayer the EntityPlayer triggering it.
     * @param quest the quest to apply itself to.
     */
    void applyToQuest(final EntityPlayer player, IQuestInstance quest);
}

