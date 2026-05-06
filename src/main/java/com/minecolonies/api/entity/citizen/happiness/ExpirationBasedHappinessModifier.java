package com.minecolonies.api.entity.citizen.happiness;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.util.constant.NbtTagConstants;
import net.minecraft.nbt.NBTTagCompound;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * The Expiration based happiness modifier. These modifiers are invoked for a limited period of time and have a happiness buff or boost for this time on the happiness. This can
 * also be inverted resulting in a buff or boost if this modifier is not invoked regularly.
 */
public final class ExpirationBasedHappinessModifier extends AbstractHappinessModifier implements ITimeBasedHappinessModifier
{
    /**
     * The number of passed days.
     */
    private int days;

    /**
     * Period of time this modifier applies.
     */
    private int period;

    /**
     * Create an instance of the happiness modifier.
     *
     * @param id       its string id.
     * @param weight   its weight.
     * @param period   the period.
     * @param supplier the supplier to get the factor.
     */
    public ExpirationBasedHappinessModifier(final String id, final double weight, final IHappinessSupplierWrapper supplier, final int period)
    {
        super(id, weight, supplier);
        this.days = period;
        this.period = period;
    }

    /**
     * Create an instance of the happiness modifier.
     */
    public ExpirationBasedHappinessModifier()
    {
        super();
    }

    @Override
    public double getFactor(final ICitizenData data)
    {
        if (days > 0 && days <= period)
        {
            return super.getFactor(data);
        }
        return 1.0;
    }

    @Override
    public void reset()
    {
        this.days = period;
    }

    @Override
    public void dayEnd(final ICitizenData data)
    {
        if (days > 0)
        {
            days--;
        }
    }

    @Override
    public int getDays()
    {
        return days;
    }

    @Override
    public void read(final NBTTagCompound compoundNBT, final boolean persist)
    {
        super.read(compoundNBT, persist);
        this.days = compoundNBT.getInteger(TAG_DAY); // [1.7.10]
        this.period = compoundNBT.getInteger(TAG_PERIOD); // [1.7.10]
    }

    @Override
    public void write(final NBTTagCompound compoundNBT, final boolean persist)
    {
        super.write(compoundNBT, persist);
        compoundNBT.setString(NbtTagConstants.TAG_MODIFIER_TYPE, HappinessRegistry.EXPIRATION_MODIFIER.toString()); // [1.7.10]
        compoundNBT.setInteger(TAG_DAY, days); // [1.7.10]
        compoundNBT.setInteger(TAG_PERIOD, period); // [1.7.10]
    }
}


