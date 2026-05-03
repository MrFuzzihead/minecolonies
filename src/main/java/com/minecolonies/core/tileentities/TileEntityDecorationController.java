package com.minecolonies.core.tileentities;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import com.minecolonies.api.util.Tuple;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * TileEntity for the decoration placeholder block.
 */
public class TileEntityDecorationController extends TileEntity implements IBlueprintDataProviderBE
{
    /** The schematic name. */
    private String schematicName = "";

    /** The schematic path. */
    private String schematicPath = "";

    /** The packName. */
    private String packName = "";

    /** Corner positions as [x, y, z] int arrays. */
    private int[] corner1 = new int[]{0, 0, 0};
    private int[] corner2 = new int[]{0, 0, 0};

    /** Rotation (0-3). */
    private int cachedRotation = -1;

    /** Mirror flag. */
    private boolean isMirrored = false;

    /** Map of block positions relative to TE pos and string tags (encoded as long[] keys). */
    private Map<long[], List<String>> tagPosMap = new HashMap<>();

    /** NBTBase map cache. */
    private Map<String, Set<long[]>> worldTagMapCache = null;

    public TileEntityDecorationController()
    {
        super();
    }

    // â”€â”€â”€ IBlueprintDataProviderBE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public String getSchematicName() { return schematicName; }

    @Override
    public void setSchematicName(final String name) { this.schematicName = name; }

    @Override
    public Map<long[], List<String>> getPositionedTags() { return tagPosMap; }

    @Override
    public void setPositionedTags(final Map<long[], List<String>> positionedTags)
    {
        this.tagPosMap = positionedTags;
        this.worldTagMapCache = null;
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
        return new Tuple<>(corner1, corner2);
    }

    @Override
    public void setSchematicCorners(final int[] pos1, final int[] pos2)
    {
        this.corner1 = pos1;
        this.corner2 = pos2;
        markDirty();
    }

    // â”€â”€â”€ NBT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setString(TAG_SCHEMATIC_NAME, schematicName);
        compound.setString(TAG_PATH, schematicPath);
        compound.setString(TAG_PACK, packName);
        compound.setInteger("rotation", cachedRotation);
        compound.setBoolean("mirrored", isMirrored);
        writeSchematicDataToNBT(compound);
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        schematicName = compound.getString(TAG_SCHEMATIC_NAME);
        schematicPath = compound.getString(TAG_PATH);
        packName = compound.getString(TAG_PACK);
        cachedRotation = compound.getInteger("rotation");
        isMirrored = compound.getBoolean("mirrored");
        readSchematicDataFromNBT(compound);
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }

    // â”€â”€â”€ Getters / Setters â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String getSchematicPath() { return schematicPath; }
    public void setSchematicPath(final String path) { this.schematicPath = path; }

    public String getPackName() { return packName; }
    public void setPackName(final String name) { this.packName = name; }

    public int getCachedRotation() { return cachedRotation; }
    public void setCachedRotation(final int rotation) { this.cachedRotation = rotation; }

    public boolean isMirrored() { return isMirrored; }
    public void setMirrored(final boolean mirrored) { this.isMirrored = mirrored; }
}

