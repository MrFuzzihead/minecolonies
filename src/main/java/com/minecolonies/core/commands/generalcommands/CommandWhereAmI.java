package com.minecolonies.core.commands.generalcommands;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] int[] -> int x,y,z

public class CommandWhereAmI implements IMCCommand
{
    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final Entity sender = context.getSource().getEntity();

        final int[] playerPos = sender.blockPosition();
        final IColony colony = IColonyManager.getInstance().getClosestColony(sender.getCommandSenderWorld(), playerPos);

        if (colony == null)
        {
            MessageUtils.format(CommandTranslationConstants.COMMAND_WHERE_AM_I_NO_COLONY).sendTo((Player) sender);
            return 0;
        }
        final int[] center = colony.getCenter();
        final double distance = BlockPosUtil.getDistance2D(center, new int[]{(int)playerPos.posX, center[1], (int)playerPos.posZ});

        if (!IColonyManager.getInstance().isCoordinateInAnyColony(sender.getCommandSenderWorld(), playerPos))
        {
            MessageUtils.format(CommandTranslationConstants.COMMAND_WHERE_AM_I_COLONY_CLOSE, distance).sendTo((Player) sender);
            return 0;
        }

        final String colonyName = colony.getName();
        final String id = Integer.toString(colony.getID());

        MessageUtils.format(CommandTranslationConstants.COMMAND_WHERE_AM_I_IN_COLONY, colonyName, id, distance).sendTo((Player) sender);

        return 0;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "whereami";
    }
}



