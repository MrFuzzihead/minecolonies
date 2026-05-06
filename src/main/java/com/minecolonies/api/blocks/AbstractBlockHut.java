package com.minecolonies.api.blocks;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

// [1.7.10 BACKPORT] Structurize interfaces kept tentatively; may need adjustment to match 1.7.10 Structurize jar.
import com.ldtteam.structurize.blocks.interfaces.*;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.structure.AbstractStructureHandler;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.blocks.interfaces.IBuildingBrowsableBlock;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.items.ItemBlockHut;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.TranslationConstants;
// [1.7.10 BACKPORT] 1.21 imports removed; 1.7.10 replacements below.
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Base class for all Minecolonies Hut Blocks. Hut Blocks are the base blocks for Minecolonies buildings.
 * Extending this class enables all the blueprint functionalities.
 *
 * [1.7.10 BACKPORT] Ported from 1.21:
 *   - int[] → int x, int y, int z
 *   - World → World
 *   - Player → EntityPlayer; EntityPlayerMP → EntityPlayerMP; EntityLivingBase → EntityLivingBase
 *   - BlockState → int meta
 *   - NBTTagCompound → NBTTagCompound
 *   - BlockEntity → TileEntity
 *   - String/String → String
 *   - @OnlyIn(Dist.CLIENT) → @SideOnly(Side.CLIENT)
 *   - registerBlockItem(IForgeRegistry<Item>, Item.Properties) → registerBlockItem() (no-arg)
 *   - Structurize interface methods: tentative – depends on 1.7.10 Structurize API.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public abstract class AbstractBlockHut<B extends AbstractBlockHut<B>> extends AbstractColonyBlock<B> implements
                                                                                                                         IAnchorBlock,
                                                                                                                         INamedBlueprintAnchorBlock,
                                                                                                                         ILeveledBlueprintAnchorBlock,
                                                                                                                         IRequirementsBlueprintAnchorBlock,
                                                                                                                         IInvisibleBlueprintAnchorBlock,
                                                                                                                         ISpecialCreativeHandlerAnchorBlock,
                                                                                                                         IBuildingBrowsableBlock
{
    /**
     * Constructor for a hut block.
     */
    public AbstractBlockHut()
    {
        super();
    }

    /**
     * Event-Handler for placement of this block by the build tool.
     *
     * @param worldIn       the world.
     * @param x             block x.
     * @param y             block y.
     * @param z             block z.
     * @param meta          block metadata (encodes facing).
     * @param placer        the entity placing the block.
     * @param stack         the ItemStack placed from.
     * @param mirror        whether the blueprint is mirrored.
     * @param style         style / pack name.
     * @param blueprintPath path within the pack.
     */
    public void onBlockPlacedByBuildTool(
      @NotNull final World worldIn,
      final int x, final int y, final int z,
      final int meta,
      final EntityLivingBase placer,
      final ItemStack stack,
      final boolean mirror,
      final String style,
      final String blueprintPath)
    {
        // [1.7.10 BACKPORT] getBlockEntity → getBlockTileEntity
        final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof AbstractTileEntityColonyBuilding)
        {
            ((AbstractTileEntityColonyBuilding) tileEntity).setMirror(mirror);
            // [1.7.10] setPackName is from IBlueprintDataProviderBE which is not implemented; skip
            // ((AbstractTileEntityColonyBuilding) tileEntity).setPackName(style);
            ((AbstractTileEntityColonyBuilding) tileEntity).setBlueprintPath(blueprintPath);
        }

        // [1.7.10 BACKPORT] setPlacedBy → onBlockPlacedBy
        onBlockPlacedBy(worldIn, x, y, z, placer, stack);
    }

    @Override
    public boolean isVisible(@Nullable final NBTTagCompound beData)
    {
        // [1.7.10 BACKPORT] NBTTagCompound → NBTTagCompound; new int[]{0,0,0} → origin key
        // TODO: verify IBlueprintDataProviderBE.readTagPosMapFrom is available in 1.7.10 Structurize
        final Map<?, List<String>> data = readTagPosMapFrom(beData.getCompoundTag(TAG_BLUEPRINTDATA));
        return !data.getOrDefault(null, new ArrayList<>()).contains("invisible");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getRequirements(final World World, final int x, final int y, final int z, final EntityPlayer player)
    {
        final List<String> requirements = new ArrayList<>();
        // [1.7.10 BACKPORT] IColonyManager.getClosestColonyView — coordinates as ints
        final IColonyView colonyView = IColonyManager.getInstance().getClosestColonyView(World, new int[]{x, y, z});
        if (colonyView == null)
        {
            requirements.add("com.minecolonies.coremod.hut.incolony");
            return requirements;
        }

        // [1.7.10 BACKPORT] InventoryUtils.findFirstSlotInItemHandlerWith → findFirstSlotInItemHandlerWith
        //   InvWrapper is a 1.21 Forge concept; replaced with player inventory check.
        boolean hasBuildingBlock = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            final ItemStack s = player.inventory.getStackInSlot(i);
            if (s != null && s.getItem() instanceof net.minecraft.item.ItemBlock
                    && ((net.minecraft.item.ItemBlock) s.getItem()).field_150939_a == this)
            {
                hasBuildingBlock = true;
                break;
            }
        }

        if (!hasBuildingBlock)
        {
            requirements.add("com.minecolonies.coremod.hut.cost");
            return requirements;
        }

        final ResourceLocation effectId = colonyView.getResearchManager().getResearchEffectIdFrom(this);
        if (colonyView.getResearchManager().getResearchEffects().getEffectStrength(effectId) > 0)
        {
            return requirements;
        }

        if (MinecoloniesAPIProxy.getInstance().getGlobalResearchTree().getResearchForEffect(effectId) != null)
        {
            requirements.add(TranslationConstants.HUT_NEEDS_RESEARCH_TOOLTIP_1);
            requirements.add(TranslationConstants.HUT_NEEDS_RESEARCH_TOOLTIP_2);
        }

        return requirements;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean areRequirementsMet(final World World, final int x, final int y, final int z, final EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            return true;
        }
        return this.getRequirements(World, x, y, z, player).isEmpty();
    }

    @Override
    public List<String> getDesc()
    {
        final List<String> desc = new ArrayList<>();
        desc.add(getBuildingEntry().getTranslationKey());
        desc.add(getBuildingEntry().getTranslationKey() + ".desc");
        return desc;
    }

    @Override
    public String getBlueprintDisplayName()
    {
        // [1.7.10 BACKPORT] String.translatable → translation key as plain String
        return getBuildingEntry().getTranslationKey();
    }

    @Override
    public int getLevel(final NBTTagCompound beData)
    {
        if (beData == null)
        {
            return 0;
        }

        try
        {
            return Integer.parseInt(beData.getCompoundTag(TAG_BLUEPRINTDATA).getString(TAG_SCHEMATIC_NAME).replaceAll("[^0-9]", ""));
        }
        catch (final NumberFormatException exception)
        {
            Log.getLogger().error("Couldn't get World from hut: " + getHutName() + ". Potential corrupt blockEntity data.");
            return 0;
        }
    }

    @Override
    public AbstractStructureHandler getStructureHandler(
      final World World,
      final int x, final int y, final int z,
      final Blueprint blueprint,
      final PlacementSettings placementSettings,
      final boolean b)
    {
        // [1.7.10 BACKPORT] CreativeBuildingStructureHandler — signature may differ; depends on Structurize 1.7.10 API.
        return new CreativeBuildingStructureHandler(World, new int[]{x, y, z}, blueprint, placementSettings, b);
    }

    @Override
    public boolean setup(
      final EntityPlayerMP player,
      final World world,
      final int x, final int y, final int z,
      final Blueprint blueprint,
      final PlacementSettings settings,
      final boolean fancyPlacement,
      final String pack,
      final String path)
    {
        // [1.7.10 BACKPORT] BlockState → int meta; blueprint.getBlockState → blueprint.getBlock/getMeta
        // TODO: verify Blueprint API in 1.7.10 Structurize
        final int anchorMeta = blueprint.getMetaAt(blueprint.getPrimaryBlockOffset());
        final Block anchorBlock = blueprint.getBlockAt(blueprint.getPrimaryBlockOffset());

        if (!(anchorBlock instanceof AbstractBlockHut<?>) || (!fancyPlacement && player.capabilities.isCreativeMode))
        {
            return true;
        }

        if (!IMinecoloniesAPI.getInstance().getConfig().getServer().blueprintBuildMode.get() && !canPaste(anchorBlock, player, x, y, z))
        {
            return false;
        }

        // [1.7.10 BACKPORT] destroyBlock → setBlock to air; setBlockAndUpdate → setBlock
        world.setBlock(x, y, z, net.minecraft.init.Blocks.air, 0, 3);
        world.setBlock(x, y, z, anchorBlock, anchorMeta, 3);

        ((AbstractBlockHut<?>) anchorBlock).onBlockPlacedByBuildTool(world,
          x, y, z,
          anchorMeta,
          player,
          null,
          settings.getMirror() != com.ldtteam.structurize.util.Mirror.NONE,
          pack,
          path);

        if (IMinecoloniesAPI.getInstance().getConfig().getServer().blueprintBuildMode.get())
        {
            return true;
        }

        @Nullable final IBuilding building = IColonyManager.getInstance().getBuilding(world, new int[]{x, y, z});
        if (building == null)
        {
            if (anchorBlock != ModBlocks.blockHutTownHall)
            {
                // [1.7.10 BACKPORT] player.blockPosition() → x,y,z
                SoundUtils.playErrorSound(player, new int[]{x, y, z});
                Log.getLogger().error("BuildTool: building is null!", new Exception());
                return false;
            }
        }
        else
        {
            SoundUtils.playSuccessSound(player, new int[]{x, y, z});
            if (building.getTileEntity() != null)
            {
                final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(world, new int[]{x, y, z});
                if (colony == null)
                {
                    // [1.7.10 BACKPORT] player.getName().getString() → player.getCommandSenderName()
                    Log.getLogger().info("No colony for " + player.getCommandSenderName());
                    return false;
                }
                else
                {
                    building.getTileEntity().setColony(colony);
                }
            }

            final String adjusted = path.replace(".blueprint", "");
            final String num = adjusted.substring(path.replace(".blueprint", "").length() - 2, adjusted.length() - 1);

            building.setStructurePack(pack);
            building.setBlueprintPath(path);
            try
            {
                building.setBuildingLevel(Integer.parseInt(num));
            }
            catch (final NumberFormatException ex)
            {
                building.setBuildingLevel(1);
            }

            // [1.7.10 BACKPORT] settings.mirror field access preserved; Mirror enum may differ in Structurize 1.7.10
            building.setIsMirrored(settings.mirror != com.ldtteam.structurize.util.Mirror.NONE);
            building.onUpgradeComplete(blueprint, building.getBuildingLevel());
        }
        return true;
    }

    /**
     * Check if we got permissions to paste.
     *
     * @param anchor the anchor block type.
     * @param player the player pasting it.
     * @param x      block x.
     * @param y      block y.
     * @param z      block z.
     * @return true if allowed.
     */
    private boolean canPaste(final Block anchor, final EntityPlayer player, final int x, final int y, final int z)
    {
        // [1.7.10 BACKPORT] player.World() → player.worldObj
        final IColony colony = IColonyManager.getInstance().getIColony(player.worldObj, new int[]{x, y, z});

        if (colony == null)
        {
            if (anchor == ModBlocks.blockHutTownHall)
            {
                return true;
            }

            if (IColonyManager.getInstance().getIColonyByOwner(player.worldObj, player) == null)
            {
                MessageUtils.format(MESSAGE_WARNING_TOWN_HALL_NOT_PRESENT).sendTo(player);
            }
            else
            {
                MessageUtils.format(MESSAGE_WARNING_TOWN_HALL_TOO_FAR_AWAY).sendTo(player);
            }

            return false;
        }
        else if (!colony.getPermissions().hasPermission(player, Action.PLACE_HUTS))
        {
            MessageUtils.format(PERMISSION_OPEN_HUT, colony.getName()).sendTo(player);
            return false;
        }
        else
        {
            return colony.getServerBuildingManager().canPlaceAt(anchor, new int[]{x, y, z}, player);
        }
    }

    /**
     * Get the blueprint name.
     *
     * @return the name.
     */
    public String getBlueprintName()
    {
        return getBuildingEntry().getRegistryName().getResourcePath();
    }

    @Override
    public void registerBlockItem()
    {
        // [1.7.10 BACKPORT] IForgeRegistry<Item> parameter removed.
        // Caller registers via GameRegistry.registerBlock(this, ItemBlockHut.class, name).
        // This method kept as no-op; registration happens in ModBlocksInitializer.
    }

    /**
     * Can this block be right-clicked without the appropriate permissions?
     *
     * @return true if so. Default false.
     */
    public boolean canRightClickWithoutPermissions()
    {
        return false;
    }

    /**
     * Check if the block can be placed at the given position by the player.
     *
     * @param x      block x.
     * @param y      block y.
     * @param z      block z.
     * @param player the player trying to place the block.
     * @return true if the block can be placed.
     */
    public boolean canPlaceAt(final int x, final int y, final int z, final EntityPlayer player)
    {
        return true;
    }
}



