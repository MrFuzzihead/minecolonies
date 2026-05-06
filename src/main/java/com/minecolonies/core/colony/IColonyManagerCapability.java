package com.minecolonies.core.colony;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.util.BackUpHelper;
// [1.7.10 BACKPORT] Removed:
//   net.minecraftforge.common.capabilities.Capability — no capabilities in 1.7.10
// Capability<> param removed from Storage methods; call-sites pass null.
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONIES;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_MANAGER;

/**
 * Capability for the colony NBTBase for chunks
 */
public interface IColonyManagerCapability
{
    /**
     * Create a colony and return it.
     *
     * @param w   the world the colony is in.
     * @param pos the position of the colony.
     * @return the created colony.
     */
    IColony createColony(@NotNull final World w, @NotNull final int[] pos);

    /**
     * Delete a colony with a certain id.
     *
     * @param id the id of the colony.
     */
    void deleteColony(final int id);

    /**
     * Get a colony with a certain id.
     *
     * @param id the id of the colony.
     * @return the colony or null.
     */
    @Nullable
    IColony getColony(final int id);

    /**
     * Get a list of all colonies.
     *
     * @return a complete list.
     */
    List<IColony> getColonies();

    /**
     * add a new colony to the capability.
     *
     * @param colony the colony to add.
     */
    void addColony(IColony colony);

    /**
     * Get the top most id of all colonies.
     *
     * @return the top most id.
     */
    int getTopID();

    /**
     * The implementation of the colonyTagCapability.
     */
    class Impl implements IColonyManagerCapability
    {
        /**
         * The list of all colonies.
         */
        @NotNull
        private final ColonyList<IColony> colonies = new ColonyList<>();

        @Override
        public IColony createColony(@NotNull final World w, @NotNull final int[] pos)
        {
            return colonies.create(w, pos);
        }

        @Override
        public void deleteColony(final int id)
        {
            colonies.remove(id);
        }

        @Override
        public IColony getColony(final int id)
        {
            return colonies.get(id);
        }

        @Override
        public List<IColony> getColonies()
        {
            return colonies.getCopyAsList();
        }

        @Override
        public void addColony(final IColony colony)
        {
            colonies.add(colony);
        }

        @Override
        public int getTopID()
        {
            return colonies.getTopID();
        }
    }

    /**
     * The storage class of the capability.
     *
     * <p><b>1.7.10 Backport:</b> {@code Capability&lt;&gt;} param removed from both methods.</p>
     */
    class Storage
    {

        /** @param capability ignored (was Capability&lt;IColonyManagerCapability&gt;) */
        public static NBTBase writeNBT(@SuppressWarnings("unused") final Object capability,
            @NotNull final IColonyManagerCapability instance,
            final boolean overworld)
        {
            final NBTTagCompound compound = new NBTTagCompound();

            final NBTTagList colonies = new NBTTagList();
            for (final IColony colony : instance.getColonies())
            {
                try
                {
                    colonies.add(colony.getColonyTag());
                }
                catch (Exception e)
                {
                    Log.getLogger()
                      .error("Colony: " + colony.getName() + " id:" + colony.getID() + " owner:" + colony.getPermissions().getOwnerName() + " could not be saved! Error:", e);
                }
            }

            compound.setTag(TAG_COLONIES, colonies);

            if (overworld)
            {
                final NBTTagCompound managerCompound = new NBTTagCompound();
                IColonyManager.getInstance().write(managerCompound);
                compound.setTag(TAG_COLONY_MANAGER, managerCompound);
            }
            return compound;
        }

        /** @param capability ignored (was Capability&lt;IColonyManagerCapability&gt;) */
        public static void readNBT(
            @SuppressWarnings("unused") final Object capability,
            @NotNull final IColonyManagerCapability instance,
            final boolean overworld,
            @NotNull final NBTBase nbt)
        {
            // Notify that we did load the cap for this world
            IColonyManager.getInstance().setCapLoaded();
            if (nbt instanceof NBTTagCompound)
            {
                final NBTTagCompound compound = (NBTTagCompound) nbt;

                if (!compound.contains(TAG_COLONIES))
                {
                    BackUpHelper.loadManagerBackup();
                    return;
                }

                if (overworld && !compound.contains(TAG_COLONY_MANAGER))
                {
                    BackUpHelper.loadManagerBackup();
                }

                // Load all colonies from Nbt
                Multimap<int[], IColony> tempColonies = ArrayListMultimap.create();
                for (final NBTBase NBTBase : compound.getTagList(TAG_COLONIES, NBTBase.TAG_COMPOUND))
                {
                    final IColony colony = Colony.loadColony((NBTTagCompound) NBTBase, null);
                    if (colony != null)
                    {
                        tempColonies.put(colony.getCenter(), colony);
                        instance.addColony(colony);
                    }
                }

                // Check colonies for duplicates causing issues.
                for (final int[] pos : tempColonies.keySet())
                {
                    // Check if any position has more than one colony
                    if (tempColonies.get(pos).size() > 1)
                    {
                        Log.getLogger().warn("Detected duplicate colonies which are at the same position:");
                        for (final IColony colony : tempColonies.get(pos))
                        {
                            Log.getLogger()
                              .warn(
                                "ID: " + colony.getID() + " name:" + colony.getName() + " citizens:" + colony.getCitizenManager().getCitizens().size() + " building count:" + colony
                                                                                                                                                                                .getServerBuildingManager()
                                                                                                                                                                                .getBuildings()
                                                                                                                                                                                .size());
                        }
                        Log.getLogger().warn("Check and remove all except one of the duplicated colonies above!");
                    }
                }

                if (compound.contains(TAG_COLONY_MANAGER) && overworld)
                {
                    IColonyManager.getInstance().read(compound.getCompoundTag(TAG_COLONY_MANAGER));
                }
            }
            else
            {
                BackUpHelper.loadManagerBackup();
            }
        }
    }
}




