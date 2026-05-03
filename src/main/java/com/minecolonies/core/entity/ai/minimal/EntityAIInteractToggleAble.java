package com.minecolonies.core.entity.ai.minimal;

import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * AI to close toggleables when collided.
 * [1.7.10] Ported from 1.21: PathEntity/PathPoint/PathNavigate replacing 1.21 Path/Node/GroundPathNavigation.
 */
public class EntityAIInteractToggleAble extends net.minecraft.entity.ai.EntityAIBase
{
    private static final int DEFAULT_HEIGHT_TO_CHECK = 2;
    private static final int LENGTH_TO_CHECK = 2;
    private static final double MIN_DISTANCE = 4D;
    private static final double MAX_DISTANCE = 5D;

    public static final ToggleAble FENCE_TOGGLE = new FenceToggle();
    public static final ToggleAble TRAP_TOGGLE  = new TrapToggle();
    public static final ToggleAble DOOR_TOGGLE  = new DoorToggle();

    protected AbstractFastMinecoloniesEntity entity;
    private Map<int[], Boolean> toggleAblePositions = new HashMap<>();
    private final List<ToggleAble> toggleAbles;
    private final List<ToggleAble> myToggled;
    private int updateTimer = 0;
    private int executeTimerSlow = 60;
    private final int offSet;

    public EntityAIInteractToggleAble(@NotNull final AbstractFastMinecoloniesEntity entityIn, final ToggleAble... toggleAbles)
    {
        super();
        this.entity = entityIn;
        this.toggleAbles = Arrays.asList(toggleAbles);
        this.myToggled = new ArrayList<>();
        if (!(entityIn.getNavigation() instanceof PathNavigate))
        {
            throw new IllegalArgumentException("Unsupported EntityCreature type for EntityAIInteractToggleAble");
        }
        offSet = entityIn.worldObj.rand.nextInt(20);
    }

    @Override
    public boolean canUse()
    {
        // Cavalry horse rider: use mount's collision
        if (entity.ridingEntity instanceof CavalryHorseEntity)
        {
            final CavalryHorseEntity horse = (CavalryHorseEntity) entity.ridingEntity;
            if (horse.isCollidedHorizontally && entity.getNavigation() instanceof PathNavigate && updateTimer-- <= 0)
            {
                updateTimer = 10;
                return checkPath();
            }
        }

        // Reactive check for collisions
        if ((entity.isCollidedHorizontally || (entity.isCollidedVertically && !entity.onGround)) && updateTimer-- <= 0)
        {
            updateTimer = 10;
            return checkPath();
        }

        // Slow periodic check
        if (executeTimerSlow-- <= 0)
        {
            executeTimerSlow = 50;
            return checkPathBlocksBelow();
        }

        return false;
    }

    @Override
    public void start()
    {
        super.start();
        updateTimer = 0;
    }

    /** Checks if there exists a path that requires toggling. */
    private boolean checkPath()
    {
        final PathNavigate nav = (PathNavigate) this.entity.getNavigation();
        final PathEntity path = nav.getPath();
        checkPathBlocksCollided(path);
        return !toggleAblePositions.isEmpty();
    }

    /** Checks the path blocks when collided with something. */
    private void checkPathBlocksCollided(final PathEntity path)
    {
        if (path == null || path.isFinished())
        {
            resetAll();
            return;
        }

        final int maxLen = Math.min(path.getCurrentPathIndex() + LENGTH_TO_CHECK, path.getCurrentPathLength());
        for (int i = Math.max(0, path.getCurrentPathIndex() - 2); i < maxLen; i++)
        {
            if (i == path.getCurrentPathLength() - 1)
            {
                return;
            }

            final PathPoint current = path.getPathPointFromIndex(i);
            final PathPoint next    = path.getPathPointFromIndex(i + 1);

            if (next.xCoord == current.xCoord && next.yCoord == current.yCoord && next.zCoord == current.zCoord)
            {
                continue;
            }

            final Direction dir;
            if (current.xCoord == next.xCoord && current.zCoord == next.zCoord)
            {
                dir = Direction.EAST;
            }
            else
            {
                dir = BlockPosUtil.directionFromDelta(next.xCoord - current.xCoord, 0, next.zCoord - current.zCoord);
            }

            for (int h = 0; h < getHeightToCheck(path, i); h++)
            {
                checkPosAndAdd(entity, dir, new int[]{current.xCoord, current.yCoord + h, current.zCoord});
                checkPosAndAdd(entity, dir, new int[]{next.xCoord, next.yCoord + h, next.zCoord});
            }
        }
    }

    /** Checks if the pos has a toggleable block and adds it to toggle positions. */
    private void checkPosAndAdd(final Entity entity, final Direction dir, final int[] pos)
    {
        for (final int[] tracked : toggleAblePositions.keySet())
        {
            if (Arrays.equals(tracked, pos)) return;
        }

        final BlockState state = BlockState.of(entity.worldObj, pos[0], pos[1], pos[2]);
        if (this.entity.getDistanceSq(pos[0] + 0.5, this.entity.posY, pos[2] + 0.5) <= MIN_DISTANCE && isValidBlockState(state))
        {
            // [1.7.10] VoxelShape not available — skip shape check, just register
            toggleAblePositions.put(pos, (state.meta & 4) != 0);
        }
    }

    /** Checks the path for toggleables below, where we need to go through. */
    private boolean checkPathBlocksBelow()
    {
        final PathNavigate nav = (PathNavigate) this.entity.getNavigation();
        final PathEntity path = nav.getPath();

        if (path == null || path.isFinished())
        {
            resetAll();
            return false;
        }

        final int[] entityPos      = BlockPosUtil.fromEntity(entity);
        final int[] entityPosBelow = new int[]{entityPos[0], entityPos[1] - 1, entityPos[2]};
        final int maxLen = Math.min(path.getCurrentPathIndex() + LENGTH_TO_CHECK, path.getCurrentPathLength());
        for (int i = Math.max(0, path.getCurrentPathIndex() - 2); i < maxLen; ++i)
        {
            final PathPoint pathpoint = path.getPathPointFromIndex(i);

            for (int h = 0; h < getHeightToCheck(path, i); h++)
            {
                final int[] pos = new int[]{pathpoint.xCoord, pathpoint.yCoord + h, pathpoint.zCoord};

                if (!Arrays.equals(entityPos, pos) && !Arrays.equals(entityPosBelow, pos))
                {
                    continue;
                }

                final BlockState state = BlockState.of(entity.worldObj, pos[0], pos[1], pos[2]);
                if (this.entity.getDistanceSq(pos[0] + 0.5, entity.posY, pos[2] + 0.5) <= MIN_DISTANCE && isValidBlockState(state))
                {
                    if (h > 0)
                    {
                        toggleAblePositions.put(pos, (state.meta & 4) != 0);
                    }
                    else if (i < path.getCurrentPathLength() - 1)
                    {
                        final PathPoint nextPoint = path.getPathPointFromIndex(i + 1);
                        if ((pos[0] == nextPoint.xCoord && pos[1] > nextPoint.yCoord && pos[2] == nextPoint.zCoord) ||
                              entity.posY - pos[1] > 1)
                        {
                            toggleAblePositions.put(pos, (state.meta & 4) != 0);
                        }
                    }
                }
            }
        }

        return !toggleAblePositions.isEmpty();
    }

    /** Gets the required height to check for the given path index. */
    private int getHeightToCheck(final PathEntity path, final int index)
    {
        if (path == null || index < 0 || index >= path.getCurrentPathLength())
        {
            return DEFAULT_HEIGHT_TO_CHECK;
        }

        final PathPoint current = path.getPathPointFromIndex(index);
        int prevDist = 0;
        if (index > 0)
        {
            prevDist = path.getPathPointFromIndex(index - 1).yCoord - current.yCoord;
        }
        int nextDist = 0;
        if (index + 1 < path.getCurrentPathLength())
        {
            nextDist = path.getPathPointFromIndex(index + 1).yCoord - current.yCoord;
        }
        return Math.max(DEFAULT_HEIGHT_TO_CHECK, DEFAULT_HEIGHT_TO_CHECK + Math.max(prevDist, nextDist));
    }

    @Override
    public boolean canContinueToUse()
    {
        return !toggleAblePositions.isEmpty();
    }

    private void resetAll()
    {
        for (final int[] pos : toggleAblePositions.keySet())
        {
            for (final ToggleAble toggleAble : toggleAbles)
            {
                final BlockState state = BlockState.of(entity.worldObj, pos[0], pos[1], pos[2]);
                if (toggleAble.isBlockToggleAble(state) && (!toggleAble.onlyCloseYourOpens() || myToggled.contains(toggleAble)))
                {
                    toggleAble.toggleBlockClosed(entity, state, entity.worldObj, pos);
                    myToggled.remove(toggleAble);
                    break;
                }
            }
        }
        toggleAblePositions.clear();
        myToggled.clear();
        updateTimer = 0;
    }

    private boolean isValidBlockState(final BlockState state)
    {
        if (state.getBlock() == Blocks.air)
        {
            return false;
        }
        for (final ToggleAble toggleAble : toggleAbles)
        {
            if (toggleAble.isBlockToggleAble(state))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick()
    {
        if (--updateTimer >= 0)
        {
            return;
        }
        updateTimer = ColonyConstants.rand.nextInt(40 + offSet);

        if (!checkPath())
        {
            return;
        }

        final Iterator<int[]> it = toggleAblePositions.keySet().iterator();
        final List<int[]> posList = new ArrayList<>();

        while (it.hasNext())
        {
            final int[] pos = it.next();
            final BlockState state = BlockState.of(entity.worldObj, pos[0], pos[1], pos[2]);

            if (!isValidBlockState(state))
            {
                it.remove();
                continue;
            }

            if (this.entity.getDistanceSq(pos[0] + 0.5, pos[1] + 0.5, pos[2] + 0.5) > MAX_DISTANCE)
            {
                it.remove();
                for (final ToggleAble toggleAble : toggleAbles)
                {
                    if (toggleAble.isBlockToggleAble(state) && (!toggleAble.onlyCloseYourOpens() || myToggled.contains(toggleAble)))
                    {
                        toggleAble.toggleBlockClosed(entity, state, entity.worldObj, pos);
                        myToggled.remove(toggleAble);
                        break;
                    }
                }
                continue;
            }

            posList.add(pos);
        }

        if (!posList.isEmpty())
        {
            final int[] chosen = posList.get(entity.worldObj.rand.nextInt(posList.size()));
            final BlockState state = BlockState.of(entity.worldObj, chosen[0], chosen[1], chosen[2]);
            for (final ToggleAble toggleAble : toggleAbles)
            {
                if (toggleAble.isBlockToggleAble(state) && toggleAble.canOpen(state))
                {
                    toggleAble.toggleBlock(entity, state, entity.worldObj, chosen);
                    myToggled.add(toggleAble);
                    break;
                }
            }
        }
    }

    // ─────────────────────────── ToggleAble hierarchy ─────────────────────────────

    public static abstract class ToggleAble
    {
        public abstract boolean isBlockToggleAble(final BlockState state);

        public boolean canOpen(final BlockState state)
        {
            return isBlockToggleAble(state);
        }

        public boolean onlyCloseYourOpens()
        {
            return false;
        }

        public abstract void toggleBlock(final Entity entity, final BlockState state, final World world, final int[] pos);

        public abstract void toggleBlockClosed(final Entity entity, final BlockState state, final World world, final int[] pos);
    }

    private static class FenceToggle extends ToggleAble
    {
        @Override
        public boolean isBlockToggleAble(final BlockState state)
        {
            return state.getBlock() instanceof BlockFenceGate;
        }

        @Override
        public void toggleBlock(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            world.setBlock(pos[0], pos[1], pos[2], state.block, state.meta ^ 4, 2);
        }

        @Override
        public void toggleBlockClosed(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            world.setBlock(pos[0], pos[1], pos[2], state.block, state.meta & ~4, 2);
        }
    }

    private static class TrapToggle extends ToggleAble
    {
        @Override
        public boolean isBlockToggleAble(final BlockState state)
        {
            return state.getBlock() instanceof BlockTrapDoor;
        }

        @Override
        public boolean canOpen(final BlockState state)
        {
            return (state.meta & 4) == 0; // currently closed
        }

        @Override
        public boolean onlyCloseYourOpens()
        {
            return true;
        }

        @Override
        public void toggleBlock(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            world.setBlock(pos[0], pos[1], pos[2], state.block, state.meta ^ 4, 2);
        }

        @Override
        public void toggleBlockClosed(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            world.setBlock(pos[0], pos[1], pos[2], state.block, state.meta & ~4, 2);
        }
    }

    private static class DoorToggle extends ToggleAble
    {
        @Override
        public boolean isBlockToggleAble(final BlockState state)
        {
            return state.getBlock() instanceof BlockDoor;
        }

        @Override
        public void toggleBlock(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            // [1.7.10] func_150014_a = setOpen; handles both door halves
            final boolean isOpening = (state.meta & 4) == 0;
            ((BlockDoor) state.block).func_150014_a(world, pos[0], pos[1], pos[2], isOpening);
        }

        @Override
        public void toggleBlockClosed(final Entity entity, final BlockState state, final World world, final int[] pos)
        {
            ((BlockDoor) state.block).func_150014_a(world, pos[0], pos[1], pos[2], false);
        }
    }
}
