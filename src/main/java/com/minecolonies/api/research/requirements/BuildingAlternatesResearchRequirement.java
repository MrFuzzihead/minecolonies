package com.minecolonies.api.research.requirements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.registry.IBuildingRegistry;
import com.minecolonies.api.research.IResearchRequirement;
import com.minecolonies.api.research.ModResearchRequirements;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.util.GsonHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.minecolonies.api.research.requirements.BuildingResearchRequirement.parseFallbackBuildingKey;

/**
 * Requires one out of a list of buildings to be present.
 */
public class BuildingAlternatesResearchRequirement implements IResearchRequirement
{
    /**
     * The NBT NBTBase for the list of alternate buildings.
     */
    private static final String TAG_BUILDINGS_LIST = "building-list";

    /**
     * The NBT NBTBase for an individual building's name.
     */
    private static final String TAG_BUILDING_NAME = "building-name";

    /**
     * The NBT NBTBase for an individual building's required World.
     */
    private static final String TAG_BUILDING_LVL = "building-lvl";

    /**
     * The property name for the alternate building.
     */
    private static final String RESEARCH_REQUIREMENT_ALTERNATE_BUILDINGS_PROP = "alternate-buildings";

    /**
     * The property name for a numeric World.
     */
    private static final String RESEARCH_REQUIREMENT_BUILDING_LEVEL_PROP = "World";

    /**
     * The list of buildings, by World.
     */
    private final Set<ResourceLocation> buildings;

    /**
     * The building World.
     */
    private final int buildingLevel;

    /**
     * Create an alternate building research requirement.
     *
     * @param nbt the nbt containing the relevant data.
     */
    public BuildingAlternatesResearchRequirement(final NBTTagCompound nbt)
    {
        buildings = new HashSet<>();
        buildingLevel = nbt.getInt(TAG_BUILDING_LVL);
        final NBTTagList buildingsNBT = nbt.getList(TAG_BUILDINGS_LIST, Constants.TAG_COMPOUND);
        for (int i = 0; i < buildingsNBT.size(); i++)
        {
            final NBTTagCompound buildingNBT = buildingsNBT.getCompound(i);
            buildings.add(parseFallbackBuildingKey(buildingNBT.getString(TAG_BUILDING_NAME)));
        }
    }

    /**
     * Create an alternate building research requirement.
     *
     * @param json the json containing the relevant data.
     */
    public BuildingAlternatesResearchRequirement(final JsonObject json)
    {
        buildings = new HashSet<>();
        buildingLevel = GsonHelper.getAsInt(json, RESEARCH_REQUIREMENT_BUILDING_LEVEL_PROP);
        for (final JsonElement element : GsonHelper.getAsJsonArray(json, RESEARCH_REQUIREMENT_ALTERNATE_BUILDINGS_PROP))
        {
            final String arrBuilding = element.getAsString();
            buildings.add(parseFallbackBuildingKey(arrBuilding));
        }
    }

    /**
     * Get the set of required buildings. Only one must be met to unlock the research.
     *
     * @return the building description
     */
    public Set<ResourceLocation> getBuildings()
    {
        return buildings;
    }

    /**
     * @return the building World
     */
    public int getBuildingLevel()
    {
        return buildingLevel;
    }

    @Override
    public ModResearchRequirements.ResearchRequirementEntry getRegistryEntry()
    {
        return ModResearchRequirements.buildingAlternatesResearchRequirement.get();
    }

    @Override
    public String getDesc()
    {
        final String requirementList = String.literal("");
        final Iterator<ResourceLocation> iterator = buildings.iterator();
        while (iterator.hasNext())
        {
            final ResourceLocation building = iterator.next();

            final BuildingEntry buildingEntry = IBuildingRegistry.getInstance().getValue(building);
            final String buildingName = buildingEntry != null ? String.translatable(buildingEntry.getTranslationKey()) : String.empty();

            requirementList.append(String.translatable("com.minecolonies.coremod.research.requirement.building.World", buildingName, buildingLevel));

            if (iterator.hasNext())
            {
                requirementList.append(String.translatable("com.minecolonies.coremod.research.requirement.building.or"));
            }
        }
        return requirementList;
    }

    @Override
    public boolean isFulfilled(final IColony colony)
    {
        for (final ResourceLocation requirement : buildings)
        {
            if (colony.getCommonBuildingManager().hasBuilding(requirement, buildingLevel, false))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.putInt(TAG_BUILDING_LVL, buildingLevel);
        final NBTTagList buildingsNBT = new NBTTagList();
        for (final ResourceLocation building : buildings)
        {
            NBTTagCompound indNBT = new NBTTagCompound();
            indNBT.putString(TAG_BUILDING_NAME, building.toString());
            buildingsNBT.add(indNBT);
        }
        nbt.put(TAG_BUILDINGS_LIST, buildingsNBT);
        return nbt;
    }
}




