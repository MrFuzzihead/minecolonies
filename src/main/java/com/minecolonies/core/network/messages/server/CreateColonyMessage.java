package com.minecolonies.core.network.messages.server;

import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.eventbus.events.colony.ColonyCreatedModEvent;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.colony.OpenBuildingUIMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.MessageUtils.MessagePriority;
import com.minecolonies.core.MineColonies;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
// [1.7.10] block.entity removed
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static com.minecolonies.api.util.constant.BuildingConstants.DEACTIVATED;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Message for trying to create a new colony.
 */
public class CreateColonyMessage implements IMessage
{
    /**
     * Town hall position to create building on.
     */
    int[] townHall;

    /**
     * If claim action.
     */
    boolean claim;

    /**
     * The colony name.
     */
    String colonyName;

    /**
     * The structure pack name.
     */
    String packName;

    /**
     * The structure path name.
     */
    String pathName;

    public CreateColonyMessage()
    {
        super();
    }

    public CreateColonyMessage(final int[] townHall, boolean claim, final String colonyName, final String packName, final String pathName)
    {
        this.townHall = townHall;
        this.claim = claim;
        this.colonyName = colonyName;
        this.packName = packName;
        this.pathName = pathName;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(townHall);
        buf.writeBoolean(claim);
        buf.writeUtf(colonyName);
        buf.writeUtf(packName);
        buf.writeUtf(pathName);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        townHall = buf.readBlockPos();
        claim = buf.readBoolean();
        colonyName = buf.readUtf(32767);
        packName = buf.readUtf(32767);
        pathName = buf.readUtf(32767);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.TRUE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        final World world = ctx.getServerHandler().playerEntity.World;

        if (sender == null)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getClosestColony(world, townHall);

        String pack = packName;
        final BlockEntity tileEntity = world.getBlockEntity(townHall);

        if (!(tileEntity instanceof TileEntityColonyBuilding))
        {
            MessageUtils.format(WARNING_TOWN_HALL_NO_TILE_ENTITY)
              .withPriority(MessagePriority.DANGER)
              .sendTo(sender);
            return;
        }

        final TileEntityColonyBuilding hut = (TileEntityColonyBuilding) tileEntity;
        if (hut.getStructurePack() != null && claim)
        {
            pack = hut.getStructurePack().getName();
        }

        final boolean reactivate = hut.getPositionedTags().getOrDefault(new int[]{0,0,0}, new ArrayList<>()).contains(DEACTIVATED);
        if (reactivate)
        {
            hut.reactivate();
            if (hut.getStructurePack() != null)
            {
                pack = hut.getStructurePack().getName();
            }
        }

        hut.setStructurePack(StructurePacks.getStructurePack(pack));
        hut.setBlueprintPath(pathName);

        final double spawnDistance = Math.sqrt(BlockPosUtil.getDistanceSquared2D(townHall, ((ServerLevel) world).getSharedSpawnPos()));
        if (spawnDistance < MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get())
        {
            if (!world.isClientSide)
            {
                MessageUtils.format(CANT_PLACE_COLONY_TOO_CLOSE_TO_SPAWN, MineColonies.getConfig().getServer().minDistanceFromWorldSpawn.get() - spawnDistance).sendTo(sender);
            }
            return;
        }
        else if (spawnDistance > MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get())
        {
            if (!world.isClientSide)
            {
                MessageUtils.format(CANT_PLACE_COLONY_TOO_FAR_FROM_SPAWN, spawnDistance - MineColonies.getConfig().getServer().maxDistanceFromWorldSpawn.get()).sendTo(sender);
            }
            return;
        }

        if (colony != null && !IColonyManager.getInstance().isFarEnoughFromColonies(world, townHall))
        {
            MessageUtils.format(MESSAGE_COLONY_CREATE_DENIED_TOO_CLOSE, colony.getName()).sendTo(sender);
            return;
        }

        final IColony ownedColony = IColonyManager.getInstance().getIColonyByOwner(world, sender);

        if (ownedColony == null)
        {
            final IColony createdColony = IColonyManager.getInstance().createColony(world, townHall, sender, colonyName, pack);
            final IBuilding building = createdColony.getServerBuildingManager().addNewBuilding((TileEntityColonyBuilding) tileEntity, world);

            if (reactivate)
            {
                MessageUtils.format(MESSAGE_COLONY_REACTIVATED, colonyName)
                  .withPriority(MessagePriority.IMPORTANT)
                  .sendTo(sender);
            }
            else
            {
                MessageUtils.format(MESSAGE_COLONY_FOUNDED)
                  .withPriority(MessagePriority.IMPORTANT)
                  .sendTo(sender);
            }

            IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyCreatedModEvent(createdColony));
            Network.getNetwork().sendToPlayer(new OpenBuildingUIMessage(building), sender);
            return;
        }

        ownedColony.getPackageManager().sendColonyViewPackets();
        ownedColony.getPackageManager().sendPermissionsPackets();
        MessageUtils.format(WARNING_COLONY_FOUNDING_FAILED)
          .withPriority(MessagePriority.DANGER)
          .sendTo(sender);
    }
}





