package com.minecolonies.api.inventory;

import com.minecolonies.api.inventory.container.*;

/**
 * [1.7.10] In 1.7.10, containers don't use a registry; they are identified by integer IDs.
 * This class holds container ID constants used to open GUIs.
 */
public class ModContainers
{
    public static final int craftingFurnace = 1;
    public static final int buildingInv = 2;
    public static final int citizenInv = 3;
    public static final int rackInv = 4;
    public static final int graveInv = 5;
    public static final int craftingGrid = 6;
    public static final int craftingBrewingstand = 7;
}
