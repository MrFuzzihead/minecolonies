package com.minecolonies.api.util;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import net.minecraft.util.ChatFormatting;
import net.minecraft.util.ClickEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Simple class for containing reusable player messaging logic.
 * [1.7.10] Ported from 1.21 Component API to 1.7.10 IChatComponent API.
 */
public class MessageUtils
{
    private static MessageBuilder NOOP = new MessageBuilder(new ChatComponentText(""))
    {
        @Override public void sendTo(final EntityPlayer... players) {}
        @Override public void sendTo(final Collection<EntityPlayer> players) {}
        @Override public MessageBuilderColonyPlayerSelector sendTo(final IColony colony) { return NOOPColony; }
        @Override public MessageBuilderColonyPlayerSelector sendTo(final IColony colony, final boolean always) { return NOOPColony; }
    };

    private static MessageBuilderColonyPlayerSelector NOOPColony = new MessageBuilderColonyPlayerSelector(null, null, false)
    {
        public void forAllPlayers() {}
        public void forManagers()   {}
    };

    public static MessageBuilder forCitizen(final AbstractEntityCitizen citizen, final String keyIn, final Object... msg)
    {
        return forCitizen(citizen, new ChatComponentTranslation(keyIn, msg));
    }

    public static MessageBuilder forCitizen(final AbstractEntityCitizen citizen, final IChatComponent component)
    {
        if (citizen.getCitizenColonyHandler().getColonyOrRegister() != null)
        {
            final IJob<?> job = citizen.getCitizenJobHandler().getColonyJob();
            IChatComponent prefix;
            if (job != null)
            {
                prefix = new ChatComponentTranslation(job.getJobRegistryEntry().getTranslationKey())
                    .appendSibling(new ChatComponentText(" "))
                    .appendSibling(citizen.getCustomNameTag() != null ? new ChatComponentText(citizen.getCustomNameTag()) : new ChatComponentText(""))
                    .appendSibling(new ChatComponentText(": "));
            }
            else
            {
                prefix = (citizen.getCustomNameTag() != null ? new ChatComponentText(citizen.getCustomNameTag()) : new ChatComponentText(""))
                    .appendSibling(new ChatComponentText(": "));
            }
            return new MessageBuilder(prefix.appendSibling(component));
        }
        return NOOP;
    }

    public static MessageBuilder format(final String key, final Object... args)
    {
        return format(new ChatComponentTranslation(key, args));
    }

    public static MessageBuilder format(final IChatComponent component)
    {
        return new MessageBuilder(component);
    }

    public enum MessagePriority
    {
        NORMAL(ChatFormatting.GRAY),
        IMPORTANT(ChatFormatting.GOLD),
        DANGER(ChatFormatting.RED);

        private final ChatFormatting color;
        MessagePriority(final ChatFormatting color) { this.color = color; }
    }

    public static class MessageBuilder
    {
        private IChatComponent component;

        @NotNull
        private MessagePriority priority = MessagePriority.NORMAL;

        @Nullable
        private ClickEvent clickEvent;

        MessageBuilder(final IChatComponent component)
        {
            this.component = component;
        }

        @NotNull
        public MessageBuilder withPriority(final MessagePriority priority)
        {
            this.priority = priority;
            return this;
        }

        public MessageBuilder withClickEvent(@NotNull final ClickEvent clickEvent)
        {
            this.clickEvent = clickEvent;
            return this;
        }

        public MessageBuilder append(final String key, final Object... args)
        {
            return append(new ChatComponentTranslation(key, args));
        }

        public MessageBuilder append(final IChatComponent other)
        {
            component.appendSibling(other);
            return this;
        }

        public IChatComponent create()
        {
            // [1.7.10] Apply color via getChatStyle
            component.getChatStyle().setColor(priority.color.getDelegate());
            return component;
        }

        public void sendTo(final EntityPlayer... players)
        {
            sendTo(Arrays.asList(players));
        }

        public void sendTo(final Collection<EntityPlayer> players)
        {
            final IChatComponent msg = create();
            for (EntityPlayer player : players)
            {
                player.addChatMessage(msg);
            }
        }

        public void sendToClose(final int[] pos, final int range, final List<EntityPlayer> players)
        {
            final IChatComponent msg = create();
            final double rangeSq = (double) range * range;
            for (EntityPlayer player : players)
            {
                final double dx = player.posX - pos[0];
                final double dy = player.posY - pos[1];
                final double dz = player.posZ - pos[2];
                if (dx*dx + dy*dy + dz*dz < rangeSq)
                {
                    player.addChatMessage(msg);
                }
            }
        }

        public MessageBuilderColonyPlayerSelector sendTo(final IColony colony)
        {
            return sendTo(colony, false);
        }

        public MessageBuilderColonyPlayerSelector sendTo(final IColony colony, final boolean alwaysShowColony)
        {
            return new MessageBuilderColonyPlayerSelector(create(), colony, alwaysShowColony);
        }
    }

    public static class MessageBuilderColonyPlayerSelector
    {
        private final IChatComponent rootComponent;
        private final IColony        colony;
        private final boolean        alwaysShowColony;

        public MessageBuilderColonyPlayerSelector(final IChatComponent rootComponent, final IColony colony, final boolean alwaysShowColony)
        {
            this.rootComponent    = rootComponent;
            this.colony           = colony;
            this.alwaysShowColony = alwaysShowColony;
        }

        public void forAllPlayers()
        {
            sendInternal(colony.getMessagePlayerEntities());
        }

        public void forManagers()
        {
            sendInternal(colony.getImportantMessageEntityPlayers());
        }

        private void sendInternal(final Collection<EntityPlayer> players)
        {
            for (EntityPlayer player : players)
            {
                IChatComponent msg = rootComponent;
                if (alwaysShowColony || !colony.isCoordInColony(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ))
                {
                    msg = new ChatComponentText("[" + colony.getName() + "] ").appendSibling(rootComponent);
                }
                player.addChatMessage(msg);
            }
        }
    }
}
