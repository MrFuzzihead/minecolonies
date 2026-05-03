package com.minecolonies.core.commands.citizencommands;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.pathfinding.IMinecoloniesNavigator;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
import com.minecolonies.core.commands.arguments.ColonyIdArgument;
import com.minecolonies.core.commands.commandTypes.IMCColonyOfficerCommand;
import com.minecolonies.core.commands.commandTypes.IMCCommand;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

import static com.minecolonies.core.commands.CommandArgumentNames.CITIZENID_ARG;
import static com.minecolonies.core.commands.CommandArgumentNames.COLONYID_ARG;

/**
 * Displays information about a chosen citizen in a chosen colony.
 */
public class CommandCitizenInfo implements IMCColonyOfficerCommand
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
        final ICitizenData citizenData = colony.getCitizenManager().getCivilian(IntegerArgumentType.getInteger(context, CITIZENID_ARG));

        if (citizenData == null)
        {
            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_NOT_FOUND), false);
            return 0;
        }

        context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO, citizenData.getId(), citizenData.getName()), false);
        final Optional<AbstractEntityCitizen> optionalEntityCitizen = citizenData.getEntity();

        if (optionalEntityCitizen.isPresent())
        {
            final AbstractEntityCitizen entityCitizen = optionalEntityCitizen.get();

            final int[] citizenPosition = entityCitizen.blockPosition();
            context.getSource()
              .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_POSITION,
                citizenPosition.getX(),
                citizenPosition.getY(),
                  citizenPosition.getZ()).withStyle(styleWithTeleport(citizenPosition)), false);

            context.getSource()
                .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_HEALTH, entityCitizen.getHealth(), entityCitizen.getMaxHealth()), false);
        }
        else
        {
            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_POSITION,
              citizenData.getLastPosition().getX(),
              citizenData.getLastPosition().getY(),
                citizenData.getLastPosition().getZ()).withStyle(styleWithTeleport(citizenData.getLastPosition())), false);

            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_NOT_LOADED), false);
        }

        final int[] homePosition = citizenData.getHomePosition();
        context.getSource()
            .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_HOME_POSITION,
                homePosition.getX(),
                homePosition.getY(),
                homePosition.getZ()).withStyle(styleWithTeleport(homePosition)), false);

        if (citizenData.getWorkBuilding() == null)
        {
            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_NO_WORKING_POSITION), false);
        }
        else
        {
            final int[] workingPosition = citizenData.getWorkBuilding().getPosition();
            context.getSource()
              .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_WORKING_POSITION,
                workingPosition.getX(),
                workingPosition.getY(),
                  workingPosition.getZ()).withStyle(styleWithTeleport(workingPosition)), false);
        }

        if (citizenData.getJob() == null)
        {
            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_NO_JOB), false);
            context.getSource().sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_NO_ACTIVITY), false);
        }
        else if (citizenData.getWorkBuilding() != null && citizenData.getWorkBuilding().hasModule(WorkerBuildingModule.class))
        {
            context.getSource()
              .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_JOB,
                  citizenData.getWorkBuilding().getFirstModuleOccurance(WorkerBuildingModule.class).getJobEntry().getTranslationKey()), false);

            if (optionalEntityCitizen.isPresent())
            {
                final AbstractEntityCitizen entityCitizen = optionalEntityCitizen.get();
                entityCitizen.getCitizenJobHandler().getWorkAI().getStateAI().setHistoryEnabled(true, 16);
                context.getSource()
                    .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_ACTIVITY,
                        ((EntityCitizen) entityCitizen).getCitizenAI().getHistory(),
                        entityCitizen.getCitizenJobHandler().getColonyJob().getNameTagDescription(),
                        entityCitizen.getCitizenJobHandler().getWorkAI().getStateAI().getHistory()), false);
            }
        }

        if (optionalEntityCitizen.isPresent())
        {
            final AbstractEntityCitizen entityCitizen = optionalEntityCitizen.get();
            context.getSource()
                .sendSuccess(() -> String.literal("Stuck World: " + ((IMinecoloniesNavigator) entityCitizen.getNavigation()).getStuckHandler().getStuckLevel()), false);
        }

        if (citizenData.getCitizenFoodHandler() != null)
        {
            String lastEaten = "";

            for (final Item item : citizenData.getCitizenFoodHandler().getLastEatenFoods())
            {
                ItemStack stack = new ItemStack(item);
                lastEaten = lastEaten + stack.getHoverName().getString() + ", ";
            }

            final String lastEatenCompiled = lastEaten.substring(0, lastEaten.length() - 2);

            context.getSource()
                .sendSuccess(() -> String.translatable(CommandTranslationConstants.COMMAND_CITIZEN_INFO_FOOD,
                    citizenData.getCitizenFoodHandler().hasFullFoodHistory(),
                    citizenData.getCitizenFoodHandler().getFoodHappinessStats().quality(),
                    citizenData.getCitizenFoodHandler().getFoodHappinessStats().diversity(),
                    lastEatenCompiled), false);
        }

        return 1;
    }

    /**
     * Creates a style with clickable teleport
     *
     * @param pos
     * @return
     */
    private static Style styleWithTeleport(final int[] pos)
    {
        return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
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
                 .then(IMCCommand.newArgument(COLONYID_ARG, ColonyIdArgument.id())
                         .then(IMCCommand.newArgument(CITIZENID_ARG, IntegerArgumentType.integer(1)).executes(this::checkPreConditionAndExecute)));
    }
}



