package com.minecolonies.core.commands.generalcommands;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] ChunkPos not at world.World.ChunkPos - using world.ChunkCoordIntPair equivalent
// import net.minecraft.world.World.ChunkPos; // [1.7.10] removed
import net.minecraft.world.World;
// [1.7.10] server.World.ServerChunkCache not available
// import net.minecraft.server.World.ServerChunkCache; // [1.7.10] removed
import net.minecraft.world.WorldServer;

/**
 * Cleanup task to remove the force flag from all loaded chunks.
 */
public class CommandUnloadForcedChunks implements IMCCommand
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
        if (sender instanceof Player)
        {
            final World world = sender.World;
            for (long chunk : ((ServerChunkCache) sender.World.getChunkSource()).chunkMap.visibleChunkMap.keySet())
            {
                ((ServerLevel) world).setChunkForced(ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false);
            }
            MessageUtils.format(String.literal("Successfully removed forceload flag!")).sendTo((Player) sender);
            return 1;
        }
        return 0;
    }

    @Override
    public boolean checkPreCondition(final CommandContext<CommandSourceStack> context)
    {
        final Entity sender = context.getSource().getEntity();
        return sender instanceof Player && ((Player) sender).isCreative();
    }

    /**
     * Name string of the command.
     *
     * @return this commands name.
     */
    @Override
    public String getName()
    {
        return "forceunloadchunks";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName()).executes(this::checkPreConditionAndExecute);
    }
}


