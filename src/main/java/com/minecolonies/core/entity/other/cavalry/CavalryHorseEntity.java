package com.minecolonies.core.entity.other.cavalry;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.managers.interfaces.IManagedAnimal;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.entity.mobs.AnimalColonyHandler;
import com.minecolonies.core.entity.mobs.IAnimalColonyHandler;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
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
    private IAnimalColonyHandler colonyHandler;
    private AbstractAdvancedPathNavigate newNavigator;

    public CavalryHorseEntity(final World world)
    {
        super(world);
        this.setTame(true);
        this.setHorseSaddled(true);
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
    @Override public int getColonyId()           { return colonyId; }
    @Override public int getManagedAnimalId()    { return managedAnimalId; }
    @Override public void setManagedAnimalId(final int id) { this.managedAnimalId = id; }
    @Override public IColony getColony()
    {
        return IColonyManager.getInstance().getColonyByWorld(colonyId, worldObj);
    }

    // TODO: Port full navigation, colony handler, synced data from 1.21
}
