package com.minecolonies.api.colony.managers.interfaces;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.colony.buildings.ICommonBuilding;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.minecolonies.api.util.MathUtils.RANDOM;

/**
 * Interface for the managers for registered structures.
 * Buildings + Extensions, Decorations, etc.
 */
public interface ICommonRegisteredStructureManager<B extends ICommonBuilding,T>
{
    /**
     * Get the townhall from the colony.
     *
     * @return the townhall building.
     */
    T getTownHall();

    /**
     * Check if the colony has a placed warehouse.
     *
     * @return true if so.
     */
    boolean hasWarehouse();

    /**
     * Get a certain building.
     *
     * @param pos the id of the building.
     * @return the building.
     */
    default B getBuilding(int[] pos)
    {
        return getBuildings().get(pos);
    }

    /**
     * Returns a map with all buildings within the colony. Key is ID (Coordinates), value is building object.
     *
     * @return Map with ID (coordinates) as key, and buildings as value.
     */
    @NotNull
    Map<int[], B> getBuildings();

    /**
     * Check if the colony has a placed townhall.
     *
     * @return true if so.
     */
    boolean hasTownHall();

    /**
     * Get building in Colony by ID. The building will be casted to the provided type.
     *
     * @param buildingId ID (coordinates) of the building to get.
     * @param type       Type of building.
     * @return the building with the specified id.
     */
    default @Nullable <BB extends B> BB getBuilding(final int[] buildingId, @NotNull final Class<BB> type)
    {
        try
        {
            return type.cast(getBuildings().get(buildingId));
        }
        catch (final ClassCastException e)
        {
            Log.getLogger().warn("getBuilding called with wrong type: ", e);
            return null;
        }
    }

    /**
     * Searches for the closest building to a given citizen.
     *
     * @param citizen  the citizen.
     * @param building the type of building.
     * @return the Position of it.
     */
    default int[] getBestBuilding(final AbstractEntityCitizen citizen, final Class<? extends B> building)
    {
        return getBestBuilding(new int[]{(int)citizen.posX, (int)citizen.posY, (int)citizen.posZ}, building);
    }

    /**
     * Searches for the closest building to a given citizen, with an additional filter predicate.
     *
     * @param citizen  the citizen.
     * @param building the type of building.
     * @param filter   the filter to match a building against to further specialize the needs.
     * @return the Position of it.
     */
    default <BB extends B> int[] getBestBuilding(final AbstractEntityCitizen citizen, final Class<BB> building, @NotNull final Predicate<BB> filter)
    {
        return getBestBuilding(new int[]{(int)citizen.posX, (int)citizen.posY, (int)citizen.posZ}, building, filter);
    }

    /**
     * Searches for the closest building to a given position.
     *
     * @param pos      the pos.
     * @param building the building class type.
     * @return the Position of it.
     */
    default int[] getBestBuilding(final int[] pos, final Class<? extends B> building)
    {
        return getBestBuilding(pos, building, b -> true);
    }

    /**
     * Searches for the closest building to a given position, with an additional filter predicate.
     *
     * @param pos      the pos.
     * @param building the building class type.
     * @param filter   the filter to match a building against to further specialize the needs.
     * @return the Position of it.
     */
    default <BB extends B> int[] getBestBuilding(final int[] pos, final Class<BB> building, @NotNull final Predicate<BB> filter)
    {
        double distance = Double.MAX_VALUE;
        int[] goodFit = null;
        for (final B currentBuilding : getBuildings().values())
        {
            if (building.isInstance(currentBuilding)
                && currentBuilding.getBuildingLevel() > 0
                && WorldUtil.isBlockLoaded(getColony().getWorld(), currentBuilding.getPosition()[0], currentBuilding.getPosition()[2])
                && filter.test((BB) currentBuilding))
            {
                final double localDistance = BlockPosUtil.distSqr(currentBuilding.getPosition(), pos[0], pos[1], pos[2]);
                if (localDistance < distance)
                {
                    distance = localDistance;
                    goodFit = currentBuilding.getPosition();
                }
            }
        }
        return goodFit;
    }

    /**
     * Returns a random building in the colony, matching the filter predicate.
     *
     * @param filterPredicate the filter to apply.
     * @return the random building. Returns null if no building matching the predicate was found.
     */
    default int[] getRandomBuilding(Predicate<B> filterPredicate)
    {
        final List<B> allowedBuildings = new ArrayList<>();
        for (final B building : getBuildings().values())
        {
            if (filterPredicate.test(building))
            {
                allowedBuildings.add(building);
            }
        }

        if (allowedBuildings.isEmpty())
        {
            return null;
        }

        return allowedBuildings.get(RANDOM.nextInt(allowedBuildings.size())).getPosition();
    }

    /**
     * Get the first building matching the conditions.
     *
     * @param predicate the predicate matching the building.
     * @return the building or null.
     */
    @Nullable
    default B getFirstBuildingMatching(final Predicate<B> predicate)
    {
        for (final B building : getBuildings().values())
        {
            if (predicate.test(building))
            {
                return building;
            }
        }
        return null;
    }

    /**
     * Check if a building matching a given resource location.
     * @param id the resource location to match.
     * @param World the World to match.
     * @param singleBuilding if one or more buildings can match it.
     * @return true if so.
     */
    default boolean hasBuilding(final ResourceLocation id, final int World, final boolean singleBuilding)
    {
        int sum = 0;
        for (final B building : getBuildings().values())
        {
            if (building.getBuildingType().getRegistryName().equals(id))
            {
                if (singleBuilding)
                {
                    if (building.getBuildingLevel() >= World)
                    {
                        return true;
                    }
                }
                else
                {
                    sum += building.getBuildingLevel();

                    if (sum >= World)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get all the building extensions.
     *
     * @param matcher the building extension matcher predicate.
     * @return an unmodifiable collection of all building extensions.
     */
    @NotNull List<IBuildingExtension> getBuildingExtensions(Predicate<IBuildingExtension> matcher);

    /**
     * Get the colony from the manager.
     * @return the IColony colony.
     */
    IColony getColony();
}


