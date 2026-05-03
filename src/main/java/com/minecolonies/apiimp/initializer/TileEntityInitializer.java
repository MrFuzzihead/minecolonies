package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.tileentities.*;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.*;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityInitializer
{
    public static void init()
    {
        MinecoloniesTileEntities.SCARECROW = TileEntityScarecrow.class;
        GameRegistry.registerTileEntity(TileEntityScarecrow.class, Constants.MOD_ID + ":scarecrow");

        MinecoloniesTileEntities.PLANTATION_FIELD = TileEntityPlantationField.class;
        GameRegistry.registerTileEntity(TileEntityPlantationField.class, Constants.MOD_ID + ":plantationfield");

        MinecoloniesTileEntities.BARREL = TileEntityBarrel.class;
        GameRegistry.registerTileEntity(TileEntityBarrel.class, Constants.MOD_ID + ":barrel");

        MinecoloniesTileEntities.BUILDING = TileEntityColonyBuilding.class;
        GameRegistry.registerTileEntity(TileEntityColonyBuilding.class, Constants.MOD_ID + ":colonybuilding");

        MinecoloniesTileEntities.DECO_CONTROLLER = TileEntityDecorationController.class;
        GameRegistry.registerTileEntity(TileEntityDecorationController.class, Constants.MOD_ID + ":decorationcontroller");

        MinecoloniesTileEntities.RACK = TileEntityRack.class;
        GameRegistry.registerTileEntity(TileEntityRack.class, Constants.MOD_ID + ":rack");

        MinecoloniesTileEntities.GRAVE = TileEntityGrave.class;
        GameRegistry.registerTileEntity(TileEntityGrave.class, Constants.MOD_ID + ":grave");

        MinecoloniesTileEntities.NAMED_GRAVE = TileEntityNamedGrave.class;
        GameRegistry.registerTileEntity(TileEntityNamedGrave.class, Constants.MOD_ID + ":namedgrave");

        MinecoloniesTileEntities.WAREHOUSE = TileEntityWareHouse.class;
        GameRegistry.registerTileEntity(TileEntityWareHouse.class, Constants.MOD_ID + ":warehouse");

        MinecoloniesTileEntities.COMPOSTED_DIRT = TileEntityCompostedDirt.class;
        GameRegistry.registerTileEntity(TileEntityCompostedDirt.class, Constants.MOD_ID + ":composteddirt");

        MinecoloniesTileEntities.ENCHANTER = TileEntityEnchanter.class;
        GameRegistry.registerTileEntity(TileEntityEnchanter.class, Constants.MOD_ID + ":enchanter");

        MinecoloniesTileEntities.STASH = TileEntityStash.class;
        GameRegistry.registerTileEntity(TileEntityStash.class, Constants.MOD_ID + ":stash");

        MinecoloniesTileEntities.COLONY_FLAG = TileEntityColonyFlag.class;
        GameRegistry.registerTileEntity(TileEntityColonyFlag.class, Constants.MOD_ID + ":colony_flag");

        MinecoloniesTileEntities.COLONY_SIGN = TileEntityColonySign.class;
        GameRegistry.registerTileEntity(TileEntityColonySign.class, Constants.MOD_ID + ":colonysign");
    }
}

