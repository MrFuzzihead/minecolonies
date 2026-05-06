package com.minecolonies.core.colony.crafting;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.FactoryVoidInput;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.crafting.IItemStorageFactory;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.constant.SerializationIdentifierConstants;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * Factory implementation taking care of creating new instances, serializing and deserializing ItemStorage.
 */
public class ItemStorageFactory implements IItemStorageFactory
{
    /**
     * Compound NBTBase for the size.
     */
    private static final String TAG_SIZE = "size";

    /**
     * Compound NBTBase for the stack.
     */
    private static final String TAG_STACK = "stack";

    /**
     * Compound NBTBase for the NBT info
     */
    private static final String TAG_SHOULDIGNORENBT = "ignoreNBT";

    /**
     * Compound NBTBase for the Damage Match Info
     */
    private static final String TAG_SHOULDIGNOREDAMAGE = "ignoreDamage";

    @NotNull
    @Override
    public TypeToken<ItemStorage> getFactoryOutputType()
    {
        return TypeConstants.ITEMSTORAGE;
    }

    @NotNull
    @Override
    public TypeToken<FactoryVoidInput> getFactoryInputType()
    {
        return TypeConstants.FACTORYVOIDINPUT;
    }

    @NotNull
    @Override
    public ItemStorage getNewInstance(@NotNull final ItemStack stack, final int size, final boolean ignoreDamage, final boolean ignoreNBT)
    {
        return new ItemStorage(stack, size, ignoreDamage, ignoreNBT);
    }

    @NotNull
    @Override
    public NBTTagCompound serialize(@NotNull final IFactoryController controller, @NotNull final ItemStorage storage)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        @NotNull NBTTagCompound stackTag = new NBTTagCompound();
        storage.getItemStack().save(stackTag);
        compound.setTag(TAG_STACK, stackTag);
        compound.setInteger(TAG_SIZE, storage.getAmount());
        compound.setBoolean(TAG_SHOULDIGNOREDAMAGE, storage.ignoreDamageValue());
        compound.setBoolean(TAG_SHOULDIGNORENBT , storage.ignoreNBT());
        return compound;
    }

    @NotNull
    @Override
    public ItemStorage deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt)
    {
        final ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_STACK));
        stack.setCount(1);
        final int size = nbt.getInt(TAG_SIZE);
        final boolean ignoreNBT = nbt.getBoolean(TAG_SHOULDIGNORENBT);
        final boolean ignoreDamage = nbt.getBoolean(TAG_SHOULDIGNOREDAMAGE);
        return this.getNewInstance(stack, size, ignoreDamage, ignoreNBT);
    }

    @Override
    public void serialize(IFactoryController controller, ItemStorage input, PacketBuffer packetBuffer)
    {
        packetBuffer.writeItem(input.getItemStack());
        packetBuffer.writeVarInt(input.getAmount());
        packetBuffer.writeBoolean(input.ignoreDamageValue());
        packetBuffer.writeBoolean(input.ignoreNBT());
    }

    @Override
    public ItemStorage deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final ItemStack stack = buffer.readItem();
        final int size = buffer.readVarInt();
        final boolean ignoreDamage = buffer.readBoolean();
        final boolean ignoreNBT = buffer.readBoolean();
        return this.getNewInstance(stack, size, ignoreDamage, ignoreNBT);
    }

    @Override
    public short getSerializationId()
    {
        return SerializationIdentifierConstants.ITEM_STORAGE_ID;
    }
}



