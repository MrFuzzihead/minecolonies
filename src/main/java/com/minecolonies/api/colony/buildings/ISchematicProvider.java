package com.minecolonies.api.colony.buildings;
import net.minecraft.network.chat.Style;

import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import com.minecolonies.api.util.Tuple;
// [1.7.10] INBTSerializable -> manual read/write

import java.util.Set;

public interface ISchematicProvider 
{
    /**
     * Sets the corners of the building based on the schematic.
     *
     * @param corner1 the first corner.
     * @param corner2 the second corner.
     */
    void setCorners(final int[] corner1, final int[] corner2);

    /**
     * Get all the corners of the building based on the schematic.
     * This is the lowest corner (x,y,z) and the highest corner (x,y,z).
     *
     * @return the corners.
     */
    Tuple<int[], int[]> getCorners();

    /**
     * Returns the {@code int[]} of the current object, also used as ID.
     *
     * @return {@code int[]} of the current object.
     */
    int[] getID();

    /**
     * Get the parent building position
     *
     * @return
     */
    int[] getParent();

    /**
     * Whether we have a parent
     *
     * @return true if there is a parent building
     */
    boolean hasParent();

    /**
     * Set the parent building position
     * @param pos
     */
    void setParent(int[] pos);

    /**
     * Get the child building positions
     * @return
     */
    Set<int[]> getChildren();

    /**
     * Returns the rotation of the current building.
     *
     * @return integer value of the rotation.
     */
    int getRotation();

    /**
     * Returns the style of the current building.
     *
     * @return String representation of the current building-style
     */
    String getStructurePack();

    /**
     * Sets the style of the building.
     *
     * @param style String value of the style.
     */
    void setStructurePack(String style);

    /**
     * Returns the blueprint path of the current building.
     *
     * @return String representation of the current blueprint-path
     */
    String getBlueprintPath();

    /**
     * Sets the blueprint path of the building.
     *
     * @param path String value of the blueprint-path.
     */
    void setBlueprintPath(String path);

    /**
     * Sets the current World of the building.
     *
     * @param World World of the building.
     */
    void setBuildingLevel(int World);

    /**
     * Returns whether the instance is dirty or not.
     *
     * @return true if dirty, false if not.
     */
    boolean isDirty();

    /**
     * Sets {@code #dirty} to false, meaning that the instance is up to date.
     */
    void clearDirty();

    /**
     * Marks the instance and the building dirty.
     */
    void markDirty();

    /**
     * Sets the mirror of the current building.
     */
    void setIsMirrored(final boolean isMirrored);

    /**
     * Returns the mirror of the current building.
     *
     * @return boolean value of the mirror.
     */
    boolean isMirrored();

    /**
     * Children must return the name of their structure.
     *
     * @return StructureProxy name.
     */
    String getSchematicName();

    /**
     * Children must return their max building World.
     *
     * @return Max building World.
     */
    int getMaxBuildingLevel();

    /**
     * Check if the building was deconstructed.
     *
     * @return true if so.
     */
    boolean isDeconstructed();

    /**
     * Set the building as deconstructed.
     */
    void setDeconstructed();

    /**
     * Called when the old schematic is updated to a new one
     *
     * @param oldSchematic
     * @param newSchematic
     */
    void onUpgradeSchematicTo(final String oldSchematic, final String newSchematic, final IBlueprintDataProviderBE blueprintDataProvider);
}




