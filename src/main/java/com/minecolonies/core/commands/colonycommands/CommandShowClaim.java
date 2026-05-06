package com.minecolonies.core.commands.colonycommands;
import net.minecraft.world.level.chunk.LevelChunk;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.minecolonies.core.commands.commandTypes.IMCOPCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.world.WorldServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.minecolonies.core.commands.CommandArgumentNames.POS_ARG;

public class CommandShowClaim implements IMCOPCommand
{
    /**
     * What happens when the command is executed after preConditions are successful.
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        final ServerLevel World = context.getSource().getLevel();

        // Colony
        int[] pos = new int[]{(int)context.getSource().getPosition().x, (int)context.getSource().getPosition().y, (int)context.getSource().getPosition().z};
        try
        {
            pos = BlockPosArgument.getBlockPos(context, POS_ARG);
        }
        catch (Exception e)
        {

        }

        final LevelChunk chunk = (LevelChunk) World.getChunk(pos);
        final int[] finalPos = pos;
        context.getSource().sendSuccess(() -> buildClaimCommandResult(chunk, finalPos, World), true);
        return 1;
    }

    /**
     * Creates the feedback text from the given cap
     *
     * @param chunk
     * @param pos
     * @param World
     * @return
     */
    private String buildClaimCommandResult(final LevelChunk chunk, final int[] pos, final ServerLevel World)
    {
        final String text = String.translatable("Claim data of chunk at: %sX %sZ\n", pos.getX(), pos.getZ()).withStyle(ChatFormatting.DARK_AQUA);

        final List<Integer> staticColonyClaims = ColonyUtils.getStaticClaims(chunk);
        final int owningColony = ColonyUtils.getOwningColony(chunk);
        if (!staticColonyClaims.isEmpty())
        {
            text.append(String.translatable("OwnerID:%s Direct colony claims:\n", owningColony).withStyle(ChatFormatting.GOLD));
            for (int colonyID : staticColonyClaims)
            {
                final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyID, World.dimension());
                if (colony == null)
                {
                    text.append(String.translatable("ID: %s Name: Unkown Colony\n", colonyID));
                }
                else
                {
                    text.append(String.translatable("ID: %s Name: %s\n", colonyID, colony.getName()));
                }
            }
        }

        final Map<Integer, Set<int[]>> buildingClaims = ColonyUtils.getAllClaimingBuildings(chunk);
        if (!buildingClaims.isEmpty())
        {
            text.append(String.translatable("Building claims:\n").withStyle(ChatFormatting.GOLD));
            for (Map.Entry<Integer, Set<int[]>> entry : buildingClaims.entrySet())
            {
                final IColony colony = IColonyManager.getInstance().getColonyByDimension(entry.getKey(), World.dimension());
                for (final int[] buildingPos : entry.getValue())
                {
                    if (colony != null)
                    {
                        final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingPos);
                        if (building != null)
                        {
                            text.append(String.translatable("ID: %s Building: %s Pos: %s\n",
                              entry.getKey(),
                              String.translatable(building.getBuildingDisplayName()),
                              buildingPos));
                        }
                        else
                        {
                            text.append(String.translatable("ID: %s Building: Unknown pos: %s\n", entry.getKey(), buildingPos));
                        }
                    }
                    else
                    {
                        text.append(String.translatable("ID: %s Building: Unknown Pos: %s\n", entry.getKey(), buildingPos));
                    }
                }
            }
        }
        return text;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "claiminfo";
    }

    public LiteralArgumentBuilder<CommandSourceStack> build()
    {
        return IMCCommand.newLiteral(getName())
          .then(IMCCommand.newArgument(POS_ARG, BlockPosArgument.blockPos()).executes(this::checkPreConditionAndExecute))
          .executes(this::checkPreConditionAndExecute);
    }
}



