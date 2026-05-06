package com.minecolonies.core.entity.other.cavalry;

import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.managers.interfaces.IManagedAnimal;
import com.minecolonies.api.colony.managers.interfaces.IAnimalDataView;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Cavalry Horse Entity — stub for 1.7.10 backport.
 * [1.7.10] Extends EntityHorse (1.7.10 vanilla horse) instead of 1.21 AbstractHorse/Horse.
 * TODO: Port full cavalry horse logic (DataWatcher, colony handler, pathfinding) from 1.21.
 */
public class CavalryHorseEntity extends EntityHorse implements IManagedAnimal<CavalryHorseEntity>
{
    public static final float SLIM_W = 0.70F;

    private static final String RESERVE_KEY = "horse_reserved_for_cavalry";

    private int colonyId         = 0;
    private int managedAnimalId  = 0;
    // private IAnimalColonyHandler colonyHandler; // TODO: restore when IAnimalColonyHandler is ported
    private AbstractAdvancedPathNavigate newNavigator;

    public CavalryHorseEntity(final World world)
    {
        super(world);
        this.setHorseTamed(true);
        // [1.7.10] setHorseSaddled may be setHorseSaddled or setHorseType; use attribute directly
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed).setBaseValue(0.35);
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger(NbtTagConstants.TAG_COLONY_ID, colonyId);
        compound.setInteger("managedAnimalId", managedAnimalId);
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if (compound.hasKey(NbtTagConstants.TAG_COLONY_ID))
        {
            colonyId = compound.getInteger(NbtTagConstants.TAG_COLONY_ID);
            managedAnimalId = compound.getInteger("managedAnimalId");
        }
    }

    // [1.7.10] IManagedAnimal stubs
    @Override public CavalryHorseEntity getEntity()    { return this; }
    @Override public int getColonyId()                 { return colonyId; }
    @Override public void setColonyId(final int id)    { this.colonyId = id; }
    @Override public int getManagedAnimalId()          { return managedAnimalId; }
    @Override public void setManagedAnimalId(final int id) { this.managedAnimalId = id; }
    @Override public Object getColonyIdAccessor()      { return colonyId; }
    @Override public Object getAnimalIdAccessor()      { return managedAnimalId; }
    @Override public IAnimalData getAnimalData()       { return null; }
    @Override public IAnimalDataView getAnimalDataView() { return null; }
    @Override public void setAnimalData(final IAnimalData data) { /* TODO */ }

    public IColony getColony()
    {
        return IColonyManager.getInstance().getColonyByWorld(colonyId, worldObj);
    }

    // TODO: Port full navigation, colony handler, synced data from 1.21
}
