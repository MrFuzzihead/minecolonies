package com.minecolonies.core.entity.ai.workers.util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.Direction;
import net.minecraft.block.state.BlockState;

import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.compatibility.Compatibility;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.BlockStateUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.MineColonies;
// [1.7.10] int[] -> int x,y,z
// import net.minecraft.core.java.util.List; // [1.7.10] wrong import
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.WorldServer;
// [1.7.10] tags removed
// [1.7.10] tags removed
import com.minecolonies.api.util.Tuple;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
// [1.7.10] ChunkPos removed
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
// [1.7.10] IBlockAccess removed
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockLeaves;
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.World.storage removed
// [1.7.10] world.World.storage removed
// [1.7.10] world.phys removed
// [1.7.10] Tags removed
// [1.7.10] registries removed

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// [1.7.10] ModTags.fungi not available
// import static com.minecolonies.api.items.ModTags.fungi;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Custom class for Trees. Used by lumberjack
 */
public class Tree
{
    /**
     * Radius propertyname for dynamic trees, used to check growth status
     */
    private static final String DYNAMICTREERADIUS = "radius";

    /**
     * Number of leaves necessary for a tree to be recognized.
     */
    private static final int NUMBER_OF_LEAVES = 3;

    /**
     * Number of leaves in every direction from the middle of the tree.
     */
    private static final int LEAVES_WIDTH = 4;

    /**
     * A lot of luck to get a guaranteed saplings drop.
     */
    private static final int A_LOT_OF_LUCK = 10000;

    /**
     * The location of the tree stump.
     */
    private int[] location;

    /**
     * The location of the tree stump.
     */
    private int[] topLog;

    /**
     * All wood blocks connected to the tree.
     */
    private LinkedList<int[]> woodBlocks = new LinkedList<>();

    /**
     * All leaves of the tree.
     */
    private LinkedList<int[]> leaves = new LinkedList<>();

    /**
     * Is the tree a tree?
     */
    private boolean isTree;

    /**
     * The spaling the lj has to use to replant the tree.
     */
    private ItemStack sapling;

    /**
     * The locations of the stumps (Some trees are connected to dirt by 4 logs).
     */
    private ArrayList<int[]> stumpLocations;

    /**
     * The wood variant of the Tree. Can change depending on Mod
     * [1.7.10] Object not available; store as Object for compatibility
     */
    private Object variant;

    /**
     * If the Tree is a Slime Tree.
     */
    private boolean slimeTree = false;

    /**
     * If the tree is a Dynamic Tree
     */
    private boolean dynamicTree = false;

    /**
     * If the tree is a Nether Tree
     */
    private boolean netherTree = false;

    /**
     * Private constructor of the tree. Used by the equals and createFromNBt method.
     */
    private Tree()
    {
        isTree = true;
    }

    /**
     * Creates a new tree Object for the lumberjack. Since the same type of variant of the block old log or new log do not match we have to separate them.
     *
     * @param world  The world where the tree is in.
     * @param log    the position of the found log.
     * @param colony the colony to search for buildings, or null if we don't care.
     */
    public Tree(@NotNull final World world, @NotNull final int[] log, @Nullable final IColony colony)
    {
        final BlockState block = BlockPosUtil.getBlockState(world, log);
        if (block.is(ModTags.tree) || Compatibility.isSlimeBlock(block.getBlock()) || Compatibility.isDynamicBlock(block.getBlock()))
        {
            isTree = true;
            woodBlocks = new LinkedList<>();
            leaves = new LinkedList<>();
            location = log;
            topLog = log;

            addAndSearch(world, log, colony);
            addAndSearch(world);

            checkTree(world, topLog);

            dynamicTree = Compatibility.isDynamicBlock(block.getBlock());
            stumpLocations = new ArrayList<>();
            woodBlocks.clear();
            slimeTree = Compatibility.isSlimeBlock(block.getBlock());
            sapling = calcSapling(world);
            if (sapling.is(Tags.Items.MUSHROOMS) || sapling.is(fungi))
            {
                netherTree = true;
            }

            // Calculate the Tree's variant Property, add mod compat for other property names later when needed
            variant = BlockStateUtils.getPropertyByNameFromState(world.getBlockState(location), "variant");
        }
        else
        {
            isTree = false;
        }
    }

    /**
     * Checks the leaf above the highest Log for drops
     *
     * @param world world the tree is in
     * @return ItemStack of the sapling found
     */
    private ItemStack calcSapling(final World world)
    {
        if (topLog == null)
        {
            return null;
        }

        ItemStack sapling;

        // Try leaf directly above the tree base first
        final int[] firstLeaf = getFirstLeaf(world);
        sapling = calcSaplingForPos(world, firstLeaf, true);
        if (sapling != null)
        {
            return sapling;
        }

        // Try all leaves found related to the tree
        for (final int[] pos : leaves)
        {
            sapling = calcSaplingForPos(world, pos, true);

            if (sapling != null)
            {
                return sapling;
            }
        }

        // Get sapling from Leaf above the treebase without checking compability
        sapling = calcSaplingForPos(world, firstLeaf, false);
        if (sapling != null)
        {
            return sapling;
        }

        return ItemStackUtils.EMPTY;
    }

    /**
     * Calculates a sapling from the leaf at the given position
     *
     * @param world         world to use for accessing blocks
     * @param pos           Blockposition of the leaf
     * @param checkFitsBase boolean whether we should check leaf and tree's log compatibility
     * @return the sapling to plant at the given position
     */
    private ItemStack calcSaplingForPos(final World world, final int[] pos, final boolean checkFitsBase)
    {
        BlockState blockState = world.getBlockState(pos);
        final Block block = blockState.getBlock();

        if (blockState.is(BlockTags.LEAVES) || Compatibility.isDynamicLeaf(block) || blockState.is(ModTags.hugeMushroomBlocks))
        {
            java.util.List<ItemStack> list = java.util.List.create();

            if (checkFitsBase)
            {
                // Check if the tree's base log variant fits the leaf
                if (Compatibility.isDynamicLeaf(block))
                {
                    if (!isDynamicTree() || !Compatibility.isDynamicFamilyFitting(pos, location, world))
                    {
                        return null;
                    }
                }
            }

            // Dynamic trees is using a custom Drops function
            if (Compatibility.isDynamicLeaf(block))
            {
                list = Compatibility.getDropsForDynamicLeaf(world, pos, blockState, A_LOT_OF_LUCK, block);
            }
            else
            {
                list.addAll(getSaplingsForLeaf((ServerLevel) world, pos));
            }

            for (final ItemStack stack : list)
            {
                // Skip bad stacks from drops calc
                if (stack.isEmpty())
                {
                    continue;
                }

                if (stack.is(ItemTags.SAPLINGS) || stack.is(Tags.Items.MUSHROOMS))
                {
                    IColonyManager.getInstance().getCompatibilityManager().connectLeafToSapling(block, stack);
                    return stack;
                }
            }
        }
        else if (blockState.is(BlockTags.WART_BLOCKS))
        {
            return IColonyManager.getInstance().getCompatibilityManager().getSaplingForLeaf(block);
        }
        return null;
    }

    /**
     * Fills the list of drops for a leaf.
     *
     * @param world    world reference
     * @param position position of the leaf
     * @return the list of saplings.
     */
    public static List<ItemStack> getSaplingsForLeaf(World world, int[] position)
    {
        // [1.7.10] LootParams/LootContextParams not available. Use vanilla block drops instead.
        List<ItemStack> list = new ArrayList<>();
        // Get drops with a wooden axe equivalent (fortune 100 simulated via 100 rolls)
        final Block block = world.getBlock(position[0], position[1], position[2]);
        final int meta = world.getBlockMetadata(position[0], position[1], position[2]);
        if (block != null && block != net.minecraft.init.Blocks.air)
        {
            for (int i = 0; i < 20; i++)
            {
                final java.util.List<ItemStack> drops = block.getDrops(world, position[0], position[1], position[2], meta, 0);
                list.addAll(drops);
                if (!list.isEmpty())
                {
                    return list;
                }
            }
        }
        return list;
    }

    /**
     * Returns the first Leaf above the toplog
     *
     * @param world the world to search in
     * @return leaf pos found
     */
    private int[] getFirstLeaf(final IBlockAccess world)
    {
        // Find the closest leaf above, stay below max height (256 in 1.7.10)
        for (int i = 1; (i + topLog[1]) < 256 && i < 10; i++)
        {
            final int[] pos = new int[]{topLog[0], topLog[1] + i, topLog[2]};
            final BlockState blockState = BlockState.of(world, pos[0], pos[1], pos[2]);
            if (blockState.getBlock() instanceof BlockLeaves
                || blockState.getBlock() == Blocks.brown_mushroom_block
                || blockState.getBlock() == Blocks.red_mushroom_block)
            {
                return pos;
            }
        }
        return new int[]{topLog[0], topLog[1] + 1, topLog[2]};
    }

    /**
     * For use in PathJobFindTree.
     *
     * @param world         the world.
     * @param pos           The coordinates.
     * @param treesToNotCut the trees the lumberjack is not supposed to cut.
     * @param dyntreesize   the radius a dynamic tree must have in order to get cut down.
     * @return true if the log is part of a tree.
     */
    public static boolean checkTree(@NotNull final IBlockAccess world, final int[] pos, final List<ItemStorage> treesToNotCut, final int dyntreesize)
    {
        //Is the first block a log?
        final BlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();
        if (!state.is(ModTags.tree) && !Compatibility.isSlimeBlock(block) && !Compatibility.isDynamicBlock(block))
        {
            return false;
        }

        // Only harvest nearly fully grown dynamic trees(8 max)
        if (Compatibility.isDynamicBlock(block)
              && BlockStateUtils.getPropertyByNameFromState(state, DYNAMICTREERADIUS) != null
              && ((Integer) state.getValue(BlockStateUtils.getPropertyByNameFromState(state, DYNAMICTREERADIUS)) < dyntreesize))
        {
            return false;
        }

        final Tuple<int[], int[]> baseAndTOp = getBottomAndTopLog(world, pos, new LinkedList<>(), null, null);

        //Get base log, should already be base log.
        final int[] basePos = baseAndTOp.getA();

        //Make sure tree is on solid ground and tree is not build above cobblestone.
        return BlockUtils.isAnySolid(world.getBlockState(basePos.below()))
                 && world.getBlockState(basePos.below()).getBlock() != Blocks.COBBLESTONE
                 && hasEnoughLeavesAndIsSupposedToCut(world, baseAndTOp.getB(), treesToNotCut);
    }

    /**
     * Adds a log and searches for further logs(Breadth first search).
     *
     * @param world        The world the log is in.
     * @param log          The log to add.
     * @param woodenBlocks The wooden blocks.
     * @param bottomLog    The bottom most log.
     * @param topLog       The top most log.
     * @return a tuple containing, first: bottom log and second: top log.
     */
    @NotNull
    private static Tuple<int[], int[]> getBottomAndTopLog(
      @NotNull final IBlockAccess world,
      @NotNull final int[] log,
      @NotNull final LinkedList<int[]> woodenBlocks,
      final int[] bottomLog,
      final int[] topLog)
    {
        int[] bottom = bottomLog == null ? log : bottomLog;
        int[] top = topLog == null ? log : topLog;

        if (woodenBlocks.size() >= MineColonies.getConfig().getServer().maxTreeSize.get())
        {
            return new Tuple<>(bottom, top);
        }

        if (log[1] < bottom.getY())
        {
            bottom = log;
        }

        if (log[1] > top.getY())
        {
            top = log;
        }

        woodenBlocks.add(log);
        for (int y = -1; y <= 1; y++)
        {
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    final int[] temp = log.offset(x, y, z);
                    final BlockState block = world.getBlockState(temp);
                    if ((block.is(ModTags.tree) || Compatibility.isSlimeBlock(block.getBlock()) || Compatibility.isDynamicBlock(block.getBlock())) && !woodenBlocks.contains(temp))
                    {
                        return getBottomAndTopLog(world, temp, woodenBlocks, bottom, top);
                    }
                }
            }
        }

        return new Tuple<>(bottom, top);
    }

    /**
     * Check if the tree has enough leaves and the lj is supposed to cut them.
     *
     * @param world         the world it is in.
     * @param pos           the position.
     * @param treesToNotCut the trees the lj is not supposed to cut.
     * @return true if so.
     */
    private static boolean hasEnoughLeavesAndIsSupposedToCut(@NotNull final IBlockAccess world, final int[] pos, final List<ItemStorage> treesToNotCut)
    {
        boolean checkedLeaves = false;
        int leafCount = 0;
        int dynamicBonusY = 0;
        final BlockState blockState = world.getBlockState(pos);
        // Additional leaf search range for dynamic trees, as we start from the baselog
        if (blockState.is(ModTags.mangroveTree) || Compatibility.isDynamicBlock(blockState.getBlock()))
        {
            dynamicBonusY = 8;
        }

        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dz = -1; dz <= 1; dz++)
            {
                for (int dy = -3; dy <= 3 + dynamicBonusY; dy++)
                {
                    final int[] leafPos = pos.offset(dx, dy, dz);
                    final BlockState block = world.getBlockState(leafPos);
                    if (block.is(BlockTags.LEAVES) || block.is(ModTags.hugeMushroomBlocks) || block.is(BlockTags.WART_BLOCKS))
                    {
                        if (!checkedLeaves && !supposedToCut(world, treesToNotCut, leafPos))
                        {
                            return false;
                        }
                        checkedLeaves = true;

                        leafCount++;
                        // Dynamic tree growth is checked by radius instead of leafcount
                        if (leafCount >= NUMBER_OF_LEAVES || (Compatibility.isDynamicLeaf(block.getBlock())))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the Lj is supposed to cut a tree.
     *
     * @param world         the world it is in.
     * @param treesToNotCut the trees he is not supposed to cut.
     * @param leafPos       the position a leaf is at.
     * @return false if not.
     */
    private static boolean supposedToCut(final IBlockAccess world, final List<ItemStorage> treesToNotCut, final int[] leafPos)
    {
        final BlockState leaf = world.getBlockState(leafPos);
        if (leaf.getOptionalValue(null /* [1.7.10] no PERSISTENT property */).orElse(false))
        {
            return false;
        }

        // sadly this is called from pathfinding so can't directly access server loot; this means if the sapling
        // isn't already cached (which it won't be the very first time we encounter a new tree type for this colony)
        // then we will try to chop it regardless of hut settings.  but the *second* tree of that type we should
        // obey the settings properly.
        final ItemStack sap = IColonyManager.getInstance().getCompatibilityManager().getSaplingForLeaf(leaf.getBlock());

        if (sap == null)
        {
            return true;
        }

        for (final ItemStorage stack : treesToNotCut)
        {
            if (ItemStackUtils.compareItemStacksIgnoreStackSize(sap, stack.getItemStack()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Reads the tree object from NBT.
     *
     * @param compound the compound of the tree.
     * @return a new tree object.
     */
    @NotNull
    public static Tree read(@NotNull final NBTTagCompound compound)
    {
        @NotNull final Tree tree = new Tree();
        tree.location = BlockPosUtil.read(compound, TAG_LOCATION);

        tree.woodBlocks = new LinkedList<>();
        final NBTTagList logs = compound.getTagList(TAG_LOGS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < logs.size(); i++)
        {
            tree.woodBlocks.add(BlockPosUtil.readFromListNBT(logs, i));
        }

        tree.stumpLocations = new ArrayList<>();
        final NBTTagList stumps = compound.getTagList(TAG_STUMPS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < stumps.size(); i++)
        {
            tree.stumpLocations.add(BlockPosUtil.readFromListNBT(stumps, i));
        }

        tree.topLog = BlockPosUtil.read(compound, TAG_TOP_LOG);

        tree.slimeTree = compound.getBoolean(TAG_IS_SLIME_TREE);
        tree.dynamicTree = compound.getBoolean(TAG_DYNAMIC_TREE);

        if (compound.hasKey(TAG_SAPLING))
        {
            tree.sapling = ItemStack.loadItemStackFromNBT(compound.getCompoundTag(TAG_SAPLING));
        }
        else
        {
            tree.isTree = false;
        }

        if (compound.hasKey(TAG_NETHER_TREE))
        {
            tree.netherTree = compound.getBoolean(TAG_NETHER_TREE);
        }
        else
        {
            tree.netherTree = false;
        }

        if (compound.hasKey(TAG_LEAVES))
        {
            final NBTTagList leavesBin = compound.getTagList(TAG_LEAVES, NBTBase.TAG_COMPOUND);
            for (int i = 0; i < leavesBin.size(); i++)
            {
                tree.leaves.add(BlockPosUtil.readFromListNBT(leavesBin, i));
            }
        }

        return tree;
    }

    /**
     * Checks if the found log is part of a tree.
     *
     * @param world  The world the tree is in.
     * @param topLog The most upper log of the tree.
     */
    private void checkTree(@NotNull final World world, @NotNull final int[] topLog)
    {
        if (!BlockUtils.isAnySolid(world.getBlockState(new int[]{location[0], location[1] - 1, location[2]})))
        {
            return;
        }
        int leafCount = 0;
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                for (int y = -1; y <= 1; y++)
                {
                    final int[] leaf = new int[]{topLog[0] + x, topLog[1] + y, topLog[2] + z};
                    final BlockState leaves = world.getBlockState(leaf);
                    if (leaves.is(BlockTags.LEAVES) || leaves.is(ModTags.hugeMushroomBlocks))
                    {
                        if (leaves.getOptionalValue(null /* [1.7.10] no PERSISTENT property */).orElse(false))
                        {
                            continue;
                        }
                        leafCount++;
                        if (leafCount >= NUMBER_OF_LEAVES)
                        {
                            isTree = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Searches all logs that belong to the tree.
     *
     * @param world  The world where the blocks are in.
     * @param colony the colony to search for buildings, or null if we don't care.
     */
    public void findLogs(@NotNull final World world, @Nullable final IColony colony)
    {
        addAndSearch(world, location, colony);
        woodBlocks.sort((c1, c2) -> (int) (c1.distSqr(location) - c2.distSqr(location)));
        if (getStumpLocations().isEmpty())
        {
            fillTreeStumps(location[1]);
        }
    }

    /**
     * Checks if the tree has been planted from more than 1 saplings. Meaning that more than 1 log is on the lowest World.
     *
     * @param yLevel The base y.
     */
    public void fillTreeStumps(final int yLevel)
    {
        for (@NotNull final int[] pos : woodBlocks)
        {
            if (pos[1] == yLevel)
            {
                stumpLocations.add(pos);
            }
        }

        // todo: for the sake of generic-ness this could check for adjacency rather than just special-casing,
        //       though that's harder if someone decides to make a tree bigger than 2x2.
        // [1.7.10] mangrove propagule averaging removed - not applicable to 1.7.10
    }

    /**
     * Adds a log and searches for further logs(Breadth first search).
     *
     * @param world  The world the log is in.
     * @param log    the log to add.
     * @param colony the colony to search for buildings, or null if we don't care.
     */
    private void addAndSearch(@NotNull final World world, @NotNull final int[] log, @Nullable final IColony colony)
    {
        if (woodBlocks.size() >= MineColonies.getConfig().getServer().maxTreeSize.get())
        {
            return;
        }

        if (woodBlocks.contains(log))
        {
            return;
        }

        // Check if the new log fits the Tree's base log type
        if (!isBlockPartOfSameTree(world.getBlockState(log), world.getBlockState(location)))
        {
            return;
        }

        if (log[1] < location[1])
        {
            location = log;
        }

        if (log[1] > topLog[1])
        {
            topLog = log;
        }

        if (colony != null)
        {
            for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values())
            {
                if (building.isInBuilding(log))
                {
                    return;
                }
            }
        }

        woodBlocks.add(log);

        // Only add the base to a dynamic tree
        if (Compatibility.isDynamicBlock(BlockPosUtil.getBlock(world, log)))
        {
            return;
        }


        for (int y = -1; y <= 1; y++)
        {
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    final int[] temp = log.offset(x, y, z);
                    final BlockState block = BlockPosUtil.getBlockState(world, temp);
                    if ((block.is(ModTags.tree) || Compatibility.isSlimeBlock(block.getBlock())))
                    {
                        addAndSearch(world, temp, colony);
                    }
                }
            }
        }
    }

    /**
     * Get the prefix of a log block, which is the path of the block in the
     * registries, with "_log" or "_wood" removed from the end.
     *
     * @param block the block to get the prefix from.
     * @return the prefix of the log block.
     */
    private String logPrefix(BlockState block)
    {
        String path = ForgeRegistries.BLOCKS.getKey(block.getBlock()).getPath();
        return path.replaceFirst("(_log|_wood|_stem|_hyphae)$", "");
    }

    /**
     * Check if this is a log in the same tree type.
     *
     * @param checkBlock the current block in the tree being evaluated.
     * @param stumpBlock the block to check against.
     * @return true if this is the same type of tree; false if it's something different.
     */
    private boolean isBlockPartOfSameTree(
      @NotNull final BlockState checkBlock,
      @NotNull final BlockState stumpBlock)
    {
        if (checkBlock.is(ModTags.mangroveTree))
        {
            return stumpBlock.is(ModTags.mangroveTree);
        }

        return (checkBlock.getBlock() == stumpBlock.getBlock()) || checkBlock.is(ModTags.extraTree) || (logPrefix(checkBlock).equals(logPrefix(stumpBlock)));
	}

    /**
     * Adds a leaf and searches for further leaves.
     *
     * @param world The world the leaf is in.
     */
    private void addAndSearch(@NotNull final World world)
    {
        int locXMin = location[0] - LEAVES_WIDTH;
        int locXMax = location[0] + LEAVES_WIDTH;
        final int locYMin = location[1] + 1;
        int locZMin = location[2] - LEAVES_WIDTH;
        int locZMax = location[2] + LEAVES_WIDTH;
        int temp;
        if (locXMin > locXMax)
        {
            temp = locXMax;
            locXMax = locXMin;
            locXMin = temp;
        }
        if (locZMin > locZMax)
        {
            temp = locZMax;
            locZMax = locZMin;
            locZMin = temp;
        }
        for (int locX = locXMin; locX <= locXMax; locX++)
        {
            for (int locY = locYMin; locY < world.getHeight(); locY++)
            {
                for (int locZ = locZMin; locZ <= locZMax; locZ++)
                {
                    final int[] leaf = new int[]{locX, locY, locZ};
                    final BlockState block = world.getBlockState(leaf);
                    if (block.is(BlockTags.LEAVES) || block.is(ModTags.hugeMushroomBlocks) ||
                            block.is(BlockTags.WART_BLOCKS) || block.is(Blocks.redstone_block /* [1.7.10] no shroomlight */))
                    {
                        if (!block.getOptionalValue(null /* [1.7.10] no PERSISTENT property */).orElse(false))
                        {
                            leaves.add(leaf);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the next log block.
     *
     * @return the position.
     */
    public int[] pollNextLog()
    {
        return woodBlocks.pollLast();
    }

    /**
     * Gets the trees sapling
     *
     * @return the sapling stack.
     */
    public ItemStack getSapling()
    {
        return sapling;
    }

    /**
     * Returns the next leaf block.
     *
     * @return the position.
     */
    public int[] pollNextLeaf()
    {
        return leaves.pollLast();
    }

    /**
     * Looks up the next log block.
     *
     * @return the position.
     */
    public int[] peekNextLog()
    {
        return woodBlocks.peekLast();
    }

    /**
     * Looks up the next leaf block.
     *
     * @return the position.
     */
    public int[] peekNextLeaf()
    {
        return leaves.peekLast();
    }

    /**
     * Check if the found tree has any leaves.
     *
     * @return true if there are leaves associated with the tree.
     */
    public boolean hasLeaves()
    {
        return !leaves.isEmpty();
    }

    /**
     * Check if the found tree has any logs.
     *
     * @return true if there are wood blocks associated with the tree.
     */
    public boolean hasLogs()
    {
        return !woodBlocks.isEmpty();
    }

    /**
     * @return if tree is slime tree.
     */
    public boolean isSlimeTree()
    {
        return slimeTree;
    }

    /**
     * @return if tree is dynamic tree
     */
    public boolean isDynamicTree()
    {
        return dynamicTree;
    }

    /**
     * @return if tree is nether tree
     */
    public boolean isNetherTree()
    {
        return netherTree;
    }

    /**
     * All stump positions of a tree (A tree may have been planted with different saplings).
     *
     * @return an Arraylist of the positions.
     */
    @NotNull
    public List<int[]> getStumpLocations()
    {
        return new ArrayList<>(stumpLocations);
    }

    /**
     * Removes a stump from the stump list.
     *
     * @param pos the position of the stump.
     */
    public void removeStump(final int[] pos)
    {
        stumpLocations.remove(pos);
    }

    /**
     * Get's the variant of a tree. A tree may only have 1 variant.
     *
     * @return the EnumType variant.
     */
    public Object getVariant()
    {
        return variant;
    }

    /**
     * Returns the trees location.
     *
     * @return the position.
     */
    public int[] getLocation()
    {
        return location;
    }

    /**
     * Needed for the equals method.
     *
     * @return the hash code of the location.
     */
    @Override
    public int hashCode()
    {
        return location.hashCode();
    }

    /**
     * Overridden equals method checks if the location of the both trees are equal.
     *
     * @param tree the object to compare.
     * @return true if equal or false if not.
     */
    @Override
    public boolean equals(@Nullable final Object tree)
    {
        return tree != null && tree.getClass() == this.getClass() && ((Tree) tree).getLocation().equals(location);
    }

    /**
     * Writes the tree Object to NBT.
     *
     * @param compound the compound of the tree.
     */
    public void write(@NotNull final NBTTagCompound compound)
    {
        if (!isTree)
        {
            return;
        }

        BlockPosUtil.write(compound, TAG_LOCATION, location);

        @NotNull final NBTTagList logs = new NBTTagList();
        for (@NotNull final int[] log : woodBlocks)
        {
            BlockPosUtil.writeToListNBT(logs, log);
        }
        compound.setTag(TAG_LOGS, logs);

        @NotNull final NBTTagList stumps = new NBTTagList();
        for (@NotNull final int[] stump : stumpLocations)
        {
            BlockPosUtil.writeToListNBT(stumps, stump);
        }
        compound.setTag(TAG_STUMPS, stumps);

        BlockPosUtil.write(compound, TAG_TOP_LOG, topLog);

        compound.setBoolean(TAG_IS_SLIME_TREE, slimeTree);
        compound.setBoolean(TAG_DYNAMIC_TREE, dynamicTree);

        NBTTagCompound saplingNBT = new NBTTagCompound();
        sapling.save(saplingNBT);

        compound.setTag(TAG_SAPLING, saplingNBT);
        compound.setBoolean(TAG_NETHER_TREE, netherTree);

        @NotNull final NBTTagList leavesBin = new NBTTagList();
        for (@NotNull final int[] pos : leaves)
        {
            BlockPosUtil.writeToListNBT(leavesBin, pos);
        }
        compound.setTag(TAG_LEAVES, leavesBin);
    }

    /**
     * Returns whether the Tree object actually is a tree.
     *
     * @return isTree
     */
    public boolean isTree()
    {
        return isTree;
    }

    /**
     * Calculates with a colony if the position is inside the colony and optionally if it is inside a building.
     * Accessed off-thread by pathfinding
     *
     * @param pos                 the position.
     * @param colony              the colony.
     * @param world               the world to use
     * @param allowInsideBuilding if false, also checks that the tree is not inside a building.
     * @return return false if not inside the colony or optionally if inside a building.
     */
    public static boolean checkIfInColony(final int[] pos, final IColony colony, final IBlockAccess world, final boolean allowInsideBuilding)
    {
        if (!colony.getLoadedChunks().contains(((long)pos[0] << 32) | ((long)pos[2] & 0xFFFFFFFFL)))
        {
            return false;
        }

        // Dynamic trees are never part of buildings
        if (allowInsideBuilding || Compatibility.isDynamicBlock(world.getBlockState(pos).getBlock()))
        {
            return true;
        }

        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values())
        {
            if (building.isInBuilding(pos))
            {
                return false;
            }
        }
        return true;
    }
}








