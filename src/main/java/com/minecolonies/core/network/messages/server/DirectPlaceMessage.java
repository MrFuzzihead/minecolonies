package com.minecolonies.core.network.messages.server;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.storage.ServerFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.MessageUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_OTHER_LEVEL;
import static com.minecolonies.api.util.constant.TranslationConstants.WRONG_COLONY;

/**
 * Place a building directly without buildtool.
 */
public class DirectPlaceMessage implements IMessage
{
    /**
     * The state to be placed..
     */
    private BlockState state;

    /**
     * The position to place it at.
     */
    private int[] pos;

    /**
     * The stack which is going to be placed.
     */
    private ItemStack stack;

    /**
     * Empty constructor used when registering the
     */
    public DirectPlaceMessage()
    {
        super();
    }

    /**
     * Place the building.
     *
     * @param state the state to be placed.
     * @param pos   the pos to place it at.
     * @param stack the stack in the hand.
     */
    public DirectPlaceMessage(final BlockState state, final int[] pos, final ItemStack stack)
    {
        super();
        this.state = state;
        this.pos = pos;
        this.stack = stack;
    }

    /**
     * Reads this packet from a {@link PacketBuffer}.
     *
     * @param buf The buffer begin read from.
     */
    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        state = Block.stateById(buf.readInt());
        pos = buf.readBlockPos();
        stack = buf.readItem();
    }

    /**
     * Writes this packet to a {@link PacketBuffer}.
     *
     * @param buf The buffer being written to.
     */
    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(Block.getId(state));
        buf.writeBlockPos(pos);
        buf.writeItem(stack);
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
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final World world = player.getCommandSenderWorld();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(world, pos);
        InventoryUtils.reduceStackInItemHandler(new InvWrapper(player.getInventory()), stack);

        if ((colony == null && state.getBlock() == ModBlocks.blockHutTownHall) || (colony != null && colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)))
        {
            final NBTTagCompound compound = stack.getTag();
            if (colony != null && compound != null && compound.contains(TAG_COLONY_ID) && colony.getID() != compound.getInt(TAG_COLONY_ID))
            {
                MessageUtils.format(WRONG_COLONY, compound.getInt(TAG_COLONY_ID)).sendTo(player);
                return;
            }

            player.getCommandSenderWorld().setBlockAndUpdate(pos, state);
            final BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof TileEntityColonyBuilding)
            {
                ((TileEntityColonyBuilding) tileEntity).setStructurePack(StructurePacks.selectedPack);

                ServerFutureProcessor.queueBlueprint(new ServerFutureProcessor.BlueprintProcessingData(StructurePacks.findBlueprintFuture(StructurePacks.selectedPack.getName(), blueprint -> blueprint.getBlockState(blueprint.getPrimaryBlockOffset()).getBlock() == state.getBlock()), world, (blueprint -> {
                    if (blueprint == null)
                    {
                        return;
                    }
                    String fullPath = blueprint.getFilePath().toString();
                    fullPath = fullPath.replace(StructurePacks.selectedPack.getPath().toString() + "/", "");
                    ((TileEntityColonyBuilding) tileEntity).setBlueprintPath(fullPath + "/" + blueprint.getFileName().substring(0, blueprint.getFileName().length() - 1) + "1.blueprint");
                    state.getBlock().setPlacedBy(world, pos, state, player, stack);

                    if (compound != null && compound.contains(TAG_OTHER_LEVEL))
                    {
                        final IBuilding building = colony.getServerBuildingManager().getBuilding(pos);
                        if (building != null)
                        {
                            building.setBuildingLevel(compound.getInt(TAG_OTHER_LEVEL));
                            building.setDeconstructed();
                        }
                    }
                })));
            }
        }
    }
}





