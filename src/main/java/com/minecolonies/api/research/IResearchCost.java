package com.minecolonies.api.research;

import com.minecolonies.api.research.ModResearchCosts.ResearchCostEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.item.Item;

import java.util.List;

/**
 * Cost item for researches.
 */
public interface IResearchCost
{
    /**
     * The type for this cost.
     *
     * @return the type.
     */
    ResearchCostEntry getType();

    /**
     * Get the count for this cost.
     *
     * @return the count.
     */
    int getCount();

    /**
     * Get the translated text for this research cost.
     *
     * @return the translated text.
     */
    default String getTranslatedName()
    {
        return ComponentUtils.formatList(getItems().stream().map(Item::getDescription).toList(), String.literal(" / "));
    }

    /**
     * Get the list of items for this cost.
     *
     * @return the list of items.
     */
    List<Item> getItems();

    /**
     * Write the research cost to nbt data.
     *
     * @return the nbt data.
     */
    NBTTagCompound writeToNBT();
}




