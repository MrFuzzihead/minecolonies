package com.minecolonies.api.entity.citizen.happiness;

import com.minecolonies.api.colony.ICitizenData;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Abstract happiness modifier implementation.
 */
public abstract class AbstractHappinessModifier implements IHappinessModifier
{
    /**
     * The supplier to get the happiness factor.
     */
    private IHappinessSupplierWrapper supplier;

    /**
     * The id of the modifier.
     */
    public String id;

    /**
     * The weight of the modifier.
     */
    private double weight;

    /**
     * Create an instance of the happiness modifier.
     *
     * @param id     its string id.
     * @param weight its weight.
     */
    public AbstractHappinessModifier(final String id, final double weight, final IHappinessSupplierWrapper supplier)
    {
        this.id = id;
        this.weight = weight;
        this.supplier = supplier;
    }

    @Override
    public double getFactor(@Nullable final ICitizenData citizenData)
    {
        return citizenData == null ? supplier.getLastCachedValue() : supplier.getValue(citizenData);
    }

    /**
     * Create an empty instance of the abstract happiness modifier.
     */
    public AbstractHappinessModifier()
    {
        super();
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void read(final NBTTagCompound compoundNBT, final boolean persist)
    {
        this.id = compoundNBT.getString(TAG_ID);
        this.weight = compoundNBT.getDouble(TAG_WEIGHT);
        final NBTTagCompound supplierCompound = compoundNBT.getCompoundTag(TAG_SUPPLIER); // [1.7.10]
        if (supplierCompound.hasKey(TAG_ID)) // [1.7.10]
        {
            supplier = new DynamicHappinessSupplier();
        }
        else
        {
            supplier = new StaticHappinessSupplier();
        }
        supplier.deserializeNBT(supplierCompound);
    }

    @Override
    public void write(final NBTTagCompound compoundNBT, final boolean persist)
    {
        compoundNBT.setString(TAG_ID, this.id); // [1.7.10]
        compoundNBT.setDouble(TAG_WEIGHT, this.weight); // [1.7.10]
        compoundNBT.setTag(TAG_SUPPLIER, this.supplier.serializeNBT()); // [1.7.10]
    }

    @Override
    public double getWeight()
    {
        return weight;
    }
}


