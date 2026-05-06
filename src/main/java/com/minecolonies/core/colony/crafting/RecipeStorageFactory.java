package com.minecolonies.core.colony.crafting;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.crafting.IRecipeStorageFactory;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.ModRecipeTypes;
import com.minecolonies.api.crafting.RecipeStorage;
import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.constant.SerializationIdentifierConstants;
import com.minecolonies.api.util.constant.TypeConstants;
// [1.7.10] BuiltInRegistries removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.colony.requestsystem.StandardFactoryController.NBT_TYPE;
import static com.minecolonies.api.colony.requestsystem.StandardFactoryController.NEW_NBT_TYPE;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_TOKEN;

/**
 * Factory implementation taking care of creating new instances, serializing and deserializing RecipeStorages.
 */
public class RecipeStorageFactory implements IRecipeStorageFactory
{
    /**
     * Compound NBTBase for the grid size.
     */
    private static final String TAG_GRID = "grid";

    /**
     * NBTBase to store the blockstate.
     */
    private static final String BLOCK_TAG = "block";

    /**
     * Compound NBTBase for the input.
     */
    private static final String INPUT_TAG = "input";

    /**
     * Compound NBTBase for the alternate outputs.
     */
    private static final String ALTOUTPUT_TAG = "alternate-output";

    /**
     * Compound NBTBase for the alternate outputs.
     */
    private static final String SECOUTPUT_TAG = "secondary-output";

    /**
     * Compound NBTBase for Source
     */
    private static final String SOURCE_TAG = "source";

    /**
     * Compound NBTBase for Type
     */
    private static final String TYPE_TAG = "type";

    /**
     * Compound NBTBase for Loot Table
     */
    private static final String LOOT_TAG = "loot-table";

    /**
     * Compound NBTBase for Tool
     */
    private static final String TOOL_TAG = "tool";

    @NotNull
    @Override
    public TypeToken<RecipeStorage> getFactoryOutputType()
    {
        return TypeConstants.RECIPE;
    }

    @NotNull
    @Override
    public TypeToken<? extends IToken<?>> getFactoryInputType()
    {
        return TypeConstants.ITOKEN;
    }

    @NotNull
    @Override
    public NBTTagCompound serialize(@NotNull final IFactoryController controller, @NotNull final RecipeStorage recipeStorage)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        @NotNull final NBTTagList inputTagList = new NBTTagList();
        for (@NotNull final ItemStorage inputItem : recipeStorage.getInput())
        {
            @NotNull final NBTTagCompound neededRes = StandardFactoryController.getInstance().serialize(inputItem);
            inputTagList.add(neededRes);
        }
        compound.setTag(INPUT_TAG, inputTagList);
        recipeStorage.getPrimaryOutput().save(compound);

        if (recipeStorage.getIntermediate() != null)
        {
            compound.setTag(BLOCK_TAG, NbtUtils.writeBlockState(recipeStorage.getIntermediate().defaultBlockState()));
        }
        compound.setInteger(TAG_GRID, recipeStorage.getGridSize());
        compound.setTag(TAG_TOKEN, StandardFactoryController.getInstance().serialize(recipeStorage.getToken()));
        if(recipeStorage.getRecipeSource() != null)
        {
            compound.setString(SOURCE_TAG, recipeStorage.getRecipeSource().toString());
        }
        compound.setString(TYPE_TAG, recipeStorage.getRecipeType().getId().toString());

        @NotNull final NBTTagList altOutputTagList = new NBTTagList();
        for (@NotNull final ItemStack stack : recipeStorage.getAlternateOutputs())
        {
            @NotNull final NBTTagCompound neededRes = new NBTTagCompound();
            stack.save(neededRes);
            altOutputTagList.add(neededRes);
        }
        compound.setTag(ALTOUTPUT_TAG, altOutputTagList);

        @NotNull final NBTTagList secOutputTagList = new NBTTagList();
        for (@NotNull final ItemStack stack : recipeStorage.getCraftingToolsAndSecondaryOutputs())
        {
            @NotNull final NBTTagCompound neededRes = new NBTTagCompound();
            stack.save(neededRes);
            secOutputTagList.add(neededRes);
        }
        compound.setTag(SECOUTPUT_TAG, secOutputTagList);

        if(recipeStorage.getLootTable() != null)
        {
            compound.setString(LOOT_TAG, recipeStorage.getLootTable().toString());
        }

        compound.setString(TOOL_TAG, recipeStorage.getRequiredTool().getRegistryName().toString());

        return compound;
    }

    @NotNull
    @Override
    public RecipeStorage deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt)
    {
        final List<ItemStorage> input = new ArrayList<>();
        final NBTTagList inputTagList = nbt.getTagList(INPUT_TAG, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < inputTagList.size(); ++i)
        {
            final NBTTagCompound inputTag = inputTagList.getCompoundTagAt(i);
            if(inputTag.contains(NEW_NBT_TYPE) || inputTag.contains(NBT_TYPE)) //Check to see if it's something the factorycontroller can handle
            {
                input.add(StandardFactoryController.getInstance().deserialize(inputTag));
            }
            else
            {
                final ItemStorage newItem = new ItemStorage(ItemStack.loadItemStackFromNBT(inputTag));
                input.add(newItem);
            }
        }

        final ItemStack primaryOutput = ItemStack.loadItemStackFromNBT(nbt);

        final Block intermediate = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompoundTag(BLOCK_TAG)).getBlock();

        final int gridSize = nbt.getInt(TAG_GRID);
        final IToken<?> token = StandardFactoryController.getInstance().deserialize(nbt.getCompoundTag(TAG_TOKEN));

        final ResourceLocation source = nbt.contains(SOURCE_TAG) ? new ResourceLocation(nbt.getString(SOURCE_TAG)) : null; 

        final ResourceLocation type = nbt.contains(TYPE_TAG) ? new ResourceLocation(nbt.getString(TYPE_TAG).toLowerCase()): ModRecipeTypes.CLASSIC_ID;

        final NBTTagList altOutputTagList = nbt.getTagList(ALTOUTPUT_TAG, NBTBase.TAG_COMPOUND);

        final List<ItemStack> altOutputs = new ArrayList<>();
        for (int i = 0; i < altOutputTagList.size(); ++i)
        {
            final NBTTagCompound altOutputTag = altOutputTagList.getCompoundTagAt(i);
            altOutputs.add(ItemStack.loadItemStackFromNBT(altOutputTag));
        }

        final NBTTagList secOutputTagList = nbt.getTagList(SECOUTPUT_TAG, NBTBase.TAG_COMPOUND);

        final List<ItemStack> secOutputs = new ArrayList<>();
        for (int i = 0; i < secOutputTagList.size(); ++i)
        {
            final NBTTagCompound secOutputTag = secOutputTagList.getCompoundTagAt(i);
            secOutputs.add(ItemStack.loadItemStackFromNBT(secOutputTag));
        }

        final ResourceLocation lootTable = nbt.contains(LOOT_TAG) ? new ResourceLocation(nbt.getString(LOOT_TAG)) : null;
        final EquipmentTypeEntry requiredTool = ModEquipmentTypes.getRegistry().getValue(EquipmentTypeEntry.parseResourceLocation(nbt.getString(TOOL_TAG)));

        return RecipeStorage.builder()
                .withToken(token)
                .withInputs(input)
                .withGridSize(gridSize)
                .withPrimaryOutput(primaryOutput)
                .withIntermediate(intermediate)
                .withRecipeId(source)
                .withRecipeType(type)
                .withAlternateOutputs(altOutputs)
                .withSecondaryOutputs(secOutputs)
                .withLootTable(lootTable)
                .withRequiredTool(requiredTool)
                .build();
    }

    @Override
    public void serialize(@NotNull final IFactoryController controller, final RecipeStorage input, final PacketBuffer packetBuffer)
    {
        packetBuffer.writeVarInt(input.getInput().size());
        input.getInput().forEach(stack -> StandardFactoryController.getInstance().serialize(packetBuffer, stack));
        packetBuffer.writeItem(input.getPrimaryOutput());

        packetBuffer.writeBoolean(input.getIntermediate() != null);
        if (input.getIntermediate() != null)
        {
            packetBuffer.writeVarInt(Block.getId(input.getIntermediate().defaultBlockState()));
        }

        packetBuffer.writeVarInt(input.getGridSize());

        packetBuffer.writeResourceLocation(input.getRecipeType().getId());

        packetBuffer.writeVarInt(input.getAlternateOutputs().size());
        input.getAlternateOutputs().forEach(stack -> packetBuffer.writeItem(stack));

        packetBuffer.writeVarInt(input.getCraftingToolsAndSecondaryOutputs().size());
        input.getCraftingToolsAndSecondaryOutputs().forEach(stack -> packetBuffer.writeItem(stack));

        packetBuffer.writeResourceLocation(input.getRequiredTool().getRegistryName());

        packetBuffer.writeBoolean(input.getLootTable() != null);
        if(input.getLootTable() != null)
        {
            packetBuffer.writeResourceLocation(input.getLootTable());
        }

        packetBuffer.writeBoolean(input.getRecipeSource() != null);
        if (input.getRecipeSource() != null)
        {
            packetBuffer.writeResourceLocation(input.getRecipeSource());
        }

        controller.serialize(packetBuffer, input.getToken());
    }

    @NotNull
    @Override
    public RecipeStorage deserialize(@NotNull final IFactoryController controller, final PacketBuffer buffer) throws Throwable
    {
        final List<ItemStorage> input = new ArrayList<>();
        final int inputSize = buffer.readVarInt();
        for (int i = 0; i < inputSize; ++i)
        {
            input.add(StandardFactoryController.getInstance().deserialize(buffer));
        }

        final ItemStack primaryOutput = buffer.readItem();
        final Block intermediate = buffer.readBoolean() ? Block.stateById(buffer.readVarInt()).getBlock() : Blocks.AIR;
        final int gridSize = buffer.readVarInt();
        final ResourceLocation type = buffer.readResourceLocation();

        final List<ItemStack> altOutputs = new ArrayList<>();
        final int altOutputSize = buffer.readVarInt();
        for (int i = 0; i < altOutputSize; ++i)
        {
            altOutputs.add(buffer.readItem());
        }

        final List<ItemStack> secOutputs = new ArrayList<>();
        final int secOutputSize = buffer.readVarInt();
        for (int i = 0; i < secOutputSize; ++i)
        {
            secOutputs.add(buffer.readItem());
        }

        final ResourceLocation resLoc = EquipmentTypeEntry.parseResourceLocation(buffer.readResourceLocation());
        final EquipmentTypeEntry requiredTool = ModEquipmentTypes.getRegistry().getValue(resLoc);

        ResourceLocation lootTable = null;
        if(buffer.readBoolean())
        {
            lootTable = buffer.readResourceLocation();
        }

        ResourceLocation source = null;
        if(buffer.readBoolean())
        {
            source = buffer.readResourceLocation();
        }

        final IToken<?> token = controller.deserialize(buffer);
        return RecipeStorage.builder()
                .withToken(token)
                .withInputs(input)
                .withGridSize(gridSize)
                .withPrimaryOutput(primaryOutput)
                .withIntermediate(intermediate)
                .withRecipeId(source)
                .withRecipeType(type)
                .withAlternateOutputs(altOutputs)
                .withSecondaryOutputs(secOutputs)
                .withLootTable(lootTable)
                .withRequiredTool(requiredTool)
                .build();
    }

    @Override
    public short getSerializationId()
    {
        return SerializationIdentifierConstants.RECIPE_STORAGE_ID;
    }
}





