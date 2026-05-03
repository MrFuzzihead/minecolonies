package com.minecolonies.api.colony.requestsystem.requestable;

// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.item.ItemStack;

/**
 * Stack based requests interface for display purposes.
 */
public interface IStackBasedTask
{
    /**
     * Get the stack associated to the task.
     * @return the stack.
     */
    ItemStack getTaskStack();

    /**
     * Get the request related count.
     * @return the count.
     */
    int getDisplayCount();

    /**
     * Get a display prefix String.
     * @return the String.
     */
    String getDisplayPrefix();
}



