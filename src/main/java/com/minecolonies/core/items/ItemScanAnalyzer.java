package com.minecolonies.core.items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.block.state.BlockState;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.blueprints.v1.BlueprintUtil;
import com.ldtteam.structurize.client.rendertask.RenderTaskManager;
import com.ldtteam.structurize.client.rendertask.tasks.BoxPreviewData;
import com.ldtteam.structurize.client.rendertask.tasks.BoxPreviewRenderTask;
import com.ldtteam.structurize.items.AbstractItemWithPosSelector;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.client.gui.WindowSchematicAnalyzer;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] NbtUtils removed
import net.minecraft.util.IChatComponent;
// [1.7.10] int /* InteractionHand */ removed
// [1.7.10] InteractionResult -> boolean
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Properties;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.phys removed
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.ldtteam.structurize.api.util.constant.NbtTagConstants.FIRST_POS_STRING;
import static com.ldtteam.structurize.api.util.constant.NbtTagConstants.SECOND_POS_STRING;
import static com.ldtteam.structurize.api.util.constant.TranslationConstants.MAX_SCHEMATIC_SIZE_REACHED;

/**
 * Item used to analyze schematics or selected blocks
 */
public class ItemScanAnalyzer extends AbstractItemWithPosSelector
{
    /**
     * NBT constants
     */
    public static String TEMP_SCAN = "selection.blueprint";
    public static String LAST_TIME = "lastworldtime";

    /**
     * Time after which the selection is ignored
     */
    private static final int TIMEOUT_DELAY = 20 * 60 * 2;

    /**
     * Client side selection caching
     */
    private static int[]  lastPos   = new int[]{0,0,0};
    private static int[]  lastPos2  = new int[]{0,0,0};
    public static  Blueprint blueprint = null;

    public ItemScanAnalyzer(
      @NotNull final String name,
      final Properties properties)
    {
        super(properties.durability(0).setNoRepair().rarity(Rarity.UNCOMMON));
    }

    /**
     * MC constructor.
     *
     * @param properties properties
     */
    public ItemScanAnalyzer(final Properties properties)
    {
        super(properties);
    }

    /**
     * Structurize: Prevent block breaking server side.
     * {@inheritDoc}
     */
    @Override
    public boolean canAttackBlock(final BlockState state, final World worldIn, final int[] pos, final EntityPlayer player)
    {
        checkTimeout(player.getMainHandItem(), worldIn);
        boolean result = super.canAttackBlock(state, worldIn, pos, player);
        openAreaBox(player.getMainHandItem());
        return result;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context)
    {
        checkTimeout(context.getItemInHand(), context.getLevel());
        InteractionResult result = super.useOn(context);
        openAreaBox(context.getItemInHand());
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final World worldIn, final EntityPlayer playerIn, final int /* InteractionHand */ handIn)
    {
        checkTimeout(playerIn.getItemInHand(handIn), worldIn);

        final ItemStack itemstack = playerIn.getItemInHand(handIn);
        final NBTTagCompound compound = itemstack.getOrCreateTag();

        int[] firstPos = null;
        if (compound.hasKey(FIRST_POS_STRING))
        {
            firstPos = NbtUtils.readBlockPos(compound.getCompoundTag(FIRST_POS_STRING));
        }

        int[] secondPos = null;
        if (compound.hasKey(SECOND_POS_STRING))
        {
            secondPos = NbtUtils.readBlockPos(compound.getCompoundTag(SECOND_POS_STRING));
        }

        return new InteractionResultHolder<>(
          onAirRightClick(
            firstPos,
            secondPos,
            worldIn,
            playerIn,
            itemstack),
          itemstack);
    }

    @Override
    public InteractionResult onAirRightClick(final int[] start, final int[] end, final World worldIn, final EntityPlayer playerIn, final ItemStack itemStack)
    {
        if (worldIn.isClientSide)
        {
            if (start != null && end != null && (!lastPos.equals(start) || !lastPos2.equals(end)))
            {
                lastPos = start;
                lastPos2 = end;

                blueprint = saveStructure(worldIn, playerIn, new AABB(getBounds(itemStack).getA(), getBounds(itemStack).getB()));
            }

            new WindowSchematicAnalyzer().open();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public AbstractItemWithPosSelector getRegisteredItemInstance()
    {
        return (AbstractItemWithPosSelector) ModItems.scanAnalyzer;
    }

    /**
     * Opens an area selection for the selected positions
     *
     * @param tool
     */
    private void openAreaBox(final ItemStack tool)
    {
        final NBTTagCompound NBTBase = tool.getOrCreateTag();
        if (NBTBase.contains(FIRST_POS_STRING) && NBTBase.contains(SECOND_POS_STRING))
        {
            final int[] start = NbtUtils.readBlockPos(NBTBase.getCompoundTag(FIRST_POS_STRING));
            final int[] end = NbtUtils.readBlockPos(NBTBase.getCompoundTag(SECOND_POS_STRING));
            RenderTaskManager.addRenderTask("analyzer", new BoxPreviewRenderTask("analyzer",
                new BoxPreviewData(start, end, Optional.empty()), 10 * 60));
        }
    }

    /**
     * Checks the selection timeout
     */
    protected void checkTimeout(final ItemStack stack, final World World)
    {
        if (stack == null || World == null)
        {
            return;
        }

        if (stack.getOrCreateTag().contains(LAST_TIME))
        {
            final long prevTime = stack.getOrCreateTag().getLong(LAST_TIME);
            if ((world.getTotalWorldTime() - prevTime) > TIMEOUT_DELAY)
            {
                stack.getOrCreateTag().remove(FIRST_POS_STRING);
                stack.getOrCreateTag().remove(SECOND_POS_STRING);
            }
        }

        stack.getOrCreateTag().putLong(LAST_TIME, world.getTotalWorldTime());
    }

    /**
     * Scan the structure and save it as blueprint
     *
     * @param world  Current world.
     * @param player causing this action.
     */
    public static Blueprint saveStructure(final World world, final EntityPlayer player, AABB box)
    {
        if (box.getXsize() * box.getYsize() * box.getZsize() > Structurize.getConfig().getServer().schematicBlockLimit.get())
        {
            player.displayClientMessage(String.translatable(MAX_SCHEMATIC_SIZE_REACHED, Structurize.getConfig().getServer().schematicBlockLimit.get()), false);
            return null;
        }

        final String fileName = TEMP_SCAN;
        final int[] zero = new int[]{(int) box.minX, (int) box.minY, (int) box.minZ};
        final Blueprint bp =
          BlueprintUtil.createBlueprint(world, zero, false, (short) (box.getXsize() + 1), (short) (box.getYsize() + 1), (short) (box.getZsize() + 1), fileName, Optional.empty());

        return bp;
    }
}







