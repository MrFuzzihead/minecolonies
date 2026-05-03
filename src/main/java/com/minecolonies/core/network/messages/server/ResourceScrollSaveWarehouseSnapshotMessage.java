package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.items.ItemResourceScroll;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Message sent to the server when the client saves a new snapshot by clicking on a warehouse.
 */
public class ResourceScrollSaveWarehouseSnapshotMessage implements IMessage
{
    /**
     * The position of the builder.
     */
    private int[] builderPos;

    /**
     * The warehouse snapshot mapping.
     */
    @NotNull
    private Map<String, Integer> snapshot = new HashMap<>();

    /**
     * The hash of the current work order (if any).
     */
    @NotNull
    private String workOrderHash = "";

    /**
     * Empty constructor used when registering the message.
     */
    public ResourceScrollSaveWarehouseSnapshotMessage()
    {
        super();
    }

    /**
     * Empty constructor used when registering the message.
     */
    public ResourceScrollSaveWarehouseSnapshotMessage(int[] builderPos)
    {
        this(builderPos, Map.of(), "");
    }

    /**
     * Empty constructor used when registering the message.
     */
    public ResourceScrollSaveWarehouseSnapshotMessage(int[] builderPos, @NotNull Map<String, Integer> snapshot, @NotNull String workOrderHash)
    {
        super();
        this.builderPos = builderPos;
        this.snapshot = snapshot;
        this.workOrderHash = workOrderHash;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        if (buf.readBoolean())
        {
            builderPos = buf.readBlockPos();
        }
        int numItems = buf.readInt();
        snapshot = new HashMap<>();
        for (int i = 0; i < numItems; i++)
        {
            String itemName = buf.readUtf(32767);
            int itemAmount = buf.readInt();
            snapshot.put(itemName, itemAmount);
        }
        workOrderHash = buf.readUtf(32767);
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeBoolean(builderPos != null);
        if (builderPos != null)
        {
            buf.writeBlockPos(builderPos);
        }
        buf.writeInt(snapshot.size());
        snapshot.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeInt(value);
        });
        buf.writeUtf(workOrderHash);
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
        Objects.requireNonNull(ctx.getServerHandler().playerEntity).getInventory().items.stream()
          .filter(stack -> stack.getItem() instanceof ItemResourceScroll)
          .filter(stack -> stack.getTag() != null)
          .filter(stack -> Objects.equals(builderPos, BlockPosUtil.read(stack.getTag(), TAG_BUILDER)))
          .forEach(stack -> {
              NBTTagCompound data = stack.getTag();
              NBTTagCompound newData = new NBTTagCompound();
              snapshot.keySet().forEach(f -> newData.putInt(f, snapshot.getOrDefault(f, 0)));
              data.put(TAG_WAREHOUSE_SNAPSHOT, newData);
              data.putString(TAG_WAREHOUSE_SNAPSHOT_WO_HASH, workOrderHash);
          });
    }
}


