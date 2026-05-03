package com.minecolonies.api.util;

// [1.7.10] world.entity removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.minecolonies.api.util.Tuple;
// [1.7.10] DyeColor -> int (dye damage values in 1.7.10)
// [1.7.10] Items -> net.minecraft.init.Items
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Utility class for summoning in fireworks.
 */

public final class FireworkUtils
{
    /**
     * Private constructor to hide the public one
     */
    private FireworkUtils()
    {

    }

    /**
     * Spawns in a given number of fireworks at the corners of a given AABB in a given world
     *
     * @param realaabb       AABB of the building
     * @param world          which world to spawn it in from
     * @param explosionLevel how many fireworks to spawn in each corner
     */
    public static void spawnFireworksAtAABBCorners(final Tuple<int[], int[]> realaabb, final World world, final int explosionLevel)
    {
        final int[] b = realaabb.getB();
        final int[] a = realaabb.getA();
        fireRocket(world, new int[]{b[0], b[1], b[2]}, explosionLevel);
        fireRocket(world, new int[]{b[0], b[1], a[2]}, explosionLevel);
        fireRocket(world, new int[]{a[0], b[1], b[2]}, explosionLevel);
        fireRocket(world, new int[]{a[0], b[1], a[2]}, explosionLevel);
    }

    /**
     * Fires a rocket at the given position, only if the sky is visible.
     *
     * @param world          which world to spawn it in.
     * @param position       the position to fire the rocket from.
     * @param explosionLevel how many fireworks to spawn in each corner.
     */
    private static void fireRocket(final World world, final int[] position, final int explosionLevel)
    {
        if (world.canBlockSeeTheSky(position[0], position[1], position[2]))
        {
            // [1.7.10] EntityFireworkRocket; no equivalent of FireworkRocketEntity in 1.7.10 easily; spawn firework item
            // TODO: spawn EntityFireworkRocket if available, or use world effects
        }
    }

    /**
     * Generates random firework with various properties.
     *
     * @param explosionAmount the amount of explosions.
     * @return ItemStack of random firework.
     */
    private static ItemStack genFireworkItemStack(final int explosionAmount)
    {
        final Random rand = new Random();
        final ItemStack fireworkItem = new ItemStack(Items.FIREWORK_ROCKET);
        final NBTTagCompound itemStackCompound = fireworkItem.getTag() != null ? fireworkItem.getTag() : new NBTTagCompound();
        final NBTTagCompound fireworksCompound = new NBTTagCompound();
        final NBTTagList explosionsTagList = new NBTTagList();
        final List<Integer> dyeColors = Arrays.stream(DyeColor.values()).map(DyeColor::getFireworkColor).collect(Collectors.toList());

        for (int i = 0; i < explosionAmount; i++)
        {
            final NBTTagCompound explosionTag = new NBTTagCompound();

            explosionTag.putBoolean(TAG_FLICKER, rand.nextInt(2) == 0);
            explosionTag.putBoolean(TAG_TRAIL, rand.nextInt(2) == 0);
            explosionTag.putInt(TAG_TYPE, rand.nextInt(5));

            final int numberOfColours = rand.nextInt(3) + 1;
            final int[] colors = new int[numberOfColours];

            for (int ia = 0; ia < numberOfColours; ia++)
            {
                colors[ia] = dyeColors.get(rand.nextInt(15));
            }
            explosionTag.putIntArray(TAG_COLORS, colors);
            explosionsTagList.add(explosionTag);
        }
        fireworksCompound.put(TAG_EXPLOSIONS, explosionsTagList);
        itemStackCompound.put(TAG_FIREWORKS, fireworksCompound);
        fireworkItem.setTag(itemStackCompound);
        return fireworkItem;
    }
}





