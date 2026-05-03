package com.minecolonies.api.research.costs;

import com.google.gson.JsonObject;
import com.minecolonies.api.research.IResearchCost;
import com.minecolonies.api.research.ModResearchCosts;
import com.minecolonies.core.util.GsonHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed - TagKey<Item> replaced with ResourceLocation stored as string
import net.minecraft.item.Item;
// [1.7.10] registries removed - ForgeRegistries.ITEMS.tags() not available

import java.util.Collections;
import java.util.List;

import static com.minecolonies.api.research.util.ResearchConstants.*;

/**
 * A plain item cost that takes a list of several items that have to be fulfilled.
 * [1.7.10] TagKey<Item> replaced with a stored ResourceLocation key (no tag lookup at runtime).
 */
public class TagItemCost implements IResearchCost
{
    private static final String JSON_PROP_TAG      = "NBTBase";
    private static final String JSON_PROP_QUANTITY = "quantity";

    /** [1.7.10] Store the tag key as a ResourceLocation string; no runtime tag lookup. */
    private final ResourceLocation tagKey;

    private final int count;

    public TagItemCost(final NBTTagCompound compound)
    {
        this.tagKey = new ResourceLocation(compound.getString(TAG_COST_TAG));
        this.count  = compound.getInteger(TAG_COST_COUNT);
    }

    public TagItemCost(final JsonObject json)
    {
        this.tagKey = GsonHelper.getAsResourceLocation(json, JSON_PROP_TAG);
        this.count  = Math.max(GsonHelper.getAsInt(json, JSON_PROP_QUANTITY, 1), 1);
    }

    @Override
    public ModResearchCosts.ResearchCostEntry getType()
    {
        return ModResearchCosts.tagItemCost.get();
    }

    @Override
    public List<Item> getItems()
    {
        // [1.7.10] Tags not available; return empty list — callers must handle gracefully
        return Collections.emptyList();
    }

    @Override
    public int getCount()
    {
        return this.count;
    }

    @Override
    public String getTranslatedName()
    {
        return String.format("com.minecolonies.coremod.research.tags.%s", this.tagKey);
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(TAG_COST_COUNT, this.count);
        compound.setString(TAG_COST_TAG, this.tagKey.toString());
        return compound;
    }
}
