package com.minecolonies.core.blocks;

import com.minecolonies.api.blocks.AbstractBlockMinecolonies;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.minecolonies.api.util.constant.CitizenConstants.BLOCK_BREAK_SOUND_RANGE;

/**
 * Custom farmland block that grows MinecoloniesCropBlock crops faster during rain.
 * [1.7.10] Ported: BlockState→metadata; ServerLevel→World; RandomSource→Random; VoxelShape→setBlockBounds; FluidState removed.
 */
public class MinecoloniesFarmland extends AbstractBlockMinecolonies<MinecoloniesFarmland>
{
    public static final String FARMLAND         = "farmland";
    public static final String FLOODED_FARMLAND = "floodedfarmland";

    /** Metadata 0-7 stores moisture level (7=fully moist). */
    private static final int MAX_MOISTURE = 7;

    private final ResourceLocation blockId;
    private final boolean waterLogged;

    public MinecoloniesFarmland(@NotNull final String blockName, final boolean waterLogged, final double height)
    {
        super(net.minecraft.block.material.Material.ground);
        this.setHardness(0.6F);
        this.setStepSound(Block.soundTypeGravel);
        this.blockId = new ResourceLocation(Constants.MOD_ID, blockName);
        this.waterLogged = waterLogged;
        // Slightly shorter than a full block
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) height / 16.0F, 1.0F);
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return blockId;
    }

    @Override
    public boolean getTickRandomly()
    {
        return true;
    }

    @Override
    public void updateTick(final World world, final int x, final int y, final int z, final Random rand)
    {
        final int moisture = world.getBlockMetadata(x, y, z);

        // Dehydrate if no water nearby and not raining
        if (!world.isRaining() && !isNearWater(world, x, y, z) && !waterLogged)
        {
            if (moisture > 0)
            {
                world.setBlockMetadataWithNotify(x, y, z, moisture - 1, 2);
            }
            else if (!shouldMaintainFarmland(world, x, y, z))
            {
                turnToDirt(world, x, y, z);
            }
        }
        else if (moisture < MAX_MOISTURE)
        {
            world.setBlockMetadataWithNotify(x, y, z, MAX_MOISTURE, 2);
        }

        // Crop growth
        final Block above = world.getBlock(x, y + 1, z);
        int growthChance = 4;
        if (world.isRaining())
        {
            growthChance = 12;
        }
        if (above instanceof MinecoloniesCropBlock cropBlock && rand.nextInt(100) <= growthChance)
        {
            cropBlock.attemptGrow(world, x, y + 1, z);
            // [1.7.10] particle notification via network message
            Network.getNetwork().sendToPosition(
                new VanillaParticleMessage(x + 0.5F, y + 0.5F, z + 0.5F, 0 /* HAPPY_VILLAGER */),
                new cpw.mods.fml.common.network.NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, BLOCK_BREAK_SOUND_RANGE));
        }
    }

    @Override
    public void onFallenUpon(final World world, final int x, final int y, final int z, final Entity entity, final float fallDistance)
    {
        super.onFallenUpon(world, x, y, z, entity, fallDistance);
        if (!world.isRemote && fallDistance > 0.5F)
        {
            turnToDirt(world, x, y, z);
        }
    }

    /**
     * Turns this farmland to dirt.
     */
    public static void turnToDirt(final World world, final int x, final int y, final int z)
    {
        world.setBlock(x, y, z, Blocks.dirt, 0, 3);
    }

    /**
     * Returns true if there is a plant on top that can sustain the farmland.
     */
    private static boolean shouldMaintainFarmland(final World world, final int x, final int y, final int z)
    {
        final Block plant = world.getBlock(x, y + 1, z);
        return plant instanceof IPlantable && Blocks.farmland.canSustainPlant(world, x, y, z, net.minecraft.util.EnumFacing.UP, (IPlantable) plant);
    }

    /**
     * Returns true if water is within 4 blocks horizontally at the same or one block below level.
     */
    private static boolean isNearWater(final World world, final int px, final int py, final int pz)
    {
        for (int dx = -4; dx <= 4; dx++)
        {
            for (int dz = -4; dz <= 4; dz++)
            {
                for (int dy = -1; dy <= 0; dy++)
                {
                    final Block b = world.getBlock(px + dx, py + dy, pz + dz);
                    if (b == Blocks.water || b == Blocks.flowing_water)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getHutName()
    {
        return blockId.getResourcePath();
    }
}
