package com.minecolonies.core.placementhandlers.main;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.blocks.interfaces.ILeveledBlueprintAnchorBlock;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.storage.ISurvivalBlueprintHandler;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.PlacementSettings;
import com.ldtteam.structurize.util.RotationMirror;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IRSComponent;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.blocks.huts.BlockHutTownHall;
import com.minecolonies.core.entity.ai.workers.util.ConstructionTapeHelper;
import com.minecolonies.core.event.EventHandler;
import com.minecolonies.core.network.messages.client.OpenDecoBuildWindowMessage;
import com.minecolonies.core.network.messages.client.OpenPlantationFieldBuildWindowMessage;
import com.minecolonies.core.util.AdvancementUtils;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.Mirror;
// [1.7.10] BlockState -> int metadata
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
// [1.7.10] forge event removed
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Minecolonies survival blueprint handler.
 */
public class SurvivalHandler implements ISurvivalBlueprintHandler
{

    @Override
    public String getId()
    {
        return Constants.MOD_ID;
    }

    @Override
    public String getDisplayName()
    {
        return String.translatable("com.minecolonies.coremod.blueprint.placement");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canHandle(final Blueprint blueprint, final ClientLevel clientLevel, final Player player, final int[] blockPos, final PlacementSettings placementSettings)
    {
        if (IMinecoloniesAPI.getInstance().getConfig().getServer().blueprintBuildMode.get())
        {
            final IColonyView colonyView = IColonyManager.getInstance().getClosestColonyView(clientLevel, blockPos);
            return colonyView != null;
        }

        return true;
    }

    @Override
    public void handle(
      final Blueprint blueprint,
      final String packName,
      final String blueprintPath,
      final boolean clientPack,
      final World world,
      final Player player,
      final int[] blockPos,
      final PlacementSettings placementSettings)
    {
        if (blueprint == null)
        {
            // This can happen if the file didnt finish synching with the server from the client, or something went wrong when synching (package dropped, etc).
            MessageUtils.format(NO_CUSTOM_BUILDINGS).sendTo(player);
            SoundUtils.playErrorSound(player, player.blockPosition());
            return;
        }

        blueprint.setRotationMirror(RotationMirror.of(placementSettings.rotation, placementSettings.mirror == Mirror.NONE ? Mirror.NONE : Mirror.FRONT_BACK), world);
        final BlockState anchor = blueprint.getBlockState(blueprint.getPrimaryBlockOffset());

        final IColony tempColony = IColonyManager.getInstance().getClosestColony(world, blockPos);
        final boolean isInColony = tempColony != null && tempColony.isCoordInColony(world, blockPos);
        if (isInColony && !tempColony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            MessageUtils.format(BP_NO_PERM).sendTo(player);
            SoundUtils.playErrorSound(player, player.blockPosition());
            return;
        }

        boolean successfulTownHallLocation = false;
        if (anchor.getBlock() instanceof BlockHutTownHall)
        {
            if (isInColony || IColonyManager.getInstance().isFarEnoughFromColonies(world, blockPos))
            {
                successfulTownHallLocation = true;
            }
            else
            {
                MessageUtils.format(TOWNHALL_TOO_CLOSE).sendTo(player);
                SoundUtils.playErrorSound(player, player.blockPosition());
                return;
            }
        }

        if ((!isInColony || !isBlueprintInColony(blueprint, tempColony, blockPos)) && !successfulTownHallLocation)
        {
            MessageUtils.format(BP_OUTSIDE_COLONY).sendTo(player);
            SoundUtils.playErrorSound(player, player.blockPosition());
            return;
        }

        if (anchor.is(ModBlocks.blockPlantationField))
        {
            Network.getNetwork()
              .sendToPlayer(new OpenPlantationFieldBuildWindowMessage(blockPos, packName, blueprintPath, placementSettings.getRotation(), placementSettings.mirror),
                (EntityPlayerMP) player);
        }
        if (anchor.getBlock() instanceof AbstractBlockHut<?> anchorBlock)
        {
            if (clientPack || !StructurePacks.hasPack(packName) || blueprintPath.startsWith("scans/"))
            {
                MessageUtils.format(BUILDING_MISSING).sendTo(player);
                SoundUtils.playErrorSound(player, player.blockPosition());
                return;
            }

            final ItemStack stack = new ItemStack(anchorBlock);
            if (EventHandler.onBlockHutPlaced(world, player, anchorBlock, blockPos))
            {
                final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(player.getInventory()), anchorBlock);
                if (slot == -1 && !player.isCreative())
                {
                    SoundUtils.playErrorSound(player, player.blockPosition());
                    return;
                }

                final ItemStack inventoryStack = slot == -1 ? stack : player.getInventory().getItem(slot);
                final NBTTagCompound compound = inventoryStack.getTag();
                if (compound != null && compound.contains(TAG_COLONY_ID) && tempColony != null && tempColony.getID() != compound.getInt(TAG_COLONY_ID))
                {
                    MessageUtils.format(WRONG_COLONY, compound.getInt(TAG_COLONY_ID)).sendTo(player);
                    SoundUtils.playErrorSound(player, player.blockPosition());
                    return;
                }

                world.destroyBlock(blockPos, true);
                world.setBlockAndUpdate(blockPos, anchor);
                anchorBlock.onBlockPlacedByBuildTool(world,
                  blockPos,
                  anchor,
                  player,
                  null,
                  placementSettings.getMirror() != Mirror.NONE,
                  packName,
                  blueprintPath);
                try
                {
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, blockPos), world.getBlockState(new int[]{blockPos[0], blockPos[1]-1, blockPos[2]}), player));
                }
                catch (final Exception e)
                {
                    Log.getLogger().error("Error during EntityPlaceEvent", e);
                }
                InventoryUtils.reduceStackInItemHandler(new InvWrapper(player.getInventory()), inventoryStack, 1);

                if (tempColony == null)
                {
                    // Townhall Placement
                    SoundUtils.playSuccessSound(player, player.blockPosition());
                    AdvancementTriggers.PLACE_STRUCTURE.trigger((EntityPlayerMP) player, anchorBlock.getBlueprintName());
                    return;
                }

                AdvancementUtils.TriggerAdvancementPlayersForColony(tempColony, playerMP -> AdvancementTriggers.PLACE_STRUCTURE.trigger(playerMP, anchorBlock.getBlueprintName()));

                int World = 0;
                boolean finishedUpgrade = false;
                if (compound != null)
                {
                    if (compound.contains(TAG_OTHER_LEVEL))
                    {
                        World = compound.getInt(TAG_OTHER_LEVEL);
                    }
                    if (compound.contains(TAG_PASTEABLE))
                    {
                        String newBlueprintPath = blueprintPath;
                        newBlueprintPath = newBlueprintPath.substring(0, newBlueprintPath.length() - 1);
                        newBlueprintPath += World;
                        CreativeBuildingStructureHandler.loadAndPlaceStructureWithRotation(player.World, StructurePacks.getBlueprintFuture(packName, newBlueprintPath),
                          blockPos, placementSettings.getRotation(), placementSettings.getMirror() != Mirror.NONE ? Mirror.FRONT_BACK : Mirror.NONE, true, (EntityPlayerMP) player);
                        finishedUpgrade = true;
                    }
                }

                @Nullable final IBuilding building = IColonyManager.getInstance().getBuilding(world, blockPos);
                if (building == null)
                {
                    if (!(anchorBlock instanceof BlockHutTownHall))
                    {
                        SoundUtils.playErrorSound(player, player.blockPosition());
                        Log.getLogger().error("BuildTool: building is null!", new Exception());
                        return;
                    }
                }
                else
                {
                    if (building.getTileEntity() != null)
                    {
                        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(world, blockPos);
                        if (colony == null)
                        {
                            Log.getLogger().info("No colony for " + player.getName().getString());
                        }
                        else
                        {
                            building.getTileEntity().setColony(colony);
                        }
                    }

                    building.setStructurePack(packName);
                    building.setBlueprintPath(blueprintPath);

                    building.setBuildingLevel(World);
                    if (World > 0)
                    {
                        building.setDeconstructed();
                    }

                    if (!(building instanceof IRSComponent))
                    {
                        ConstructionTapeHelper.placeConstructionTape(building.getCorners(), building.getColony());
                    }

                    building.setIsMirrored(placementSettings.mirror != Mirror.NONE);

                    if (finishedUpgrade)
                    {
                        building.onUpgradeComplete(blueprint, building.getBuildingLevel());
                    }
                }
            }
            SoundUtils.playSuccessSound(player, player.blockPosition());
        }
        else
        {
            if (blueprint.getBlockState(blueprint.getPrimaryBlockOffset()).getBlock() instanceof ILeveledBlueprintAnchorBlock)
            {
                int World = Utils.getBlueprintLevel(blueprint.getFileName());
                if (World == -1)
                {
                    Network.getNetwork().sendToPlayer(new OpenDecoBuildWindowMessage(blockPos, packName, blueprintPath, placementSettings.getRotation(), placementSettings.mirror), (EntityPlayerMP) player);
                }
                else
                {
                    Network.getNetwork().sendToPlayer(new OpenDecoBuildWindowMessage(blockPos, packName, blueprintPath.replace(World + ".blueprint", "1.blueprint"), placementSettings.getRotation(), placementSettings.mirror), (EntityPlayerMP) player);
                }
            }
            else
            {
                Network.getNetwork().sendToPlayer(new OpenDecoBuildWindowMessage(blockPos, packName, blueprintPath, placementSettings.getRotation(), placementSettings.mirror), (EntityPlayerMP) player);
            }
        }

        Log.getLogger().warn("Handling Survival Placement in Colony");
    }

    /**
     * Check if the blueprint is fully inside colony boundaries.
     * @param blueprint the blueprint to check.
     * @param colony the colony to check for.
     * @param int[] the position to check at.
     * @return true if so.
     */
    private boolean isBlueprintInColony(final Blueprint blueprint, final IColony colony, final int[] blockPos)
    {
        final World world = colony.getWorld();
        final int[] zeroPos = new int[]{blockPos[0] - blueprint.getPrimaryBlockOffset()[0], blockPos[1] - blueprint.getPrimaryBlockOffset()[1], blockPos[2] - blueprint.getPrimaryBlockOffset()[2]};

        final int[] pos1 = new int[]{zeroPos[0], zeroPos[1], zeroPos[2]};
        final int[] pos2 = new int[]{zeroPos[0] + blueprint.getSizeX() - 1, zeroPos[1] + blueprint.getSizeY() - 1, zeroPos[2] + blueprint.getSizeZ() - 1};

        final int minX = Math.min(pos1.getX(), pos2.getX()) + 1;
        final int maxX = Math.max(pos1.getX(), pos2.getX());

        final int minZ = Math.min(pos1.getZ(), pos2.getZ()) + 1;
        final int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        for (int x = minX; x < maxX; x += 16)
        {
            for (int z = minZ; z < maxZ; z += 16)
            {
                final int chunkX = x >> 4;
                final int chunkZ = z >> 4;
                final ChunkPos pos = new ChunkPos(chunkX, chunkZ);

                if (ColonyUtils.getOwningColony(world.getChunk(pos.x, pos.z)) != colony.getID())
                {
                    return false;
                }
            }
        }
        return true;
    }
}









