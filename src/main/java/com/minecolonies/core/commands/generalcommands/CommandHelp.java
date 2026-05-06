package com.minecolonies.core.commands.generalcommands;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraftforge.common.ForgeHooks;

import static com.minecolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_HELP_INFO_DISCORD;
import static com.minecolonies.api.util.constant.translation.CommandTranslationConstants.COMMAND_HELP_INFO_WIKI;

public class CommandHelp implements IMCCommand
{

    private static final String wikiUrl    = "https://wiki.minecolonies.ldtteam.com";
    private static final String discordUrl = "https://discord.minecolonies.com";

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final Entity sender = context.getSource().getEntity();
        if (!(sender instanceof Player))
        {
            return 0;
        }

        context.getSource().sendSuccess(() -> String.translatable(COMMAND_HELP_INFO_WIKI), true);
        context.getSource().sendSuccess(() -> ((String) ForgeHooks.newChatWithLinks(wikiUrl)).append(String.literal("\n")), true);
        context.getSource().sendSuccess(() -> String.translatable(COMMAND_HELP_INFO_DISCORD), true);
        context.getSource().sendSuccess(() -> ForgeHooks.newChatWithLinks(discordUrl), true);

        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "help";
    }
}



