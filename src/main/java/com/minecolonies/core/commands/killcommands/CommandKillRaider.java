package com.minecolonies.core.commands.killcommands;

import com.minecolonies.api.colony.colonyEvents.EventStatus;
import com.minecolonies.api.colony.colonyEvents.IColonyEvent;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.util.DamageSourceKeys;
import com.minecolonies.core.commands.commandTypes.IMCOPCommand;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
// import net.minecraft.world.entity.EntityTypeTest; // [1.7.10] not available

public class CommandKillRaider implements IMCOPCommand
{
    private int entitiesKilled = 0;

    /**
     * What happens when the command is executed
     *
     * @param context the context of the command execution
     */
    @Override
    public int onExecute(final CommandContext<CommandSourceStack> context)
    {
        entitiesKilled = 0;

        context.getSource().getLevel().getEntities(EntityTypeTest.forClass(AbstractEntityMinecoloniesRaider.class), (e) -> true).forEach(entity ->
        {
            if (entity != null)
            {
                final AbstractEntityMinecoloniesRaider EntityCreature = (AbstractEntityMinecoloniesRaider) entity;
                EntityCreature.die(context.getSource().getLevel().damageSources().source(DamageSourceKeys.CONSOLE));
                EntityCreature.remove(Entity.RemovalReason.DISCARDED);

                final IColonyEvent event = EntityCreature.getColony().getEventManager().getEventByID(EntityCreature.getEventID());

                if (event != null)
                {
                    event.setStatus(EventStatus.DONE);
                }

                entitiesKilled++;
            }
        });
        context.getSource().sendSuccess(() -> String.literal(entitiesKilled + " entities killed"), true);
        return 1;
    }

    /**
     * Name string of the command.
     */
    @Override
    public String getName()
    {
        return "raider";
    }
}


