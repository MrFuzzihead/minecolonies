package com.minecolonies.api.quests;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Quest reward interface for all reward types.
 */
public interface IQuestRewardTemplate
{
    /**
     * Apply the reward to colony and player.
     * @param colony the involved colony.
     * @param EntityPlayer the involved player.
     * @param colonyQuest the related quest.
     */
    void applyReward(final IColony colony, final EntityPlayer player, final IQuestInstance colonyQuest);
}

