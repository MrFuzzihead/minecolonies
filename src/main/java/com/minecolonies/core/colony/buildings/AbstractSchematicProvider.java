package com.minecolonies.core.colony.buildings;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.BlockInfo;
import com.ldtteam.structurize.util.RotationMirror;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.ISchematicProvider;
import com.minecolonies.api.colony.buildings.modules.IAltersBuildingFootprint;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.compatibility.newstruct.BlueprintMapping;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.FireworkUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.util.BuildingUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
import net.minecraft.world.World;
// [1.7.10] World.block.Mirror removed
// [1.7.10] block.entity removed
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.Constants.DEFAULT_STYLE;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_INVALID_BUILDING;

public abstract class AbstractSchematicProvider implements ISchematicProvider, IBuilding
{
    /**
     * The Colony for this schematic Provider
     */
    protected final IColony colony;

    /**
     * The location of the building.
     */
    private final int[] location;

    /**
     * The World of the building.
     */
    private int buildingLevel = 0;

    /**
     * The mirror of the building.
     */
    private boolean isBuildingMirrored = false;

    /**
     * The building style.
     */
    private String structurePack = DEFAULT_STYLE;

    /**
     * The building blueprint path.
     */
    private String path = "";

    /**
     * Height of the building.
     */
    private int height;

    /**
     * The type of the building
     */
    private BuildingEntry buildingType = null;

    /**
     * If the building was deconstructed by the builder.
     */
    private boolean isDeconstructed;

    /**
     * Corners of the building.
     */
    private int[] lowerCorner  = new int[]{0,0,0};
    private int[] higherCorner = new int[]{0,0,0};

    /**
     * Cached rotation.
     */
    public int cachedRotation = -1;

    /**
     * Parent schematic this is in
     */
    private int[] parentSchematic = new int[]{0,0,0};

    /**
     * Blueprint future for delayed info reading.
     */
    private Future<Blueprint> blueprintFuture;
    private String            blueprintFuturePack = "";
    private String            blueprintFutureName = "";

    /**
     * If prestige should be recalculated.
     */
    private boolean recalcPrestige;

    public AbstractSchematicProvider(final int[] pos, final IColony colony)
    {
        if (pos.equals(new int[]{0,0,0}))
        {
            Log.getLogger().warn("Creating building at zero pos!:", new Exception());
        }

        this.location = pos;
        this.colony = colony;
    }

    @Override
    public int hashCode()
    {
        return (int) (31 * this.getID().asLong());
    }

    @Override
    public boolean equals(final Object o)
    {
        return o instanceof AbstractBuilding && ((IBuilding) o).getID().equals(this.getID());
    }

    @Override
    public boolean isDeconstructed()
    {
        return isDeconstructed;
    }

    @Override
    public void setDeconstructed()
    {
        this.isDeconstructed = true;
    }

    @Override
    public String getBlueprintPath()
    {
        return path;
    }

    @Override
    public void setBlueprintPath(final String path)
    {
        this.path = path;
        getTileEntity().setBlueprintPath(path);
        cachedRotation = -1;
        this.markDirty();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = new NBTTagCompound();
        BlockPosUtil.write(compound, TAG_LOCATION, location);

        compound.putString(TAG_PACK, structurePack);
        compound.putString(TAG_PATH, getBlueprintPath());

        compound.putInt(TAG_SCHEMATIC_LEVEL, buildingLevel);
        compound.putBoolean(TAG_MIRROR, isBuildingMirrored);

        getCorners();
        BlockPosUtil.write(compound, TAG_CORNER1, this.lowerCorner);
        BlockPosUtil.write(compound, TAG_CORNER2, this.higherCorner);

        compound.putInt(TAG_HEIGHT, this.height);

        compound.putInt(TAG_ROTATION, cachedRotation);

        compound.putBoolean(TAG_DECONSTRUCTED, isDeconstructed);

        BlockPosUtil.write(compound, TAG_PARENT_SCHEM, parentSchematic);
        return compound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        buildingLevel = compound.getInt(TAG_SCHEMATIC_LEVEL);

        deserializerStructureInformationFrom(compound);

        isBuildingMirrored = compound.getBoolean(TAG_MIRROR);

        if (compound.contains(TAG_CORNER1) && compound.contains(TAG_CORNER2))
        {
            setCorners(BlockPosUtil.read(compound, TAG_CORNER1), BlockPosUtil.read(compound, TAG_CORNER2));
        }

        if (compound.contains(TAG_HEIGHT))
        {
            this.height = compound.getInt(TAG_HEIGHT);
        }

        if (compound.contains(TAG_ROTATION))
        {
            this.cachedRotation = compound.getInt(TAG_ROTATION);
        }

        if (compound.contains(TAG_DECONSTRUCTED))
        {
            this.isDeconstructed = compound.getBoolean(TAG_DECONSTRUCTED);
        }
        else
        {
            this.isDeconstructed = false;
        }

        parentSchematic = BlockPosUtil.read(compound, TAG_PARENT_SCHEM);
    }

    private void deserializerStructureInformationFrom(final NBTTagCompound compound)
    {
        String packName;
        String path;
        if (compound.contains(TAG_STYLE) && !compound.getString(TAG_STYLE).isEmpty())
        {
            packName = BlueprintMapping.getStyleMapping(compound.getString(TAG_STYLE));
            path = BlueprintMapping.getPathMapping(compound.getString(TAG_STYLE), this.getSchematicName()) + buildingLevel + ".blueprint";
        }
        else
        {
            packName = compound.getString(TAG_PACK);
            path = compound.getString(TAG_PATH);
        }

        if ((path == null || path.isEmpty()) && getBuildingType().getBuildingBlock() instanceof AbstractBlockHut<?> abstractBlockHut)
        {
            path = BlueprintMapping.getPathMapping("", abstractBlockHut.getBlueprintName()) + "1.blueprint";
        }

        this.structurePack = packName;
        this.path = path;

        if (structurePack == null || structurePack.isEmpty())
        {
            Log.getLogger().warn("Loaded empty style, setting to Default");
            structurePack = DEFAULT_STYLE;
        }
    }

    @Override
    public IColony getColony()
    {
        return colony;
    }

    @Override
    public int[] getPosition()
    {
        return location;
    }

    @Override
    public void setCorners(final int[] pos1, final int[] pos2)
    {
        this.lowerCorner = new int[]{Math.min(pos1[0], pos2[0]), Math.min(pos1[1], pos2[1]), Math.min(pos1[2], pos2[2])};
        this.higherCorner = new int[]{Math.max(pos1[0], pos2[0]), Math.max(pos1[1], pos2[1]), Math.max(pos1[2], pos2[2])};
    }

    @Override
    public Tuple<int[], int[]> getCorners()
    {
        if (lowerCorner.equals(new int[]{0,0,0}) || higherCorner.equals(new int[]{0,0,0}))
        {
            this.calculateCorners();

            if (lowerCorner.equals(new int[]{0,0,0}) || higherCorner.equals(new int[]{0,0,0}))
            {
                return new Tuple<>(getPosition(), getPosition());
            }
        }

        return new Tuple<>(lowerCorner, higherCorner);
    }

    @Override
    public int[] getID()
    {
        // Location doubles as ID.
        return location;
    }

    @Override
    public int[] getParent()
    {
        return isParentValid(parentSchematic) ? parentSchematic : new int[]{0,0,0};
    }

    @Override
    public boolean hasParent()
    {
        return !parentSchematic.equals(new int[]{0,0,0});
    }

    @Override
    public void setParent(final int[] pos)
    {
        if (isParentValid(pos))
        {
            parentSchematic = pos;
        }
    }

    private boolean isParentValid(int[] position)
    {
        final IBuilding building = colony.getServerBuildingManager().getBuilding(position);
        return building != null && !building.getID().equals(getID()) && !building.hasParent();
    }

    @Override
    public Set<int[]> getChildren()
    {
        return colony.getServerBuildingManager().getBuildings().values().stream()
          .filter(f -> f.getParent().equals(getID()))
          .map(ISchematicProvider::getID)
          .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int getRotation()
    {
        if (cachedRotation == -1)
        {
            cachedRotation = BuildingUtils.getRotationFromBlueprint(colony.getWorld(), getPosition());
        }
        return cachedRotation;
    }

    /**
     * Load updated TE data from the schematic if missing.
     */
    public void safeUpdateTEDataFromSchematic()
    {
        if (buildingLevel <= 0 || blueprintFuture != null)
        {
            return;
        }

        final TileEntityColonyBuilding te = (TileEntityColonyBuilding) colony.getWorld().getBlockEntity(getPosition());

        try
        {
            unsafeUpdateTEDataFromSchematic(te);
            return;
        }
        catch (final Exception ex)
        {
            Log.getLogger().warn("TileEntity with invalid data, restoring correct data from schematic.");
            te.setSchematicName(this.getSchematicName() + Math.max(1, buildingLevel));
        }

        try
        {
            unsafeUpdateTEDataFromSchematic(te);
        }
        catch (final Exception ex)
        {
            MessageUtils.format(WARNING_INVALID_BUILDING, getSchematicName(), getID().getX(), getID().getY(), getID().getZ(), getStructurePack()).sendTo(colony).forAllPlayers();
        }
    }

    @Override
    public void onColonyTick(final IColony colony)
    {
        if (blueprintFuture != null && blueprintFuture.isDone())
        {
            final Blueprint blueprint;
            try
            {
                blueprint = blueprintFuture.get();
                if (blueprint != null)
                {
                    blueprintFuture = null;
                    if (recalcPrestige)
                    {
                        calculatePrestige(blueprint);
                        recalcPrestige = false;
                    }
                }
                else
                {
                    recalcPrestige = false;
                    colony.getServerBuildingManager().clearPendingPrestigeCalc(this);
                }
            }
            catch (Exception e)
            {
                Log.getLogger().info("Failed to load blueprintfuture for: pack:" + blueprintFuturePack + " name:" + blueprintFutureName, e);
                blueprintFuture = null;
            }
        }
    }

    @Override
    public void asyncPrestigeRecalc()
    {
        // No need to calculate prestige for buildings at World 0.
        if (buildingLevel == 0)
        {
            colony.getServerBuildingManager().clearPendingPrestigeCalc(this);
            return;
        }

        if (!recalcPrestige)
        {
            recalcPrestige = true;
            blueprintFuture = StructurePacks.getBlueprintFuture(this.getStructurePack(), this.getBlueprintPath());
        }
    }

    /**
     * Load the schematic data from the TE schematic name, if it's a reattempt, calculate the name from the building (backup).
     * Might throw exceptions if data is invalid.
     */
    private void unsafeUpdateTEDataFromSchematic(final TileEntityColonyBuilding te)
    {
        final String structureName;
        final String packName;
        if (te.getSchematicName().isEmpty())
        {
            structureName = path;
            packName = structurePack;
        }
        else
        {
            structureName = te.getBlueprintPath();
            packName = te.getStructurePack().getName();
        }

        blueprintFuture = StructurePacks.getBlueprintFuture(packName, structureName);
        blueprintFuturePack = packName;
        blueprintFutureName = structureName;
    }

    @Override
    public String getStructurePack()
    {
        final int[] parent = getParent();
        if (parent != new int[]{0,0,0})
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(parent);
            if (building != null)
            {
                return building.getStructurePack();
            }
        }

        return structurePack;
    }

    @Override
    public void setStructurePack(final String pack)
    {
        this.structurePack = pack;
        cachedRotation = -1;
        this.markDirty();
        getTileEntity().setStructurePack(StructurePacks.getStructurePack(pack));
    }

    @Override
    public int getBuildingLevel()
    {
        return buildingLevel;
    }

    @Override
    public void setBuildingLevel(final int World)
    {
        if (World > getMaxBuildingLevel())
        {
            return;
        }

        isDeconstructed = false;
        buildingLevel = World;
        markDirty();
    }

    @Override
    public void setIsMirrored(final boolean isMirrored)
    {
        this.isBuildingMirrored = isMirrored;
    }

    @Override
    public boolean isMirrored()
    {
        return isBuildingMirrored;
    }

    @Override
    public boolean isInBuilding(@NotNull final int[] positionVec)
    {
        final Tuple<int[], int[]> corners = getCorners();
        int[] cornerA = corners.getA();
        int[] cornerB = corners.getB();

        if (this.hasModule(IAltersBuildingFootprint.class))
        {
            final Tuple<int[], int[]> extensions = this.getFirstModuleOccurance(IAltersBuildingFootprint.class).getAdditionalCorners();
            cornerA = cornerA.offset(extensions.getA());
            cornerB = cornerB.offset(extensions.getB());
        }

        return positionVec.getX() >= cornerA.getX() - 1 && positionVec.getX() <= cornerB.getX() + 1
                 && positionVec.getY() >= cornerA.getY() - 1 && positionVec.getY() <= cornerB.getY() + 1
                 && positionVec.getZ() >= cornerA.getZ() - 1 && positionVec.getZ() <= cornerB.getZ() + 1;
    }

    @Override
    public void upgradeBuildingLevelToSchematicData()
    {
        final BlockEntity tileEntity = colony.getWorld().getBlockEntity(getID());
        if (tileEntity instanceof IBlueprintDataProviderBE blueprintDataProvider)
        {
            if (blueprintDataProvider.getSchematicName().isEmpty())
            {
                return;
            }

            setCorners(blueprintDataProvider.getInWorldCorners().getA(), blueprintDataProvider.getInWorldCorners().getB());

            int World = 0;
            try
            {
                World = Integer.parseInt(blueprintDataProvider.getSchematicName().substring(blueprintDataProvider.getSchematicName().length() - 1));
            }
            catch (NumberFormatException e)
            {
            }

            if (World > 0 && (World > getBuildingLevel() || isDeconstructed) && World <= getMaxBuildingLevel())
            {
                if (World > getBuildingLevel())
                {
                    Tuple<int[], int[]> corners = getCorners();
                    if (getParent() != new int[]{0,0,0})
                    {
                        final BlockEntity parentTileEntity = colony.getWorld().getBlockEntity(getParent());
                        if (parentTileEntity instanceof AbstractTileEntityColonyBuilding parentBuildingTileEntity)
                        {
                            corners = parentBuildingTileEntity.getBuilding().getCorners();
                        }
                    }
                    FireworkUtils.spawnFireworksAtAABBCorners(corners, colony.getWorld(), World);
                }

                setBuildingLevel(World);
                onUpgradeComplete(null, World);
                isDeconstructed = false;
            }
        }
    }

    @Override
    public void onUpgradeSchematicTo(final String oldSchematic, final String newSchematic, final IBlueprintDataProviderBE blueprintDataProvider)
    {
        upgradeBuildingLevelToSchematicData();
    }

    @Override
    public final BuildingEntry getBuildingType()
    {
        return buildingType;
    }

    @Override
    public void setBuildingType(final BuildingEntry buildingType)
    {
        this.buildingType = buildingType;
    }
}







