package com.minecolonies.api.colony.requestsystem.requestable;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class used to represent equipment inside the request system.
 */
public class Tool implements IDeliverable
{
    /**
     * Set of type tokens belonging to this class.
     */
    private final static Set<TypeToken<?>> TYPE_TOKENS =
      ReflectionUtils.getSuperClasses(TypeToken.of(Tool.class)).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TYPE      = "Type";
    private static final String NBT_MIN_LEVEL = "MinLevel";
    private static final String NBT_MAX_LEVEL = "MaxLevel";
    private static final String NBT_RESULT    = "Result";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    private final EquipmentTypeEntry equipmentType;

    @NotNull
    private final Integer minLevel;

    @NotNull
    private final Integer maxLevel;

    @NotNull
    private ItemStack result = ItemStackUtils.EMPTY;

    public Tool(@NotNull final EquipmentTypeEntry equipmentType, @NotNull final Integer minLevel, @NotNull final Integer maxLevel)
    {
        this(equipmentType, minLevel, maxLevel, ItemStackUtils.EMPTY);
    }

    public Tool(@NotNull final EquipmentTypeEntry equipmentType, @NotNull final Integer minLevel, @NotNull final Integer maxLevel, @NotNull final ItemStack result)
    {
        this.equipmentType = equipmentType;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.result = result;
    }

    /**
     * Serializes this equipment into NBT.
     *
     * @param controller The IFactoryController used to serialize sub types.
     * @param equipment       the equipment to serialize.
     * @return The NBTTagCompound containing the equipment data.
     */
    @NotNull
    public static NBTTagCompound serialize(final IFactoryController controller, final Tool equipment)
    {
        final NBTTagCompound compound = new NBTTagCompound();

        compound.setString(NBT_TYPE, equipment.getEquipmentType().getRegistryName().toString());
        compound.setInteger(NBT_MIN_LEVEL, equipment.getMinLevel());
        compound.setInteger(NBT_MAX_LEVEL, equipment.getMaxLevel());
        compound.setTag(NBT_RESULT, equipment.getResult().writeToNBT(new net.minecraft.nbt.NBTTagCompound()));

        return compound;
    }

    /**
     * Returns the equipment type that is requested.
     *
     * @return The equipment type that is requested.
     */
    @NotNull
    public EquipmentTypeEntry getEquipmentType()
    {
        return equipmentType;
    }

    /**
     * The minimal equipment World requested.
     *
     * @return The minimal equipment World requested.
     */
    @NotNull
    public Integer getMinLevel()
    {
        return minLevel;
    }

    /**
     * The maximum equipment World requested.
     *
     * @return The maximum equipment World requested.
     */
    @NotNull
    public Integer getMaxLevel()
    {
        return maxLevel;
    }

    /**
     * Static method that constructs an instance from NBT.
     *
     * @param controller The {@link IFactoryController} to deserialize components with.
     * @param nbt        The nbt to serialize from.
     * @return An instance of equipment with the data contained in the given NBT.
     */
    @NotNull
    public static Tool deserialize(final IFactoryController controller, final NBTTagCompound nbt)
    {
        //API:Map the given strings a proper way.
        String resLoc = nbt.getString(NBT_TYPE);
        final EquipmentTypeEntry type = ModEquipmentTypes.lookup(EquipmentTypeEntry.parseResourceLocation(resLoc));
        final Integer minLevel = nbt.getInteger(NBT_MIN_LEVEL);
        final Integer maxLevel = nbt.getInteger(NBT_MAX_LEVEL);
        final ItemStack result = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(NBT_RESULT));

        return new Tool(type, minLevel, maxLevel, result);
    }

    /**
     * Serialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the the buffer to write to.
     * @param input      the input to serialize.
     */
    public static void serialize(final IFactoryController controller, final PacketBuffer buffer, final Tool input)
    {
        try { buffer.writeStringToBuffer(input.getEquipmentType().getRegistryName().toString()); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        buffer.writeInt(input.getMinLevel());
        buffer.writeInt(input.getMaxLevel());
        buffer.writeBoolean(!ItemStackUtils.isEmpty(input.result));
        if (!ItemStackUtils.isEmpty(input.result))
        {
            try { buffer.writeItemStackToBuffer(input.result); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }
    }

    /**
     * Deserialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the buffer to read.
     * @return the deliverable.
     */
    public static Tool deserialize(final IFactoryController controller, final PacketBuffer buffer)
    {
        try {
            final EquipmentTypeEntry type = ModEquipmentTypes.lookup(new net.minecraft.util.ResourceLocation(buffer.readStringFromBuffer(32767)));
            final int minLevel = buffer.readInt();
            final int maxLevel = buffer.readInt();
            final ItemStack result = buffer.readBoolean() ? buffer.readItemStackFromBuffer() : null;

            return new Tool(type, minLevel, maxLevel, result);
        } catch (java.io.IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean matches(@NotNull final ItemStack stack)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return false;
        }

        try
        {
            return ItemStackUtils.hasEquipmentLevel(stack, getEquipmentType(), getMinLevel(), getMaxLevel());
        }
        catch (final Exception e)
        {
            Log.getLogger().warn("Got exception for Itemstack when trying to match equipment World: " + stack.getDisplayName() + " - " + stack.getItem().getClass().getName(), e);
            return false;
        }
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public int getMinimumCount()
    {
        return 1;
    }

    /**
     * The resulting stack if set during creation, else ItemStack.Empty.
     *
     * @return The resulting stack.
     */
    @NotNull
    public ItemStack getResult()
    {
        return result;
    }

    @Override
    public void setResult(@NotNull final ItemStack result)
    {
        this.result = result;
    }

    @Override
    public IDeliverable copyWithCount(final int newCount)
    {
        return new Tool(this.equipmentType, this.minLevel, this.maxLevel, this.result);
    }

    /**
     * Check if the equipment is armor.
     *
     * @return true if so.
     */
    public boolean isArmor()
    {
        return equipmentType == ModEquipmentTypes.helmet.get() || equipmentType == ModEquipmentTypes.leggings.get() || equipmentType == ModEquipmentTypes.chestplate.get()
                 || equipmentType == ModEquipmentTypes.boots.get();
    }

    @Override
    public int hashCode()
    {
        int result1 = getEquipmentType().hashCode();
        result1 = 31 * result1 + getMinLevel().hashCode();
        result1 = 31 * result1 + getMaxLevel().hashCode();
        result1 = 31 * result1 + getResult().hashCode();
        return result1;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof final Tool equipment))
        {
            return false;
        }

        if (!getEquipmentType().equals(equipment.getEquipmentType()))
        {
            return false;
        }
        if (!getMinLevel().equals(equipment.getMinLevel()))
        {
            return false;
        }
        if (!getMaxLevel().equals(equipment.getMaxLevel()))
        {
            return false;
        }
        return ItemStackUtils.compareItemStacksIgnoreStackSize(getResult(), equipment.getResult());
    }

    @Override
    public Set<TypeToken<?>> getSuperClasses()
    {
        return TYPE_TOKENS;
    }
}



