package com.minecolonies.api.tileentities;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.ldtteam.structurize.storage.StructurePackMeta;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IBuildingContainer;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.InventoryFunctions;
import com.minecolonies.core.tileentities.TileEntityRack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;

public abstract class AbstractTileEntityColonyBuilding extends TileEntityRack // [1.7.10] IBlueprintDataProviderBE uses modern BlockPos; not implementing
{
    /**
     * Version of the TE data.
     */
    private static final String TAG_VERSION = "version";
    private static final int    VERSION     = 2;

    /**
     * Corner positions of schematic, relative to te pos (as [x, y, z] int arrays).
     */
    private int[] corner1 = new int[]{0, 0, 0};
    private int[] corner2 = new int[]{0, 0, 0};

    /**
     * The TE's schematic name.
     */
    private String schematicName = "";

    /**
     * Map of block positions (encoded as long chunkPosAsLong) relative to TE pos and string tags.
     */
    private Map<long[], List<String>> tagPosMap = new HashMap<>();

    /**
     * Check if the building might have old data.
     */
    private int version = 0;

    /**
     * NBTBase map cache.
     */
    private Map<String, Set<long[]>> worldTagMapCache = null;

    /**
     * List based NBTBase map cache.
     */
    private Map<String, List<long[]>> worldTagMapCacheWithList;

    public AbstractTileEntityColonyBuilding()
    {
        super();
    }

    /**
     * Finds the first matching ItemStack in the entity. It will be taken from the chest and placed in the worker inventory.
     *
     * @param entity                      the tileEntity chest or building.
     * @param itemStackSelectionPredicate the itemStack predicate.
     * @return true if found the stack.
     */
    public static boolean isInTileEntity(final IInventory entity, @NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        return InventoryFunctions.matchFirstInProvider(entity, itemStackSelectionPredicate);
    }

    /**
     * Returns the colony ID.
     *
     * @return ID of the colony.
     */
    public abstract int getColonyId();

    /**
     * Returns the colony of the tile entity.
     *
     * @return Colony of the tile entity.
     */
    public abstract IColony getColony();

    /**
     * Sets the colony of the tile entity.
     *
     * @param c Colony to set in references.
     */
    public abstract void setColony(IColony c);

    /**
     * Returns the position of the tile entity as [x, y, z].
     *
     * @return position of the tile entity.
     */
    public abstract int[] getPosition();

    /**
     * Check for a certain item and return the position of the chest containing it as [x, y, z].
     *
     * @param itemStackSelectionPredicate the stack to search for.
     * @return the position or null.
     */
    @Nullable
    public abstract int[] getPositionOfChestWithItemStack(@NotNull Predicate<ItemStack> itemStackSelectionPredicate);

    /**
     * Returns the building associated with the tile entity.
     *
     * @return {@link IBuildingContainer} associated with the tile entity.
     */
    public abstract IBuilding getBuilding();

    /**
     * Sets the building associated with the tile entity.
     *
     * @param b {@link IBuildingContainer} to associate with the tile entity.
     */
    public abstract void setBuilding(IBuilding b);

    /**
     * Returns the view of the building associated with the tile entity.
     *
     * @return {@link IBuildingView} the tile entity is associated with.
     */
    public abstract IBuildingView getBuildingView();

    /**
     * Checks if the player has permission to access the hut.
     *
     * @param player Player to check permission of.
     * @return True when player has access, or building doesn't exist, otherwise false.
     */
    public abstract boolean hasAccessPermission(EntityPlayer player);

    /**
     * Set if the entity is mirrored.
     *
     * @param mirror true if so.
     */
    public abstract void setMirror(boolean mirror);

    /**
     * Check if building is mirrored.
     *
     * @return true if so.
     */
    public abstract boolean isMirrored();

    /**
     * Getter for the style.
     *
     * @return the pack of it.
     */
    public abstract StructurePackMeta getStructurePack();

    /**
     * Set the pack of the tileEntity.
     *
     * @param style the pack to set.
     */
    public abstract void setStructurePack(final StructurePackMeta style);

    /**
     * Set the blueprint path of the tileEntity.
     *
     * @param path the path to set.
     */
    public abstract void setBlueprintPath(final String path);

    /**
     * Get the blueprint path of the tileEntity.
     *
     * @return path the path to get.
     */
    public abstract String getBlueprintPath();

    /**
     * Get the building name that this {@link AbstractTileEntityColonyBuilding} belongs to.
     *
     * @return The buildings name.
     */
    public abstract ResourceLocation getBuildingName();

    // [1.7.10] getSchematicName/setSchematicName - regular methods (not overriding)
    public String getSchematicName()
    {
        return schematicName.replace(".blueprint", "");
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE
    public void setSchematicName(final String name)
    {
        schematicName = name;
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE; kept as regular method
    public Map<long[], List<String>> getPositionedTags()
    {
        return tagPosMap;
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE; kept as regular method
    public Map<String, Set<long[]>> getWorldTagNamePosMap()
    {
        if (worldTagMapCache == null)
        {
            // [1.7.10] IBlueprintDataProviderBE.super not available; compute inline
            final Map<String, Set<long[]>> result = new HashMap<>();
            for (final Map.Entry<long[], List<String>> entry : tagPosMap.entrySet())
            {
                for (final String tag : entry.getValue())
                {
                    result.computeIfAbsent(tag, k -> new java.util.HashSet<>()).add(entry.getKey());
                }
            }
            worldTagMapCache = result;
        }
        return worldTagMapCache;
    }

    /**
     * Get a list version of the positioned tags, mapped from NBTBase name to position.
     *
     * @return the list version.
     */
    public Map<String, List<long[]>> getCachedWorldTagNamePosMap()
    {
        if (worldTagMapCacheWithList == null)
        {
            final Map<String, Set<long[]>> worldTagNamePosMap = getWorldTagNamePosMap();
            worldTagMapCacheWithList = new HashMap<>();
            for (final Map.Entry<String, Set<long[]>> entry : worldTagNamePosMap.entrySet())
            {
                worldTagMapCacheWithList.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return worldTagMapCacheWithList;
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE; kept as regular method
    public void setPositionedTags(final Map<long[], List<String>> positionedTags)
    {
        tagPosMap = positionedTags;
        worldTagMapCache = null;
        worldTagMapCacheWithList = null;
        markDirty();
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE; kept as regular method
    public Tuple<int[], int[]> getSchematicCorners()
    {
        return new Tuple<>(corner1, corner2);
    }

    // [1.7.10] was @Override IBlueprintDataProviderBE; kept as regular method
    public void setSchematicCorners(final int[] pos1, final int[] pos2)
    {
        corner1 = pos1;
        corner2 = pos2;
        markDirty();
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readSchematicDataFromNBT(compound);
        this.version = compound.getInteger(TAG_VERSION);
    }

    // [1.7.10] readSchematicDataFromNBT - not @Override; was from IBlueprintDataProviderBE
    public void readSchematicDataFromNBT(final NBTTagCompound originalCompound)
    {
        final String old = getSchematicName();
        // [1.7.10] IBlueprintDataProviderBE.super.readSchematicDataFromNBT not available; call basic read
        if (originalCompound.hasKey("schematicName"))
        {
            schematicName = originalCompound.getString("schematicName");
        }

        if (worldObj == null || worldObj.isRemote || getColony() == null || getColony().getServerBuildingManager() == null)
        {
            return;
        }

        final IBuilding building = getColony().getServerBuildingManager().getBuilding(new int[]{xCoord, yCoord, zCoord});
        if (building != null)
        {
            building.onUpgradeSchematicTo(old, getSchematicName(), null); // [1.7.10] this no longer implements IBlueprintDataProviderBE
        }
        this.version = VERSION;
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        // [1.7.10] writeSchematicDataToNBT was from IBlueprintDataProviderBE; write schematicName manually
        compound.setString("schematicName", schematicName);
        compound.setInteger(TAG_VERSION, this.version);
    }

    /**
     * Get the tile entity position as [x, y, z].
     *
     * @return position.
     */
    public int[] getTilePos()
    {
        return new int[]{xCoord, yCoord, zCoord};
    }

    /**
     * Check if the TE is on an old data version.
     *
     * @return true if so.
     */
    public boolean isOutdated()
    {
        return version < VERSION;
    }
}

