package com.minecolonies.core.commands.colonycommands;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.commands.arguments.ColonyIdArgument;
import com.minecolonies.core.commands.commandTypes.IMCColonyOfficerCommand;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.server.World.Ticket;
import net.minecraft.server.World.TicketType;
import net.minecraft.util.SortedArraySet;

import java.util.HashSet;
import java.util.Set;

import static com.minecolonies.core.commands.CommandArgumentNames.COLONYID_ARG;
import static com.minecolonies.core.commands.colonycommands.CommandColonyInfo.ID_TEXT;
import static com.minecolonies.core.commands.colonycommands.CommandColonyInfo.NAME_TEXT;

public class CommandColonyChunks implements IMCColonyOfficerCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final IColony colony = ColonyIdArgument.getColony(context, COLONYID_ARG);

        Set<TicketType> types = new HashSet<>();

        for (final Long chunkLong : colony.getLoadedChunks())
        {
            final SortedArraySet<Ticket<?>> tickets = context.getSource().getLevel().getChunkSource().chunkMap.getDistanceManager().tickets.get((long) chunkLong);
            if (tickets != null)
            {
                for (final Ticket<?> ticket : tickets)
                {
                    types.add(ticket.getType());
                }
            }
        }

        StringBuilder ticketString = new StringBuilder();
        for (final TicketType type : types)
        {
            ticketString.append("[").append(type).append("]");
        }

        context.getSource()
          .sendSuccess(() -> String.literal(ID_TEXT)
            .append(String.literal("" + colony.getID()).withStyle(ChatFormatting.YELLOW))
            .append(String.literal(" " + NAME_TEXT))
            .append(String.literal("" + colony.getName()).withStyle(ChatFormatting.YELLOW)), true);
        context.getSource().sendSuccess(() -> String.literal("Loaded chunks:").append(String.literal(" " + colony.getLoadedChunkCount()).withStyle(ChatFormatting.YELLOW)), true);
        context.getSource().sendSuccess(() -> String.translatable("Ticket types: ").append(String.literal(ticketString.toString()).withStyle(ChatFormatting.YELLOW)), true);

        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "chunkstatus";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName())
          .then(IMCCommand.newArgument(COLONYID_ARG, ColonyIdArgument.id()).executes(this::checkPreConditionAndExecute));
    }
}


