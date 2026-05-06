package com.minecolonies.core.quests.rewards;
import net.minecraft.tags.TagParser;

import com.google.gson.JsonObject;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.quests.IQuestInstance;
import com.minecolonies.api.quests.IQuestRewardTemplate;
import com.minecolonies.api.util.Log;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.ResourceLocation;
// [1.7.10] GsonHelper removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
// [1.7.10] registries removed

import static com.minecolonies.api.quests.QuestParseConstant.*;

/**
 * Item based quest reward.
 */
public class ItemRewardTemplate implements IQuestRewardTemplate
{
    /**
     * The stack to give to the player.
     */
    private final ItemStack item;

    /**
     * Setup the item reward.
     * @param item the item.
     */
    public ItemRewardTemplate(final ItemStack item)
    {
        this.item = item;
    }

    /**
     * Create the reward.
     * @param jsonObject the json to read from.
     * @return the reward object.
     */
    public static IQuestRewardTemplate createReward(final JsonObject jsonObject)
    {
        JsonObject details = jsonObject.getAsJsonObject(DETAILS_KEY);
        final int quantity = details.get(QUANTITY_KEY).getAsInt();
        final ItemStack item = new ItemStack(ForgeRegistries.ITEMS.getHolder(new ResourceLocation(details.get(ITEM_KEY).getAsString())).get().get());
        if (details.has(NBT_KEY))
        {
            try
            {
                item.setTag(TagParser.parseTag(GsonHelper.getAsString(details, NBT_KEY)));
            }
            catch (CommandSyntaxException e)
            {
                Log.getLogger().error("Unable to load itemstack nbt from json!");
                throw new RuntimeException(e);
            }
        }
        item.setCount(quantity);
        return new ItemRewardTemplate(item);
    }
    @Override
    public void applyReward(final IColony colony, final EntityPlayer player, final IQuestInstance colonyQuest)
    {
        player.getInventory().add(item);
    }
}



