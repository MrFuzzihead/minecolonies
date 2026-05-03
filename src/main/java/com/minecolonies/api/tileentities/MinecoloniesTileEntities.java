package com.minecolonies.api.tileentities;

import com.minecolonies.core.tileentities.*;
import net.minecraft.tileentity.TileEntity;

/**
 * Holds references to all MineColonies TileEntity classes.
 * In 1.7.10 there is no BlockEntityType registry — TileEntities are registered directly
 * via GameRegistry.registerTileEntity(Class, String) and looked up by class.
 */
public class MinecoloniesTileEntities
{
    public static Class<? extends AbstractTileEntityScarecrow> SCARECROW;

    public static Class<? extends AbstractTileEntityPlantationField> PLANTATION_FIELD;

    public static Class<? extends AbstractTileEntityBarrel> BARREL;

    public static Class<? extends AbstractTileEntityColonyBuilding> BUILDING;

    public static Class<? extends TileEntity> DECO_CONTROLLER;

    public static Class<TileEntityRack> RACK;

    public static Class<TileEntityGrave> GRAVE;

    public static Class<? extends TileEntityNamedGrave> NAMED_GRAVE;

    public static Class<? extends AbstractTileEntityWareHouse> WAREHOUSE;

    public static Class<? extends TileEntity> COMPOSTED_DIRT;

    public static Class<TileEntityEnchanter> ENCHANTER;

    public static Class<TileEntityStash> STASH;

    public static Class<TileEntityColonyFlag> COLONY_FLAG;

    public static Class<TileEntityColonySign> COLONY_SIGN;
}
