package com.minecolonies.core.colony.managers;

import com.google.common.collect.Maps;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.managers.interfaces.ITravellingManager;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.core.util.TeleportHelper;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
// [1.7.10] INBTSerializable -> manual read/write

import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the travelling system and its API.
 * Keeps track of the travelling destination and progress of a given party.
 * <p>
 * Does not handle any interactions with the entities or the citizens.
 * </p>
 */
public class TravellingManager implements ITravellingManager
{

    private final IColony                    colony;
    private final Map<Integer, TravelerData> travelerDataMap = Maps.newHashMap();

    public TravellingManager(final IColony colony) {this.colony = colony;}

    @Override
    public boolean isTravelling(final int citizenId)
    {
        return Optional.ofNullable(travelerDataMap.get(citizenId)).map(TravelerData::isTraveling).orElse(false);
    }

    @Override
    public Optional<int[]> getTravellingTargetFor(final int citizenId)
    {
        return Optional.ofNullable(travelerDataMap.get(citizenId)).map(TravelerData::getTarget);
    }

    @Override
    public void startTravellingTo(final int citizenId, final int[] target, final int travelTimeInTicks)
    {
        travelerDataMap.put(citizenId, new TravelerData(citizenId, target, travelTimeInTicks));
        colony.markDirty();
    }

    @Override
    public void finishTravellingFor(final int citizenId)
    {
        if (travelerDataMap.containsKey(citizenId))
        {
            travelerDataMap.remove(citizenId);
            colony.markDirty();
        }
    }

    @Override
    public void recallAllTravellingCitizens()
    {
        for (final Integer citizenId : travelerDataMap.keySet())
        {
            final ICitizenData citizenData = this.colony.getCitizenManager().getCivilian(citizenId);
            final int[] spawnHutPos;
            if (citizenData.getWorkBuilding() != null)
            {
                spawnHutPos = citizenData.getWorkBuilding().getPosition();
            }
            else
            {
                spawnHutPos = colony.getServerBuildingManager().getTownHall().getPosition();
            }

            Optional<AbstractEntityCitizen> optionalEntityCitizen = citizenData.getEntity();
            if (optionalEntityCitizen.isEmpty())
            {
                Log.getLogger().warn(String.format("The traveller %d from colony #%d has returned very confused!", citizenData.getId(), colony.getID()));
                citizenData.setNextRespawnPosition(EntityUtils.getSpawnPoint(colony.getWorld(), spawnHutPos));
                citizenData.updateEntityIfNecessary();
                optionalEntityCitizen = citizenData.getEntity();
            }

            optionalEntityCitizen.ifPresent(abstractEntityCitizen -> TeleportHelper.teleportCitizen(abstractEntityCitizen, colony.getWorld(), spawnHutPos));
        }

        this.travelerDataMap.clear();
        colony.markDirty();
    }

    public boolean onTick()
    {
        travelerDataMap.values().forEach(TravelerData::onTick);
        return true;
    }

    // [1.7.10] not @Override - ITravellingManager has no serializeNBT
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound data = new NBTTagCompound();
        final NBTTagList output = new NBTTagList();

        for (TravelerData travelerData : travelerDataMap.values())
        {
            NBTTagCompound serializeNBT = travelerData.serializeNBT();
            output.appendTag(serializeNBT); // [1.7.10] add -> appendTag
        }

        data.setTag(NbtTagConstants.TRAVELER_DATA, output); // [1.7.10] put -> setTag

        return data;
    }

    // [1.7.10] not @Override - ITravellingManager has no deserializeNBT
    public void deserializeNBT(final NBTTagCompound nbt)
    {
        final NBTTagList travelerData = nbt.getTagList(NbtTagConstants.TRAVELER_DATA, 10); // [1.7.10] NBTBase.TAG_COMPOUND -> 10
        travelerDataMap.clear();

        for (int i = 0; i < travelerData.tagCount(); i++) // [1.7.10] not iterable; use indexed for
        {
            final NBTTagCompound nbtCompound = travelerData.getCompoundTagAt(i);
            TravelerData data = new TravelerData(nbtCompound);
            travelerDataMap.put(data.getCitizenId(), data);
        }
    }

    private static final class TravelerData
    {
        private int      citizenId           = -1;
        private int[] target              = new int[]{0,0,0};
        private int      initialTravelTime   = 0;
        private int      remainingTravelTime = 0;

        public TravelerData(final int citizenId, final int[] target, final int initialTravelTime)
        {
            this.citizenId = citizenId;
            this.target = target;
            this.initialTravelTime = initialTravelTime;
            this.remainingTravelTime = initialTravelTime;
        }

        public TravelerData(final NBTTagCompound NBTBase)
        {
            this.deserializeNBT(NBTBase);
        }

        public void onTick()
        {
            if (remainingTravelTime > 0)
            {
                remainingTravelTime -= ColonyConstants.UPDATE_TRAVELING_INTERVAL;
                remainingTravelTime = Math.max(0, remainingTravelTime);
            }
        }

        public boolean hasReachedTarget()
        {
            return remainingTravelTime == 0;
        }

        public double getTravelPercentage()
        {
            return ((double) remainingTravelTime * 100) / (double) initialTravelTime;
        }

        public int getCitizenId()
        {
            return citizenId;
        }

        public int[] getTarget()
        {
            return target;
        }

        public int getInitialTravelTime()
        {
            return initialTravelTime;
        }

        public int getRemainingTravelTime()
        {
            return remainingTravelTime;
        }

        public boolean isTraveling()
        {
            return !hasReachedTarget();
        }

        // [1.7.10] not @Override - TravelerData has no supertype with serializeNBT
        public NBTTagCompound serializeNBT()
        {
            final NBTTagCompound data = new NBTTagCompound();
            data.setInteger(NbtTagConstants.TAG_CITIZEN, citizenId); // [1.7.10] putInt -> setInteger
            data.setInteger(NbtTagConstants.TAG_TARGET + "_x", target[0]); // [1.7.10] NbtUtils.writeBlockPos not available
            data.setInteger(NbtTagConstants.TAG_TARGET + "_y", target[1]);
            data.setInteger(NbtTagConstants.TAG_TARGET + "_z", target[2]);
            data.setInteger(NbtTagConstants.TAG_INITIAL_TRAVEL_TIME, initialTravelTime); // [1.7.10] putInt -> setInteger
            data.setInteger(NbtTagConstants.TAG_REMAINING_TRAVEL_TIME, remainingTravelTime); // [1.7.10] putInt -> setInteger
            return data;
        }

        // [1.7.10] not @Override - TravelerData has no supertype with deserializeNBT
        public void deserializeNBT(final NBTTagCompound nbt)
        {
            this.citizenId = nbt.getInteger(NbtTagConstants.TAG_CITIZEN); // [1.7.10] getInt -> getInteger
            this.target = new int[]{ // [1.7.10] NbtUtils.readBlockPos not available
                nbt.getInteger(NbtTagConstants.TAG_TARGET + "_x"),
                nbt.getInteger(NbtTagConstants.TAG_TARGET + "_y"),
                nbt.getInteger(NbtTagConstants.TAG_TARGET + "_z")
            };
            this.initialTravelTime = nbt.getInteger(NbtTagConstants.TAG_INITIAL_TRAVEL_TIME); // [1.7.10] getInt -> getInteger
            this.remainingTravelTime = nbt.getInteger(NbtTagConstants.TAG_REMAINING_TRAVEL_TIME); // [1.7.10] getInt -> getInteger
        }
    }
}





