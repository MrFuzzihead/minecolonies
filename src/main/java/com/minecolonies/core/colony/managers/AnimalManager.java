package com.minecolonies.core.colony.managers;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import com.minecolonies.api.colony.IAnimalData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.managers.interfaces.IAnimalManager;
import com.minecolonies.api.colony.managers.interfaces.IManagedAnimal;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.AnimalData;
import com.minecolonies.core.network.messages.client.colony.ColonyViewAnimalViewDataMessage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed

public class AnimalManager implements IAnimalManager
{
    /**
     * NBT Tags
     */
    public static String TAG_ANIMAL_MANAGER = "animalManager";
    public static String TAG_ANIMALS        = "animals";
    public static String TAG_NEXTID         = "nextID";

    /**
     * Map with animal ID and data
     */
    private Map<Integer, IAnimalData> animalMap = new HashMap<>();

    /**
     * Whether this manager is dirty and needs re-serialize
     */
    private boolean isDirty = false;

    /**
     * The colony of the manager.
     */
    private final IColony colony;

    /**
     * The next free ID
     */
    private int nextAnimalID = -1;

    public AnimalManager(final IColony colony)
    {
        this.colony = colony;
    }

    /**
     * Registers a managed animal with the colony.
     * If the animal ID is 0 or the animal data associated with the ID is null, the animal is removed from the world.
     * If the animal UUID does not match the UUID of the data, the animal is removed from the world.
     * If an existing managed animal with the same ID is found, the existing animal is removed from the world if it is alive.
     * If the animal is not added to the world, it is removed from the world and a warn message is logged.
     * @param animal the managed animal to register
     */
    @Override
    public void registerAnimal(final IManagedAnimal <? extends Animal> animal)
    {
        Entity animalEntity = animal.getEntity();

        if (animal.getManagedAnimalId() == 0 || animalMap.get(animal.getManagedAnimalId()) == null)
        {
            if (!animalEntity.isAddedToWorld())
            {
                Log.getLogger().warn("Discarding entity not added to world, should be only called after:", new Exception());
            }

            animalEntity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        final IAnimalData data = animalMap.get(animal.getManagedAnimalId());

        if (data == null || !animalEntity.getUUID().equals(data.getUUID()))
        {
            if (!animalEntity.isAddedToWorld())
            {
                Log.getLogger().warn("Discarding entity not added to world, should be only called after:", new Exception());
            }
            animalEntity.remove(Entity.RemovalReason.DISCARDED);
            return;
        }

        final Optional<IManagedAnimal <? extends Animal>> existingManagedAnimal = data.getManagedAnimal();

        if (!existingManagedAnimal.isPresent())
        {
            data.setManagedAnimal(animal);
            animal.setAnimalData(data);
            return;
        }

        if (existingManagedAnimal.get() == animal)
        {
            return;
        }

        if (animalEntity.isAlive())
        {
            existingManagedAnimal.get().getEntity().remove(Entity.RemovalReason.DISCARDED);
            data.setManagedAnimal(animal);
            animal.setAnimalData(data);
            return;
        }

        if (!animalEntity.isAddedToWorld())
        {
            Log.getLogger().warn("Discarding entity not added to world, should be only called after:", new Exception());
        }

        animalEntity.remove(Entity.RemovalReason.DISCARDED);
    }

    /**
     * Gets a list of all animal data managed by this animal manager.
     * 
     * @return a list of all animal data managed by this animal manager.
     */
    public List<IAnimalData> getAnimals()
    {
        return new ArrayList<>(animalMap.values());
    }

    /**
     * Get the current amount of citizens, might be bigger then {@link #getMaxCitizens()}
     *
     * @return The current amount of citizens in the colony.
     */
    @Override
    public int getCurrentAnimalCount()
    {
        return animalMap.size();
    }

    /**
     * Creates and registers a new animal data.
     * This method also ensures that IDs are getting reused.
     * That's needed to prevent bugs when calling IDs that are not used.
     *
     * @return The newly created animal data.
     */
    @Override
    public IAnimalData createAndRegisterAnimalData(final IManagedAnimal<? extends Animal> managedAnimal)
    {

        if (colony == null)
        {
            Log.getLogger().warn("Missing colony while attempting to create and register managed animal data. This should not happen - report to devs." + this, new Exception());
            return null;
        }

        if (this.getCurrentAnimalCount() == 0)
        {
            nextAnimalID = 1;
        }
        else
        {
            //This ensures that IDs are getting reused.
            //That's needed to prevent bugs when calling IDs that are not used.
            for (int i = 1; i <= this.getCurrentAnimalCount() + 1; i++)
            {
                if (this.getAnimal(i) == null)
                {
                    nextAnimalID = i;
                    break;
                }
            }
        }

        final AnimalData animalData = new AnimalData(nextAnimalID, colony);
        animalMap.put(animalData.getId(), animalData);

        // Create all necessary associations between the animal data and the managed animal
        managedAnimal.setManagedAnimalId(animalData.getId());
        managedAnimal.getEntity().getEntityData().set(managedAnimal.getColonyIdAccessor(), colony.getID());
        animalData.setManagedAnimal(managedAnimal);

        return animalData;
    }

    /**
     * Get the animal data by ID.
     *
     * @param id The animal ID.
     * @return The animal data, or null if not found.
     */
    @Override
    public IAnimalData getAnimal(final int id)
    {
        return animalMap.get(id);
    }

    /**
     * Read the animal data from nbt.
     *
     * @param compound the compound to read it from.
     */
    @Override
    public void read(@NotNull final NBTTagCompound compound)
    {
        // If the NBTBase doesn't exist, don't mutate current state.
        if (!compound.contains(TAG_ANIMAL_MANAGER, NBTBase.TAG_COMPOUND))
        {
            return;
        }

        final NBTTagCompound animalManagerNBT = compound.getCompoundTag(TAG_ANIMAL_MANAGER);

        // Start from a clean slate so reloads don't accumulate stale entries.
        animalMap.clear();

        // Restore next id even if animals list is absent/corrupt.
        if (animalManagerNBT.contains(TAG_NEXTID, NBTBase.TAG_INT))
        {
            nextAnimalID = animalManagerNBT.getInt(TAG_NEXTID);
        }
        else
        {
            nextAnimalID = 1;
        }

        // Read animals list (if present). If missing, we still keep nextAnimalID restored.
        if (animalManagerNBT.contains(TAG_ANIMALS, NBTBase.TAG_LIST))
        {
            final NBTTagList animalList = animalManagerNBT.getTagList(TAG_ANIMALS, NBTBase.TAG_COMPOUND);

            for (int i = 0; i < animalList.size(); i++)
            {
                final NBTTagCompound animalTag = animalList.getCompoundTagAt(i);

                try
                {
                    final IAnimalData data = AnimalData.loadAnimalFromNBT(colony, animalTag);
                    if (data == null)
                    {
                        Log.getLogger().warn("Skipped null animal data at index {} while reading animal manager.", i);
                        continue;
                    }

                    final int id = data.getId();
                    final IAnimalData prev = animalMap.put(id, data);
                    if (prev != null)
                    {
                        Log.getLogger().warn("Duplicate animal id {} while reading animal manager; overwrote previous entry.", id);
                    }
                }
                catch (final Exception e)
                {
                    // Don't abort the entire load on one bad entry.
                    Log.getLogger().error("Failed to load animal data at index {} in animal manager; skipping entry.", i, e);
                }
            }
        }
        else
        {
            Log.getLogger().warn("No animals list while reading animal manager; skipping.");
        }
    }

    /**
     * Write the animal information to nbt.
     *
     * @param compoundNBT the compound to write it to.
     * @throws UnsupportedOperationException if not implemented.
     */
    @Override
    public void write(@NotNull NBTTagCompound compoundNBT)
    {
        final NBTTagCompound animalManagerNBT = new NBTTagCompound();

        final NBTTagList animalList = new NBTTagList();
        for (Map.Entry<Integer, IAnimalData> entry : animalMap.entrySet())
        {
            animalList.add(entry.getValue().serializeNBT());
        }

        animalManagerNBT.put(TAG_ANIMALS, animalList);
        animalManagerNBT.putInt(TAG_NEXTID, nextAnimalID);
        compoundNBT.put(TAG_ANIMAL_MANAGER, animalManagerNBT);
    }

    @Override
    public void onColonyTick(IColony colony)
    {
        // No-Op scaffolding for future tick-based animal management.
    }

    @Override
    public boolean tickAnimalData(final int tickRate)
    {
        for (IAnimalData animalData : this.getAnimals())
        {
            animalData.update(tickRate);
        }
        return false;
    }

    /**
     * Marks the Animal Manager as dirty, which will cause it to update and save to disk when the colony is saved.
     */
    @Override
    public void markDirty()
    {
        this.isDirty = true;
    }

    /**
     * Clear the dirty flag of the Animal Manager, which will prevent it from re-saving to disk when the colony is saved.
     */
    @Override
    public void clearDirty()
    {
        this.isDirty = false;
    }

    /**
     * Send the necessary packets to subscribers.
     *
     * @param closeSubscribers players that were subscribed but are now out of range
     * @param newSubscribers   players that have just come into range and need data
     */
    @Override
    public void sendPackets(@NotNull final Set<EntityPlayerMP> closeSubscribers, @NotNull final Set<EntityPlayerMP> newSubscribers)
    {
        Set<IAnimalData> toSend = null;
        boolean refresh = !newSubscribers.isEmpty() || this.isDirty;

        if (refresh)
        {
            toSend = new HashSet<>(animalMap.values());
            for (final IAnimalData data : animalMap.values())
            {
                data.clearDirty();
            }
            this.clearDirty();
        }
        else
        {
            for (final IAnimalData data : animalMap.values())
            {
                if (data.isDirty())
                {
                    if (toSend == null)
                    {
                        toSend = new HashSet<>();
                    }

                    toSend.add(data);
                }
                data.clearDirty();
            }
        }

        if (toSend == null || toSend.isEmpty())
        {
            return;
        }

        Set<EntityPlayerMP> players = new HashSet<>(newSubscribers);
        players.addAll(closeSubscribers);

        final ColonyViewAnimalViewDataMessage message = new ColonyViewAnimalViewDataMessage(colony, toSend, refresh);

        for (final EntityPlayerMP player : players)
        {
            Network.getNetwork().sendToPlayer(message, player);
        }
    }
}





