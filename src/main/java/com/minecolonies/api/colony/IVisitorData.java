package com.minecolonies.api.colony;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * Data for colony visitors, based on citizendata
 */
public interface IVisitorData extends ICitizenData
{
    /**
     * Sets the recruitment cost stack
     */
    void setRecruitCosts(final ItemStack cost);

    /**
     * Returns the recruitment cost stack
     *
     * @return itemstack
     */
    ItemStack getRecruitCost();

    /**
     * The position the visitor is sitting on
     *
     * @return sitting pos
     */
    int[] getSittingPosition();

    /**
     * Sets the sitting position
     *
     * @param pos sitting pos
     */
    void setSittingPosition(final int[] pos);
}


