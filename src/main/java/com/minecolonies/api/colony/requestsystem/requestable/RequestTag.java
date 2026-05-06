package com.minecolonies.api.colony.requestsystem.requestable;
import net.minecraft.tags.TagKey;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
// [1.7.10] TagKey/ItemTags do not exist — replaced with ResourceLocation tag ID
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Deliverable that can only be fulfilled by a stack whose item is contained in a given tag with a given minimal amount of items.
 */
public class RequestTag implements IDeliverable
{
    private final static Set<TypeToken<?>> TYPE_TOKENS = ReflectionUtils.getSuperClasses(TypeToken.of(RequestTag.class)).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

    private static final String NBT_TAG      = "NBTBase";
    private static final String NBT_RESULT   = "Result";
    private static final String NBT_COUNT    = "Count";
    private static final String NBT_MINCOUNT = "MinCount";

    // [1.7.10] Tags do not exist; theTag is stored as its ResourceLocation identifier only.
    @NotNull
    private final ResourceLocation theTag;

    @NotNull
    private ItemStack result;

    private int count;
    private int minCount;

    public RequestTag(@NotNull final ResourceLocation tag, final int count)
    {
        this(tag, count, count);
    }

    public RequestTag(@NotNull final ResourceLocation tag, final int count, final int minCount)
    {
        this(tag, ItemStackUtils.EMPTY, count, minCount);
    }

    public RequestTag(@NotNull final ResourceLocation tag, @NotNull final ItemStack result, final int count, final int minCount)
    {
        this.theTag = tag;
        this.result = result;
        this.count = count;
        this.minCount = minCount;
    }

    @Override
    public boolean matches(@NotNull final ItemStack stack)
    {
        // [1.7.10] Tags do not exist; always returns false (no tag matching available).
        return false;
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public int getMinimumCount()
    {
        return minCount;
    }

    @NotNull
    @Override
    public ItemStack getResult()
    {
        return result;
    }

    @NotNull
    public ResourceLocation getTag()
    {
        return theTag;
    }

    @Override
    public void setResult(@NotNull final ItemStack result)
    {
        this.result = result;
    }

    @Override
    public IDeliverable copyWithCount(final int newCount)
    {
        return new RequestTag(theTag, result, newCount, minCount);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestTag tag1 = (RequestTag) o;
        return getCount() == tag1.getCount() &&
                 getMinimumCount() == tag1.getMinimumCount() &&
                 getTag().equals(tag1.getTag()) &&
                 getResult().equals(tag1.getResult());
    }

    @Override
    public int hashCode()
    {
        int result1 = theTag.toString().hashCode();
        result1 = 31 * result1 + getResult().hashCode();
        return result1;
    }

    public static NBTTagCompound serialize(final IFactoryController controller, final RequestTag input)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setString(NBT_TAG, input.getTag().toString());
        if (!ItemStackUtils.isEmpty(input.getResult()))
        {
            compound.setTag(NBT_RESULT, input.getResult().writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger(NBT_COUNT, input.getCount());
        compound.setInteger(NBT_MINCOUNT, input.getMinimumCount());
        return compound;
    }

    public static void serialize(final IFactoryController controller, final PacketBuffer buffer, final RequestTag input)
    {
        try { buffer.writeStringToBuffer(input.getTag().toString()); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        buffer.writeBoolean(!ItemStackUtils.isEmpty(input.getResult()));
        if (!ItemStackUtils.isEmpty(input.getResult()))
        {
        try { buffer.writeItemStackToBuffer(input.getResult()); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }
        buffer.writeInt(input.getCount());
        buffer.writeInt(input.getMinimumCount());
    }

    public static RequestTag deserialize(final IFactoryController controller, final PacketBuffer buffer)
    {
        try {
            final ResourceLocation theTag = new ResourceLocation(buffer.readStringFromBuffer(32767));
            final ItemStack result = buffer.readBoolean() ? buffer.readItemStackFromBuffer() : ItemStackUtils.EMPTY;
            final int count = buffer.readInt();
            final int minCount = buffer.readInt();
            return new RequestTag(theTag, result, count, minCount);
        } catch (java.io.IOException e) { throw new RuntimeException(e); }
    }

    public static RequestTag deserialize(final IFactoryController controller, final NBTTagCompound compound)
    {
        final ResourceLocation theTag = new ResourceLocation(compound.getString(NBT_TAG));
        final ItemStack result = compound.hasKey(NBT_RESULT)
            ? ItemStackUtils.deserializeFromNBT(compound.getCompoundTag(NBT_RESULT))
            : ItemStackUtils.EMPTY;

        int count = compound.getInteger("size");
        int minCount = count;
        if (compound.hasKey(NBT_COUNT))
        {
            count = compound.getInteger(NBT_COUNT);
            minCount = compound.getInteger(NBT_MINCOUNT);
        }
        return new RequestTag(theTag, result, count, minCount);
    }

    @Override
    public Set<TypeToken<?>> getSuperClasses()
    {
        return TYPE_TOKENS;
    }
}
