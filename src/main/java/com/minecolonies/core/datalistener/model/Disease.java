package com.minecolonies.core.datalistener.model;

import com.minecolonies.api.crafting.ItemStorage;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] net.minecraft.util.random.Weight/WeightedEntry removed
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A possible disease.
 *
 * @param id        the id of the disease.
 * @param name      the name of the disease.
 * @param rarity    the rarity of the disease.
 * @param cureItems the list of items needed to heal.
 */
public record Disease(ResourceLocation id, String name, int rarity, List<ItemStorage> cureItems)
{
    /**
     * Predicate for the different usages to check if inventory contains a cure.
     *
     * @param cure the expected cure item.
     * @return the predicate for checking if the cure exists.
     */
    public static Predicate<ItemStack> hasCureItem(final ItemStorage cure)
    {
        return stack -> isCureItem(stack, cure);
    }

    /**
     * Check if the given item is a cure item.
     *
     * @param stack the input stack.
     * @param cure  the cure item.
     * @return true if so.
     */
    public static boolean isCureItem(final ItemStack stack, final ItemStorage cure)
    {
        return Objects.equals(new ItemStorage(stack), cure);
    }

    /**
     * Get the cure string containing all items required for the cure.
     *
     * @return the cure string.
     */
    public String getCureString()
    {
        final StringBuilder cureString = new StringBuilder();
        for (int i = 0; i < cureItems.size(); i++)
        {
            final ItemStorage cureStack = cureItems.get(i);
            cureString.append(cureStack.getItemStack().stackSize).append(" ").append(cureStack.getItemStack().getDisplayName());
            if (i != cureItems.size() - 1)
            {
                cureString.append(" + ");
            }
        }
        return cureString.toString();
    }

    /** [1.7.10] Replaces WeightedEntry.getWeight() */
    public int getWeight()
    {
        return rarity;
    }
}
