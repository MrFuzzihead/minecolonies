package com.minecolonies.core.commands.colonycommands;
import net.minecraft.network.chat.Style;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.commands.arguments.ColonyIdArgument;
import com.minecolonies.core.commands.commandTypes.IMCColonyOfficerCommand;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.commands.CommandSourceStack;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText

import static com.minecolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_DISABLED_IN_CONFIG;
import static com.minecolonies.core.commands.CommandArgumentNames.COLONYID_ARG;

public class CommandColonyInfo implements IMCColonyOfficerCommand
{
    public static final  String ID_TEXT           = "ID: ";
    public static final  String NAME_TEXT         = "Name: ";
    public static final String MAYOR_TEXT = "Mayor: ";
    private static final String COORDINATES_TEXT  = "Coordinates: ";
    private static final String COORDINATES_XYZ   = "x=%s y=%s z=%s";
    private static final String CITIZENS          = "Citizens: ";
    private static final String LAST_CONTACT_TEXT = "Last contact with Owner or Officer: %d hours ago!";
    private static final String IS_DELETABLE      = "If true this colony cannot be deleted: ";
    private static final String CANNOT_BE_RAIDED  = "This colony is unable to be raided";

    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final IColony colony = ColonyIdArgument.getColony(context, COLONYID_ARG);

        if (!context.getSource().hasPermission(OP_PERM_LEVEL) && !MineColonies.getConfig().getServer().canPlayerUseShowColonyInfoCommand.get())
        {
            context.getSource().sendSuccess(() -> String.translatable(COMMAND_DISABLED_IN_CONFIG), true);
            return 0;
        }

        final int[] position = colony.getCenter();
        context.getSource().sendSuccess(() -> String.literal(ID_TEXT + colony.getID() + " " + NAME_TEXT + colony.getName()), true);
        final String mayor = colony.getPermissions().getOwnerName();
        context.getSource().sendSuccess(() -> String.literal(MAYOR_TEXT + mayor), true);
        context.getSource()
          .sendSuccess(() -> String.literal(CITIZENS + colony.getCitizenManager().getCurrentCitizenCount() + "/" + colony.getCitizenManager().getMaxCitizens()), true);
        context.getSource()
          .sendSuccess(() -> String.literal(COORDINATES_TEXT + String.format(COORDINATES_XYZ, position.getX(), position.getY(), position.getZ())).setStyle(Style.EMPTY.withColor(
            ChatFormatting.GREEN)), true);
        context.getSource().sendSuccess(() -> String.literal(String.format(LAST_CONTACT_TEXT, colony.getLastContactInHours())), true);

        if (!colony.getRaiderManager().canHaveRaiderEvents())
        {
            context.getSource().sendSuccess(() -> String.literal(CANNOT_BE_RAIDED), true);
        }

        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "info";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName())
                 .then(IMCCommand.newArgument(COLONYID_ARG, ColonyIdArgument.id()).executes(this::checkPreConditionAndExecute));
    }
}



