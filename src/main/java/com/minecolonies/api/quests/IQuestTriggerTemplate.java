package com.minecolonies.api.quests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.minecolonies.api.colony.IColony;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;

/**
 * Quest triggers are used to check if a colony fulfills certain conditions for a quest to be made available.
 */
public interface IQuestTriggerTemplate
{
    /**
     * Check if the quest trigger condition is fulfilled.
     * @param colony the colony the quest is in.
     * @return true if so.
     */
    ITriggerReturnData canTriggerQuest(final IColony colony);

    /**
     * Check if the quest trigger condition is fulfilled.
     * @param questId the quest to try to trigger.
     * @param colony the colony the quest is in.
     * @return true if so.
     */
    default ITriggerReturnData canTriggerQuest(final ResourceLocation questId, final IColony colony)
    {
        return canTriggerQuest(colony);
    }

    /**
     * Match a nbt NBTBase and a json element NBTBase.
     * @param nbtTag the nbt NBTBase to check.
     * @param matchTag the element NBTBase to check.
     * @return true if the matchTag fits into the nbtTag or if they match.
     */
    static boolean matchNbt(final NBTBase nbtTag, final JsonElement matchTag)
    {
        return matchNbt(nbtTag, matchTag, 1);
    }

    /**
     * Match a nbt NBTBase and a json element NBTBase.
     * @param nbtTag the nbt NBTBase to check.
     * @param matchTag the element NBTBase to check.
     * @param count the number of elements to match in a list.
     * @return true if the matchTag fits into the nbtTag or if they match.
     */
    static boolean matchNbt(final NBTBase nbtTag, final JsonElement matchTag, final int count)
    {
        if (nbtTag instanceof final NBTTagCompound nbtCompound)
        {
            if (!(matchTag instanceof JsonObject matchObject))
            {
                return false;
            }

            for (String key : matchObject.keySet())
            {
                if (!nbtCompound.contains(key))
                {
                    return false;
                }

                if (!matchNbt(nbtCompound.get(key), matchObject.get(key)))
                {
                    return false;
                }
            }
            return true;
        }

        if (nbtTag instanceof NBTTagList nbtList)
        {
            // Check if we're trying to match an element in the list.
            int matchCount = 0;
            for (final NBTBase NBTBase : nbtList)
            {
                if (matchNbt(NBTBase, matchTag))
                {
                    matchCount++;
                    if (matchCount >= count)
                    {
                        return true;
                    }
                }
            }

            // This can also be partial matching (e.g. find 3 elements).
            if (!(matchTag instanceof JsonArray arrayTag))
            {
                return false;
            }

            for (final JsonElement element: arrayTag)
            {
                boolean matched = false;
                for (final NBTBase NBTBase : nbtList)
                {
                    if (matchNbt(NBTBase, element))
                    {
                        matched = true;
                        break;
                    }
                }

                if (!matched)
                {
                    return false;
                }
            }
            return true;
        }

        // Don't handle non primitives from here on.
        if (!(matchTag instanceof JsonPrimitive))
        {
            return false;
        }

        // Full equals for string.
        if (nbtTag instanceof NBTTagString && ((JsonPrimitive) matchTag).isString())
        {
            return nbtTag.getAsString().equals(matchTag.getAsString());
        }
        else if (nbtTag instanceof NBTTagByte && ((JsonPrimitive) matchTag).isBoolean())
        {
            return (((NBTTagByte) nbtTag).getAsByte() == 0) != matchTag.getAsBoolean();
        }
        // Larger equals for numbers.
        else if (nbtTag instanceof NumericTag && ((JsonPrimitive) matchTag).isNumber())
        {
            return ((NumericTag) nbtTag).getAsDouble() >= matchTag.getAsDouble();
        }
        return false;
    }
}



