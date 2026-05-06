package com.minecolonies.core.entity.citizen.citizenhandlers;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenMournHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTBase;

import java.util.*;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_DECEASED;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_MOURNING;

/**
 * The new happiness handler for the citizen.
 */
public class CitizenMournHandler implements ICitizenMournHandler
{
    /**
     * Citizens that have recently died and were somehow related to this citizen.
     */
    private Set<String> deceasedCitizens = new HashSet<>();

    /**
     * If the citizen is currently mourning.
     */
    private boolean isMourning;

    /**
     * Create a new instance of the citizen happiness handler.
     *
     * @param data the data to handle.
     */
    public CitizenMournHandler(final ICitizenData data)
    {

    }

    @Override
    public void read(final NBTTagCompound compound)
    {
        isMourning = compound.getBoolean(TAG_MOURNING);
        final NBTTagList NBTBase = compound.getTagList(TAG_DECEASED, NBTBase.TAG_STRING);
        for (int i = 0; i < NBTBase.size(); i++)
        {
            deceasedCitizens.add(NBTBase.getString(i));
        }
    }

    @Override
    public void write(final NBTTagCompound compound)
    {
        compound.setBoolean(TAG_MOURNING, isMourning);
        final NBTTagList deceasedNbt = new NBTTagList();
        for (final String deceased : deceasedCitizens)
        {
            deceasedNbt.add(new NBTTagString(deceased));
        }
        compound.setTag(TAG_DECEASED, deceasedNbt);
    }

    @Override
    public void addDeceasedCitizen(final String name)
    {
        deceasedCitizens.add(name);
    }

    @Override
    public Set<String> getDeceasedCitizens()
    {
    	return deceasedCitizens;
    }

    @Override
    public void removeDeceasedCitizen(final String name)
    {
        deceasedCitizens.remove(name);
    }

    @Override
    public void clearDeceasedCitizen()
    {
        deceasedCitizens.clear();
    }

    @Override
    public boolean shouldMourn()
    {
        return !deceasedCitizens.isEmpty();
    }

    @Override
    public boolean isMourning()
    {
        return isMourning;
    }

    @Override
    public void setMourning(final boolean mourn)
    {
        this.isMourning = mourn;
    }
}




