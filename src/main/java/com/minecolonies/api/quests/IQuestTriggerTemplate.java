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
        // [1.7.10] No pattern-matching instanceof (Java 16+); use regular instanceof + cast
        if (nbtTag instanceof NBTTagCompound)
        {
            final NBTTagCompound nbtCompound = (NBTTagCompound) nbtTag;
            if (!(matchTag instanceof JsonObject))
            {
                return false;
            }
            final JsonObject matchObject = (JsonObject) matchTag;

            for (String key : matchObject.keySet())
            {
                if (!nbtCompound.hasKey(key)) // [1.7.10] contains→hasKey
                {
                    return false;
                }

                if (!matchNbt(nbtCompound.getTag(key), matchObject.get(key))) // [1.7.10] get→getTag
                {
                    return false;
                }
            }
            return true;
        }

        if (nbtTag instanceof NBTTagList)
        {
            final NBTTagList nbtList = (NBTTagList) nbtTag;
            // Check if we're trying to match an element in the list.
            // [1.7.10] NBTTagList is not Iterable; use indexed loop with compound getter
            int matchCount = 0;
            for (int i = 0; i < nbtList.tagCount(); i++)
            {
                // [1.7.10] No generic get(int); use getCompoundTagAt for compound lists
                final NBTBase nbtElement = nbtList.getCompoundTagAt(i);
                if (matchNbt(nbtElement, matchTag))
                {
                    matchCount++;
                    if (matchCount >= count)
                    {
                        return true;
                    }
                }
            }

            // This can also be partial matching (e.g. find 3 elements).
            if (!(matchTag instanceof JsonArray))
            {
                return false;
            }
            final JsonArray arrayTag = (JsonArray) matchTag;

            for (final JsonElement element: arrayTag)
            {
                boolean matched = false;
                for (int i = 0; i < nbtList.tagCount(); i++)
                {
                    final NBTBase nbtElement = nbtList.getCompoundTagAt(i);
                    if (matchNbt(nbtElement, element))
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
            // [1.7.10] getAsString()→func_150285_a_() on NBTTagString, but NBTTagString doesn't expose it on NBTBase
            // Use toString() which includes the string value (with quotes), or cast directly
            return ((NBTTagString) nbtTag).func_150285_a_().equals(((JsonPrimitive) matchTag).getAsString());
        }
        else if (nbtTag instanceof NBTTagByte && ((JsonPrimitive) matchTag).isBoolean())
        {
            // [1.7.10] getAsByte()→func_150290_f() on NBTTagByte
            return (((NBTTagByte) nbtTag).func_150290_f() == 0) != matchTag.getAsBoolean();
        }
        // Larger equals for numbers.
        // [1.7.10] NumericTag→NBTBase.NBTPrimitive
        else if (nbtTag instanceof NBTBase.NBTPrimitive && ((JsonPrimitive) matchTag).isNumber())
        {
            // [1.7.10] getAsDouble()→func_150286_g() on NBTPrimitive
            return ((NBTBase.NBTPrimitive) nbtTag).func_150286_g() >= matchTag.getAsDouble();
        }
        return false;
    }
}



