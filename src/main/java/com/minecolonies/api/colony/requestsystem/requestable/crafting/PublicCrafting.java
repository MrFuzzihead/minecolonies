package com.minecolonies.api.colony.requestsystem.requestable.crafting;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class PublicCrafting extends AbstractCrafting
{
    /**
     * Set of type tokens belonging to this class.
     */
    private final static Set<TypeToken<?>> TYPE_TOKENS = ReflectionUtils.getSuperClasses(TypeToken.of(PublicCrafting.class)).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

    /**
     * Create a Stack deliverable.
     *
     * @param stack the required stack.
     * @param count the crafting count.
     * @param minCount the min count.
     */
    public PublicCrafting(@NotNull final ItemStack stack, final int count, final int minCount, final IToken<?> recipeToken)
    {
        super(stack, count, minCount, recipeToken);
    }

    /**
     * Create a Stack deliverable.
     *
     * @param stack the required stack.
     * @param count the crafting count.
     */
    public PublicCrafting(@NotNull final ItemStack stack, final int count, final IToken<?> recipeToken)
    {
        super(stack, count, count, recipeToken);
    }

    /**
     * Serialize the deliverable.
     *
     * @param controller the controller.
     * @param input      the input.
     * @return the compound.
     */
    public static NBTTagCompound serialize(final IFactoryController controller, final PublicCrafting input)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setTag(NBT_STACK, input.getStack().writeToNBT(new net.minecraft.nbt.NBTTagCompound()));
        compound.setInteger(NBT_COUNT, input.getCount());
        final NBTTagCompound tokenCompound = StandardFactoryController.getInstance().serialize(input.getRecipeID());
        compound.setTag(NBT_TOKEN, tokenCompound);
        return compound;
    }

    /**
     * Deserialize the deliverable.
     *
     * @param controller the controller.
     * @param compound   the compound.
     * @return the deliverable.
     */
    public static PublicCrafting deserialize(final IFactoryController controller, final NBTTagCompound compound)
    {
        final ItemStack stack = ItemStackUtils.deserializeFromNBT(compound.getCompoundTag(NBT_STACK));
        final int count = compound.getInteger(NBT_COUNT);
        IToken<?> token = null;
        if (compound.hasKey(NBT_TOKEN))
        {
            token = StandardFactoryController.getInstance().deserialize(compound.getCompoundTag(NBT_TOKEN));
        }
        return new PublicCrafting(stack, count, token);
    }

    /**
     * Serialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the the buffer to write to.
     * @param input      the input to serialize.
     */
    public static void serialize(final IFactoryController controller, final PacketBuffer buffer, final PublicCrafting input)
    {
        try { buffer.writeItemStackToBuffer(input.getStack()); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        buffer.writeInt(input.getCount());
        StandardFactoryController.getInstance().serialize(buffer, input.getRecipeID());
    }

    /**
     * Deserialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the buffer to read.
     * @return the deliverable.
     */
    public static PublicCrafting deserialize(final IFactoryController controller, final PacketBuffer buffer)
    {
        final ItemStack stack;
        try { stack = buffer.readItemStackFromBuffer(); } catch (java.io.IOException e) { throw new RuntimeException(e); }

        final int count = buffer.readInt();
        final IToken<?> token = StandardFactoryController.getInstance().deserialize(buffer);

        return new PublicCrafting(stack, count, token);
    }

    @Override
    public Set<TypeToken<?>> getSuperClasses()
    {
        return TYPE_TOKENS;
    }
}



