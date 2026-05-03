package com.minecolonies.core.colony.interactionhandling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.interactionhandling.IChatPriority;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static com.minecolonies.api.colony.interactionhandling.ModInteractionResponseHandlers.SIMPLE_NOTIFICATION;

/**
 * A simple interaction which displays until an acceptable response is clicked
 */
public class SimpleNotificationInteraction extends StandardInteraction
{
    /**
     * Whether this interaction is active
     */
    private boolean active = true;

    public SimpleNotificationInteraction(
      final String inquiry,
      final IChatPriority priority)
    {
        super(inquiry, null, priority);
    }

    @Override
    public void onServerResponseTriggered(final int responseId, final Player player, final ICitizenData data)
    {
        super.onServerResponseTriggered(responseId, player, data);
        onResponse(responseId);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean onClientResponseTriggered(final int responseId, final Player player, final ICitizenDataView data, final Object /* BOWindow: todo ModularUI2 */ window)
    {
        onResponse(responseId);
        return super.onClientResponseTriggered(responseId, player, data, window);
    }

    /**
     * Removes the interaction after a response
     *
     * @param responseId response
     */
    private void onResponse(final int responseId)
    {
        final String response = getPossibleResponses().get(responseId);
        if (response.getContents() instanceof TranslatableContents)
        {
            if (((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_OKAY)
                  || ((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_IGNORE))
            {
                active = false;
            }
        }
    }

    @Override
    public String getType()
    {
        return SIMPLE_NOTIFICATION.getPath();
    }

    @Override
    public boolean isValid(final ICitizenData citizen)
    {
        return active;
    }
}



