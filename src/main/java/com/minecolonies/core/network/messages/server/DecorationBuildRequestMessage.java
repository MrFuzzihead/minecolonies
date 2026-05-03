package com.minecolonies.core.network.messages.server;

import com.ldtteam.structurize.storage.ServerFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.workorders.IServerWorkOrder;
import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import com.minecolonies.core.blocks.BlockDecorationController;
import com.minecolonies.core.colony.buildings.AbstractBuildingStructureBuilder;
import com.minecolonies.core.colony.workorders.WorkOrderDecoration;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Registries removed
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.Rotation;
// [1.7.10] BlockState -> int metadata
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Adds a entry to the builderRequired map.
 */
public class DecorationBuildRequestMessage implements IMessage
{
    /**
     * The id of the building.
     */
    private int[] pos;

    /**
     * The display name of the decoration.
     */
    private String packName;

    /**
     * The name of the decoration.
     */
    private String path;

    /**
     * The rotation.
     */
    private Rotation rotation;

    /**
     * If mirrored.
     */
    private boolean mirror;

    /**
     * The dimension.
     */
    private int /* ResourceKey */ dimension;

    /**
     * Type of workorder.
     */
    private WorkOrderType workOrderType;

    /**
     * The builder, or ZERO to auto-assign.
     */
    private int[] builder;

    /**
     * Empty constructor used when registering the
     */
    public DecorationBuildRequestMessage()
    {
        super();
    }

    /**
     * Creates a build request for a decoration.
     *
     * @param pos         the position of it.
     * @param packName    pack name.
     * @param path        blueprint path.
     * @param dimension   the dimension we're executing on.
     */
    public DecorationBuildRequestMessage(final WorkOrderType workOrderType, @NotNull final int[] pos, final String packName, final String path, final int /* ResourceKey */ dimension, final Rotation rotation, final boolean mirror, final int[] builder)
    {
        super();
        this.workOrderType = workOrderType;
        this.pos = pos;
        this.packName = packName;
        this.path = path;
        this.dimension = dimension;
        this.rotation = rotation;
        this.mirror = mirror;
        this.builder = builder;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        this.workOrderType = WorkOrderType.values()[buf.readInt()];
        this.pos = buf.readBlockPos();
        this.packName = buf.readUtf(32767);
        this.path = buf.readUtf(32767);
        this.dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(buf.readUtf(32767)));
        this.mirror = buf.readBoolean();
        this.rotation = Rotation.values()[buf.readInt()];
        this.builder = buf.readBlockPos();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(this.workOrderType.ordinal());
        buf.writeBlockPos(this.pos);
        buf.writeUtf(this.packName);
        buf.writeUtf(this.path);
        buf.writeUtf(this.dimension.location().toString());
        buf.writeBoolean(this.mirror);
        buf.writeInt(this.rotation.ordinal());
        buf.writeBlockPos(this.builder);
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
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromDim(dimension, pos);
        if (colony == null)
        {
            return;
        }
        final Player player = ctx.getServerHandler().playerEntity;

        //Verify player has permission to change this hut its settings
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
        {
            return;
        }

        final Optional<Map.Entry<Integer, IServerWorkOrder>> wo = colony.getWorkManager().getWorkOrders().entrySet().stream()
          .filter(entry -> entry.getValue() instanceof WorkOrderDecoration)
          .filter(entry -> entry.getValue().getLocation().equals(pos)).findFirst();

        if (wo.isPresent())
        {
            colony.getWorkManager().removeWorkOrder(wo.get().getKey());
            return;
        }

        ServerFutureProcessor.queueBlueprint(new ServerFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(packName, path),
          player.World,
          (blueprint -> {
              if (blueprint == null)
              {
                  Log.getLogger().error(String.format("Schematic %s doesn't exist on the server.", path));
                  return;
              }

              final String[] split = path.split("/");
              final String displayName = split[split.length - 1].replace(".blueprint", "");

              final BlockState structureState = blueprint.getBlockInfoAsMap().get(blueprint.getPrimaryBlockOffset()).getState();
              final WorkOrderType type = structureState != null && !(structureState.getBlock() instanceof BlockDecorationController)
                      ? WorkOrderType.BUILD : workOrderType;

              final WorkOrderDecoration order = WorkOrderDecoration.create(
                  type,
                  packName,
                  path,
                  WordUtils.capitalizeFully(displayName),
                  pos,
                  rotation.ordinal(),
                  mirror,
                  0);
              order.setBlueprint(blueprint, colony.getWorld());

              if (!builder.equals(new int[]{0,0,0}))
              {
                  final IBuilding building = colony.getServerBuildingManager().getBuilding(builder);
                  if (building instanceof AbstractBuildingStructureBuilder)
                  {
                      order.setClaimedBy(builder);
                  }
              }

              colony.getWorkManager().addWorkOrder(order, false);
          })));
    }
}





