package com.minecolonies.core.network.messages.client;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.client.gui.WindowSuggestBuildTool;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] int[] -> int x,y,z
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Open the suggestion window.
 */
public class OpenSuggestionWindowMessage implements IMessage
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
    public OpenSuggestionWindowMessage()
    {
        super();
    }

    /**
     * Open the window.
     *
     * @param state the state to be placed.
     * @param pos   the pos to place it at.
     * @param stack the stack in the hand.
     */
    public OpenSuggestionWindowMessage(final BlockState state, final int[] pos, final ItemStack stack)
    {
        super();
        this.state = state;
        this.pos = pos;
        this.stack = stack;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        state = Block.stateById(buf.readInt());
        pos = buf.readBlockPos();
        stack = buf.readItem();
    }

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
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        new WindowSuggestBuildTool(pos, state, stack).open();
    }
}


