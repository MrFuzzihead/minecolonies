package com.minecolonies.api.colony.requestsystem.requestable;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.ReflectionUtils;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Eatable requestable. Delivers a stack of food.
 */
public class Food implements IDeliverable
{
    /**
     * Set of type tokens belonging to this class.
     */
    private final static Set<TypeToken<?>> TYPE_TOKENS = ReflectionUtils.getSuperClasses(TypeToken.of(Food.class)).stream().filter(type -> !type.equals(TypeConstants.OBJECT)).collect(Collectors.toSet());

    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_COUNT  = "Count";
    private static final String NBT_RESULT = "Result";
    private static final String NBT_EXCLUSION = "Exclusion";
    private static final String NBT_MIN_NUTRITION  = "MinNutrition";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    private final int count;
    private final int minNutrition;

    private final List<ItemStorage> exclusionList = new ArrayList<>();

    @NotNull
    private ItemStack result = ItemStackUtils.EMPTY;

    public Food(final int count, final int minNutrition)
    {
        this.count = count;
        this.minNutrition = minNutrition;
    }

    public Food(final int count, @NotNull final ItemStack result, final int minNutrition)
    {
        this.count = count;
        this.result = result;
        this.minNutrition = minNutrition;
    }

    public Food(final int count, @NotNull final ItemStack result, List<ItemStorage> exclusionList, final int minNutrition)
    {
        this.count = count;
        this.result = result;
        this.exclusionList.addAll(exclusionList);
        this.minNutrition = minNutrition;
    }

    public Food(final int count, final List<ItemStorage> exclusionList, final int minNutrition)
    {
        this(count, ItemStackUtils.EMPTY, exclusionList, minNutrition);
    }

    /**
     * Serialize the deliverable.
     *
     * @param controller the controller.
     * @param food       the input.
     * @return the compound.
     */
    public static NBTTagCompound serialize(final IFactoryController controller, final Food food)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(NBT_COUNT, food.count);

        if (!ItemStackUtils.isEmpty(food.result))
        {
            compound.setTag(NBT_RESULT, food.result.writeToNBT(new net.minecraft.nbt.NBTTagCompound()));
        }
        if (!food.exclusionList.isEmpty())
        {
            @NotNull final NBTTagList items = new NBTTagList();
            for (@NotNull final ItemStorage item : food.exclusionList)
            {
                @NotNull final NBTTagCompound itemCompound = new NBTTagCompound();
                item.getItemStack().writeToNBT(itemCompound);
                items.appendTag(itemCompound);
            }
            compound.setTag(NBT_EXCLUSION, items);
        }
        compound.setInteger(NBT_MIN_NUTRITION, food.minNutrition);
        return compound;
    }

    /**
     * Deserialize the deliverable.
     *
     * @param controller the controller.
     * @param compound   the compound.
     * @return the deliverable.
     */
    public static Food deserialize(final IFactoryController controller, final NBTTagCompound compound)
    {
        final int count = compound.getInteger(NBT_COUNT);
        final ItemStack result = compound.hasKey(NBT_RESULT) ? ItemStackUtils.deserializeFromNBT(compound.getCompoundTag(NBT_RESULT)) : ItemStackUtils.EMPTY;
        final List<ItemStorage> items = new ArrayList<>();

        if (compound.hasKey(NBT_EXCLUSION))
        {
            final NBTTagList filterableItems = compound.getTagList(NBT_EXCLUSION, 10);
            for (int i = 0; i < filterableItems.tagCount(); ++i)
            {
                items.add(new ItemStorage(ItemStack.loadItemStackFromNBT(filterableItems.getCompoundTagAt(i))));
            }
        }
        final int minNutrition = compound.getInteger(NBT_MIN_NUTRITION);
        return new Food(count, result, items, minNutrition);
    }

    /**
     * Serialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the the buffer to write to.
     * @param input      the input to serialize.
     */
    public static void serialize(final IFactoryController controller, final PacketBuffer buffer, final Food input)
    {
        buffer.writeInt(input.count);

        buffer.writeBoolean(!ItemStackUtils.isEmpty(input.result));
        if (!ItemStackUtils.isEmpty(input.result))
        {
            try { buffer.writeItemStackToBuffer(input.result); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }

        buffer.writeInt(input.exclusionList.size());
        for (ItemStorage item : input.exclusionList)
        {
            try { buffer.writeItemStackToBuffer(item.getItemStack()); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }
        buffer.writeInt(input.minNutrition);
    }

    /**
     * Deserialize the deliverable.
     *
     * @param controller the controller.
     * @param buffer     the buffer to read.
     * @return the deliverable.
     */
    public static Food deserialize(final IFactoryController controller, final PacketBuffer buffer)
    {
        final int count = buffer.readInt();
        final ItemStack result = buffer.readBoolean() ? buffer.readItemStackFromBuffer() : null;

        List<ItemStorage> items = new ArrayList<>();
        final int itemsCount = buffer.readInt();
        for (int i = 0; i < itemsCount; ++i)
        {
            items.add(new ItemStorage(buffer.readItemStackFromBuffer()));
        }
        final int minNutrition = buffer.readInt();
        if (!items.isEmpty())
        {
            return new Food(count, result, items, minNutrition);
        }
        return new Food(count, result, minNutrition);
    }

    @Override
    public boolean matches(@NotNull final ItemStack stack)
    {
        return ItemStackUtils.ISFOOD.test(stack)
                 && !exclusionList.contains(new ItemStorage(stack))
                 && !(ItemStackUtils.ISCOOKABLE.test(stack) && exclusionList.contains(new ItemStorage(MinecoloniesAPIProxy.getInstance().getFurnaceRecipes().getSmeltingResult(stack))))
                 && (ItemStackUtils.ISCOOKABLE.test(stack) || (stack.getItem() instanceof net.minecraft.item.ItemFood && ((net.minecraft.item.ItemFood) stack.getItem()).func_150905_g(stack) >= minNutrition));
    }

    @Override
    public void setResult(@NotNull final ItemStack result)
    {
        this.result = result;
    }

    @Override
    public IDeliverable copyWithCount(final int newCount)
    {
        return new Food(newCount, exclusionList, minNutrition);
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public int getMinimumCount()
    {
        return 1;
    }

    @NotNull
    @Override
    public ItemStack getResult()
    {
        return result;
    }

    public List<ItemStorage> getExclusionList()
    {
        return exclusionList;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Food))
        {
            return false;
        }

        final Food food = (Food) o;

        if (getCount() != food.getCount())
        {
            return false;
        }
        return ItemStackUtils.compareItemStacksIgnoreStackSize(getResult(), food.getResult());
    }

    @Override
    public int hashCode()
    {
        int result1 = getCount();
        result1 = 31 * result1 + getResult().hashCode();
        return result1;
    }

    @Override
    public Set<TypeToken<?>> getSuperClasses()
    {
        return TYPE_TOKENS;
    }

    @Override
    public boolean canBeResolvedByBuilding()
    {
        return false;
    }
}




