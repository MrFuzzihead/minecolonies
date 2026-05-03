package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.items.ItemCrop;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.minecolonies.api.research.util.ResearchConstants.GREEN_REVOLUTION;
import static com.minecolonies.api.util.constant.Constants.UPDATE_FLAG;

/**
 * Abstract Minecolonies crop type. We have our own to avoid cheesing the crops.
 */
public class MinecoloniesCropBlock extends AbstractBlockMinecolonies<MinecoloniesCropBlock>
{
    public static String BELL_PEPPER = "bell_pepper";
    public static String CABBAGE = "cabbage";
    public static String CHICKPEA = "chickpea";
    public static String DURUM = "durum";
    public static String EGGPLANT = "eggplant";
    public static String GARLIC = "garlic";
    public static String ONION = "onion";
    public static String SOYBEAN = "soybean";
    public static String TOMATO = "tomato";
    public static String RICE = "rice";

    public static String BUTTERNUT_SQUASH = "butternut_squash";
    public static String CORN = "corn";
    public static String MINT = "mint";
    public static String NETHER_PEPPER = "nether_pepper";
    public static String PEAS = "peas";

    /** AGE stored as metadata 0-6 */
    public static final int MAX_AGE = 6;

    private final Block preferredFarmland;
    private final List<Block> droppedFrom;

    private final ResourceLocation blockId;

    /**
     * Constructor to create a block of this type.
     * @param blockName the block id.
     */
    public MinecoloniesCropBlock(final String blockName, final Block preferredFarmland, final List<Block> droppedFrom)
    {
        super(Material.plants);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.25f, 1.0f);
        this.blockId = new ResourceLocation(Constants.MOD_ID, blockName);
        this.preferredFarmland = preferredFarmland;
        this.droppedFrom = droppedFrom;
        setStepSound(soundTypeGrass);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return blockId;
    }

    @Override
    public void registerBlockItem()
    {
        net.minecraftforge.fml.common.registry.GameRegistry.registerItem(new ItemCrop(this), blockId.getResourcePath());
    }

    /**
     * Check if the block is of max age.
     * @param meta the metadata its at.
     * @return true if max age.
     */
    public final boolean isMaxAge(int meta)
    {
        return meta >= this.getMaxAge();
    }

    /**
     * Get the default max crop age.
     * @return the max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Method to be called to attempt grow this crop.
     * @param world World its in.
     * @param x x coord.
     * @param y y coord.
     * @param z z coord.
     */
    public void attemptGrow(World world, int x, int y, int z)
    {
        if (world.isRemote) return;
        if (world.getBlockLightValue(x, y, z) >= 9)
        {
            final int meta = world.getBlockMetadata(x, y, z);
            if (meta < this.getMaxAge())
            {
                world.setBlockMetadataWithNotify(x, y, z, meta + 1, UPDATE_FLAG);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        final ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() != null && meta >= this.getMaxAge())
        {
            // Harvest logic: drop items and reset to age 0
            if (!world.isRemote)
            {
                dropBlockAsItem(world, x, y, z, meta, 0);
                world.setBlockMetadataWithNotify(x, y, z, 0, UPDATE_FLAG);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return world.getBlockLightValue(x, y, z) >= 8
            && world.getBlock(x, y - 1, z) == preferredFarmland;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        final int meta = world.getBlockMetadata(x, y, z);
        final float height = 0.125f + meta * 0.125f;
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return 6; // cross/crop render type
    }

    /**
     * Get the preferred farmland for this crop.
     * @return the preferred farmland.
     */
    public Block getPreferredFarmland()
    {
        return preferredFarmland;
    }

    /**
     * Get the blocks that this crop drops from.
     */
    public List<Block> getDroppedFrom()
    {
        return droppedFrom;
    }
}
