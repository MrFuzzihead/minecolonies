package com.minecolonies.core.tileentities;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildingextensions.plantation.IPlantationModule;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.tileentities.AbstractTileEntityPlantationField;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Implementation for plantation field tile entities.
 */
public class TileEntityPlantationField extends AbstractTileEntityPlantationField
{
    /**
     * Cached result for {@link TileEntityPlantationField#getWorkingPositions(String)}.
     */
    private final Map<String, List<int[]>> workingPositions = new HashMap<>();

    /**
     * The schematic name of the placeholder block.
     */
    private String schematicName = "";

    /**
     * The schematic path of the placeholder block.
     */
    private String schematicPath = "";

    /**
     * The packName it is included in.
     */
    private String packName = "";

    /**
     * Corner positions as [x, y, z] int arrays.
     */
    private int[] corner1 = new int[]{0, 0, 0};
    private int[] corner2 = new int[]{0, 0, 0};

    /**
     * Rotation (0-3).
     */
    private int rotation = 0;

    /**
     * Mirror flag.
     */
    private boolean mirrored = false;

    /**
     * Map of block positions (encoded as long[]) relative to TE pos and string tags.
     */
    private Map<long[], List<String>> tagPosMap = new HashMap<>();

    /**
     * The colony this plantation field is located in.
     */
    private IColony currentColony;

    /**
     * Cached result for {@link TileEntityPlantationField#getPlantationFieldTypes()}.
     */
    private Set<BuildingExtensionEntry> plantationFieldTypes;

    /**
     * NBTBase name pos map cache.
     */
    private Map<String, Set<long[]>> worldTagMapCache = null;

    /**
     * Default constructor.
     */
    public TileEntityPlantationField()
    {
        super();
    }

    @Override
    public Set<BuildingExtensionEntry> getPlantationFieldTypes()
    {
        if (plantationFieldTypes == null)
        {
            plantationFieldTypes = tagPosMap.values().stream()
                                     .flatMap(Collection::stream)
                                     .map(this::getPlantationFieldEntryFromFieldTag)
                                     .filter(Objects::nonNull)
                                     .collect(Collectors.toSet());
        }
        return plantationFieldTypes;
    }

    @Override
    public List<int[]> getWorkingPositions(final String NBTBase)
    {
        workingPositions.computeIfAbsent(NBTBase, newTag -> tagPosMap.entrySet().stream()
                                                          .filter(f -> f.getValue().contains(newTag))
                                                          .distinct()
                                                          .map(Map.Entry::getKey)
                                                          // TODO: long[] keys â€” decode + offset relative to TE pos
                                                          .map(k -> new int[]{xCoord, yCoord, zCoord})
                                                          .collect(Collectors.toList()));
        return workingPositions.get(NBTBase);
    }

    @Override
    public IColony getCurrentColony()
    {
        if (currentColony == null && worldObj != null)
        {
            this.currentColony = IColonyManager.getInstance().getIColony(worldObj, xCoord, yCoord, zCoord);
        }
        return currentColony;
    }

    @Override
    @Nullable
    public Integer getDimension()
    {
        final IColony colony = getCurrentColony();
        if (colony != null)
        {
            return colony.getDimension();
        }
        return null;
    }

    @Override
    public int getRotation()
    {
        return this.rotation;
    }

    @Override
    public boolean getMirror()
    {
        return this.mirrored;
    }

    private BuildingExtensionEntry getPlantationFieldEntryFromFieldTag(final String fieldTag)
    {
        return BuildingExtensionRegistries.getBuildingExtensionRegistry().getValues().stream()
                 .filter(fieldEntry -> {
                     final List<IPlantationModule> modules = fieldEntry.getExtensionModuleProducers().stream()
                                                               .map(m -> m.apply(null))
                                                               .filter(IPlantationModule.class::isInstance)
                                                               .map(m -> (IPlantationModule) m)
                                                               .collect(Collectors.toList());
                     return modules.stream().anyMatch(module -> module.getFieldTag().equals(fieldTag));
                 })
                 .findFirst()
                 .orElse(null);
    }

    // â”€â”€â”€ IBlueprintDataProviderBE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public String getSchematicName() { return schematicName; }

    @Override
    public void setSchematicName(final String s)
    {
        this.schematicName = s;
        markDirty();
    }

    @Override
    public Map<long[], List<String>> getPositionedTags() { return tagPosMap; }

    @Override
    public void setPositionedTags(final Map<long[], List<String>> positionedTags)
    {
        tagPosMap = positionedTags;
        worldTagMapCache = null;
        markDirty();
    }

    @Override
    public Map<String, Set<long[]>> getWorldTagNamePosMap()
    {
        if (worldTagMapCache == null)
        {
            worldTagMapCache = IBlueprintDataProviderBE.super.getWorldTagNamePosMap();
        }
        return worldTagMapCache;
    }

    @Override
    public Tuple<int[], int[]> getSchematicCorners()
    {
        if ((corner1[0] == 0 && corner1[1] == 0 && corner1[2] == 0)
              || (corner2[0] == 0 && corner2[1] == 0 && corner2[2] == 0))
        {
            return new Tuple<>(new int[]{xCoord, yCoord, zCoord}, new int[]{xCoord, yCoord, zCoord});
        }
        return new Tuple<>(corner1, corner2);
    }

    @Override
    public void setSchematicCorners(final int[] pos1, final int[] pos2)
    {
        corner1 = pos1;
        corner2 = pos2;
        markDirty();
    }

    // â”€â”€â”€ NBT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readSchematicDataFromNBT(compound);
        this.rotation = compound.getInteger(TAG_ROTATION);
        this.mirrored = compound.getBoolean(TAG_MIRROR);
        if (compound.hasKey(TAG_PATH))
        {
            this.schematicPath = compound.getString(TAG_PATH);
        }
        if (compound.hasKey(TAG_NAME))
        {
            this.schematicName = compound.getString(TAG_NAME);
            if (this.schematicPath == null || this.schematicPath.isEmpty())
            {
                this.schematicPath = this.schematicName;
                this.schematicName = "";
            }
        }
        this.packName = compound.getString(TAG_PACK);

        if (!this.schematicPath.endsWith(".blueprint"))
        {
            this.schematicPath = this.schematicPath + ".blueprint";
        }
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        writeSchematicDataToNBT(compound);
        compound.setInteger(TAG_ROTATION, this.rotation);
        compound.setBoolean(TAG_MIRROR, this.mirrored);
        compound.setString(TAG_NAME, schematicName == null ? "" : schematicName);
        compound.setString(TAG_PATH, schematicPath == null ? "" : schematicPath);
        compound.setString(TAG_PACK, (packName == null || packName.isEmpty()) ? "" : packName);
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public String getBlueprintPath() { return schematicPath; }

    public void setBlueprintPath(final String filePath)
    {
        this.schematicPath = filePath;
        if (!this.schematicPath.endsWith(".blueprint"))
        {
            this.schematicPath = this.schematicPath + ".blueprint";
        }
        markDirty();
    }

    public String getPackName() { return packName; }

    public void setPackName(final String packName)
    {
        this.packName = packName;
        markDirty();
    }
}

