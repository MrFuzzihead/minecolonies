package com.minecolonies.api.colony.interactionhandling;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.NbtTagConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * The abstract interaction response handler to be extended by the other ones.
 */
public abstract class AbstractInteractionResponseHandler implements IInteractionResponseHandler
{
    /**
     * The text the citizen is saying.
     */
    private String inquiry;

    /**
     * The map of response options of the player, to new inquires of the interacting entity.
     */
    private Map<String, String> responses = new LinkedHashMap<>();

    /**
     * If the interaction is a primary (true) or secondary (false) interaction.
     */
    private boolean primary;

    /**
     * The interaction priority.
     */
    private IChatPriority priority;

    /**
     * The inquiry of the citizen.
     *
     * @param inquiry        the inquiry.
     * @param primary        if primary inquiry.
     * @param priority       the priority.
     * @param responseTuples optional response options.
     */
    @SafeVarargs
    public AbstractInteractionResponseHandler(
      @NotNull final String inquiry,
      final boolean primary,
      final IChatPriority priority,
      final Tuple<String, String>... responseTuples)
    {
        this.inquiry = inquiry;
        this.primary = primary;
        this.priority = priority;
        for (final Tuple<String, String> element : responseTuples)
        {
            this.responses.put(element.getA(), element.getB());
        }
    }

    /**
     * Way to load the response handler.
     */
    public AbstractInteractionResponseHandler()
    {
        // Do nothing, await loading from NBT.
    }

    @Override
    public String getInquiry()
    {
        return inquiry;
    }

    @Nullable
    @Override
    public String getResponseResult(final String response)
    {
        return responses.getOrDefault(response, null);
    }

    @Override
    public List<String> getPossibleResponses()
    {
        return ImmutableList.copyOf(responses.keySet());
    }

    /**
     * Serialize the response handler to NBT.
     *
     * @return the serialized data.
     */
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound NBTBase = new NBTTagCompound();
        NBTBase.putString(TAG_INQUIRY, String.Serializer.toJson(this.inquiry));
        final NBTTagList list = new NBTTagList();
        for (final Map.Entry<String, String> element : responses.entrySet())
        {
            final NBTTagCompound elementTag = new NBTTagCompound();
            elementTag.putString(TAG_RESPONSE, String.Serializer.toJson(element.getKey()));
            elementTag.putString(TAG_NEXT_INQUIRY, String.Serializer.toJson(element.getValue()));

            list.add(elementTag);
        }
        NBTBase.put(TAG_RESPONSES, list);
        NBTBase.putBoolean(TAG_PRIMARY, isPrimary());
        NBTBase.putInt(TAG_PRIORITY, priority.getPriority());
        NBTBase.putString(NbtTagConstants.TAG_HANDLER_TYPE, getType());
        return NBTBase;
    }

    /**
     * Deserialize the response handler from NBT.
     */
    public void deserializeNBT(@NotNull final NBTTagCompound compoundNBT)
    {
        this.inquiry = String.Serializer.fromJson(compoundNBT.getString(TAG_INQUIRY));
        final NBTTagList list = compoundNBT.getList(TAG_RESPONSES, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            final NBTTagCompound nbt = list.getCompound(i);
            this.responses.put(String.Serializer.fromJson(nbt.getString(TAG_RESPONSE)), String.Serializer.fromJson(nbt.getString(TAG_NEXT_INQUIRY)));
        }
        this.primary = compoundNBT.getBoolean(TAG_PRIMARY);
        this.priority = ChatPriority.values()[compoundNBT.getInt(TAG_PRIORITY)];
    }

    @Override
    public boolean isPrimary()
    {
        return primary;
    }

    @Override
    public IChatPriority getPriority()
    {
        return this.priority;
    }

    @Override
    public boolean isVisible(final World world)
    {
        return true;
    }

    @Override
    public boolean isValid(final ICitizenData colony)
    {
        return true;
    }
}




