package com.minecolonies.core.entity.pathfinding.navigation;
import net.minecraft.block.state.BlockState;


import net.minecraft.util.EnumFacing;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.other.MinecoloniesMinecart;
import com.minecolonies.api.entity.pathfinding.IDynamicHeuristicNavigator;
import com.minecolonies.api.entity.pathfinding.IMinecoloniesNavigator;
import com.minecolonies.api.entity.pathfinding.IStuckHandler;
import com.minecolonies.api.util.*;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;
import com.minecolonies.core.entity.pathfinding.PathFindingStatus;
import com.minecolonies.core.entity.pathfinding.PathPointExtended;
import com.minecolonies.core.entity.pathfinding.Pathfinding;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.pathjobs.*;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
import com.minecolonies.core.entity.pathfinding.pathresults.TreePathResult;
import com.minecolonies.core.util.WorkerUtil;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.world.IBlockAccess;
// [1.7.10] ChunkPos removed
import net.minecraft.world.World;
import net.minecraft.block.*;
// [1.7.10] BlockState -> int metadata
// [1.7.10] RailShape removed
import net.minecraft.pathfinding.PathPoint; // [1.7.10] Node -> PathPoint
import net.minecraft.pathfinding.PathEntity; // [1.7.10] Path -> PathEntity
import net.minecraft.pathfinding.PathFinder;
// [1.7.10] WalkNodeEvaluator removed
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import com.minecolonies.api.entity.pathfinding.IPathJob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.core.entity.pathfinding.PathFindingStatus.IN_PROGRESS_FOLLOWING;
import static com.minecolonies.core.entity.pathfinding.pathjobs.AbstractPathJob.MAX_NODES;

/**
 * Minecolonies async PathNavigate.
 */
// TODO: Rework
public class MinecoloniesAdvancedPathNavigate extends AbstractAdvancedPathNavigate implements IDynamicHeuristicNavigator, IMinecoloniesNavigator
{
    private static final double ON_PATH_SPEED_MULTIPLIER = 1.3D;
    public static final  double MIN_Y_DISTANCE           = 0.001;
    public static final  int    MAX_SPEED_ALLOWED        = 2;
    public static final  double MIN_SPEED_ALLOWED        = 0.1;

    @Nullable
    private PathResult<? extends AbstractPathJob> pathResult;

    /**
     * Spawn pos of minecart.
     */
    private int[] spawnedPos = new int[]{0,0,0};

    /**
     * Desired position to reach
     */
    private int[] safeDestinationPos;

    /**
     * The stuck handler to use
     */
    private IStuckHandler<MinecoloniesAdvancedPathNavigate> stuckHandler;

    /**
     * Whether we did set sneaking
     */
    private boolean isSneaking = true;

    /**
     * Speed factor for swimming
     */
    private double swimSpeedFactor = 1.0;

    /**
     * Average heuristic
     */
    private double heuristicAvg = 1;

    /**
     * Paused ticks, during those no new pathjob is allowed
     */
    private int pauseTicks = 0;

    /**
     * Increasing amount for pause times, each time a path fails
     */
    private int pauseTickBackupAmount = 10;

    /**
     * Temporary block position
     */
    private int[] tempPos = new int[]{0, 0, 0};

    /**
     * wanted position for movecontrol
     */
    private Vec3Mutable wantedPosition = Vec3Mutable.createEmpty();

    /**
     * The recheck delay for checking stuck
     */
    private int checkStuckDelay = 10;

    /**
     * Time at which a path finished
     */
    private long finishTime = Long.MAX_VALUE;

    /**
     * The last path index used for wanted position calculations
     */
    private int lastWantedPathIndex = -1;

    // [1.7.10] fields from 1.21 PathNavigation that don't exist in PathNavigate
    private int tick = 0;
    private boolean hasDelayedRecomputation = false;
    private double speedModifier = 1.0;

    /**
     * Instantiates the navigation of an ourEntity.
     *
     * @param entity the ourEntity.
     * @param world  the world it is in.
     */
    public MinecoloniesAdvancedPathNavigate(@NotNull final EntityCreature entity, final World world)
    {
        super(entity, world);

        // [1.7.10] moveHelper is private in EntityLiving; custom MovementHandler assignment skipped
        // entity.moveHelper = new MovementHandler(entity);
        getPathingOptions().setEnterDoors(true);
        getPathingOptions().setCanOpenDoors(true);
        getPathingOptions().setCanSwim(true);

        stuckHandler = PathingStuckHandler.createStuckHandler().withTakeDamageOnStuck(0.2f).withTeleportSteps(6).withTeleportOnFullStuck();
    }

    @Override
    public void setSwimSpeedFactor(final double factor)
    {
        this.swimSpeedFactor = factor;
    }

    @Nullable
    protected PathResult<PathJobMoveAwayFromLocation> walkAwayFrom(final int[] avoid, final double range, final double speedFactor, final boolean safeDestination)
    {
        @NotNull final int[] start = PathfindingUtils.prepareStart(ourEntity);

        return setPathJob(new PathJobMoveAwayFromLocation(CompatibilityUtils.getWorldFromEntity(ourEntity),
          start,
          avoid,
          (int) range,
          (int) ourEntity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).getAttributeValue(),
          ourEntity), null, speedFactor, safeDestination);
    }

    @Nullable
    @Override
    protected PathResult<AbstractPathJob> walkTowards(final int[] towards, final double range, final double speedFactor)
    {
        return setPathJob(new PathJobMoveTowards(CompatibilityUtils.getWorldFromEntity(ourEntity),
          PathfindingUtils.prepareStart(ourEntity),
          towards,
          (int) range,
          ourEntity), null, speedFactor, false);
    }

    @Nullable
    protected PathResult<PathJobRandomPos> walkToRandomPos(final int range, final double speedFactor)
    {
        @NotNull final int[] start = PathfindingUtils.prepareStart(ourEntity);
        final PathResult<PathJobRandomPos> result = setPathJob(new PathJobRandomPos(CompatibilityUtils.getWorldFromEntity(ourEntity),
          start,
            range,
          (int) ourEntity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).getAttributeValue(),

          ourEntity), null, speedFactor, true);

        if (result == null)
        {
            return null;
        }

        result.getJob().getPathingOptions().withToggleCost(1).withJumpCost(1).withDropCost(1).canDrop = false;
        return result;
    }

    @Nullable
    protected PathResult<PathJobRandomPos> walkToRandomPosAround(final int range, final double speedFactor, final int[] pos)
    {
        final PathResult<PathJobRandomPos> result = setPathJob(new PathJobRandomPos(CompatibilityUtils.getWorldFromEntity(ourEntity),
          PathfindingUtils.prepareStart(ourEntity),
          3,
          (int) ourEntity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).getAttributeValue(),
          range,
          ourEntity, pos), pos, speedFactor, false);

        if (result == null)
        {
            return null;
        }

        result.getJob().getPathingOptions().withToggleCost(1).withJumpCost(1).withDropCost(1).canDrop = false;
        return result;
    }

    @Override
    protected PathResult<PathJobRandomPos> walkToRandomPos(
      final int range,
      final double speedFactor,
      final int[][] corners)
    {
        return walkToRandomPos(range, speedFactor, corners, false);
    }

    @Override
    protected PathResult<PathJobRandomPos> walkToRandomPos(
        final int range,
        final double speedFactor,
        final int[][] corners, final boolean preferInside)
    {
        @NotNull final int[] start = PathfindingUtils.prepareStart(ourEntity);

        final PathResult<PathJobRandomPos> result = setPathJob(new PathJobRandomPos(CompatibilityUtils.getWorldFromEntity(ourEntity),
            start,
            range,
            (int) ourEntity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).getAttributeValue(),
            ourEntity,
            corners[0],
            corners[1], preferInside), null, speedFactor, true);

        if (result == null)
        {
            return null;
        }

        result.getJob().getPathingOptions().withJumpCost(1).withDropCost(1).canDrop = false;
        return result;
    }

    @Override
    protected PathResult<PathJobMoveCloseToXNearY> walkCloseToXNearY(
        final int[] desiredPosition,
        final int[] nearbyPosition,
        final int distToDesired,
        final double speedFactor,
        final boolean safeDestination)
    {
        PathJobMoveCloseToXNearY pathJob = new PathJobMoveCloseToXNearY(ourEntity.worldObj, desiredPosition, nearbyPosition, 1, ourEntity);
        return setPathJob(pathJob, desiredPosition, speedFactor, safeDestination);
    }

    @Nullable
    @Override
    public <T extends AbstractPathJob> PathResult<T> setPathJob(
      @NotNull final AbstractPathJob job,
      final int[] dest,
      final double speedFactor, final boolean safeDestination)
    {
        if (pauseTicks > 0)
        {
            return null;
        }

        // [1.7.10] Pose system does not exist
        // if (ourEntity.getPose() != Pose.STANDING) { ourEntity.setPose(Pose.STANDING); }

        if (pathResult != null)
        {
            pathResult.cancel();
            pathResult.setStatus(PathFindingStatus.CANCELLED);
            pathResult = null;
        }
        super.clearPathEntity(); // [1.7.10] stop() -> clearPathEntity()

        if (dest != null)
        {
            final int[] jobStart = job.getStart();
            final long dx = jobStart[0] - dest[0], dy = jobStart[1] - dest[1], dz = jobStart[2] - dest[2];
            if (dx*dx + dy*dy + dz*dz > 900L * 900L)
            {
                Log.getLogger()
                    .error(
                        "Entity: " + ourEntity.getCommandSenderName() + " is trying to walk too far! from:"
                            + Arrays.toString(jobStart) + " to:"
                            + Arrays.toString(dest), new Exception());

                if (!Arrays.equals(dest, new int[]{0,0,0}))
                {
                    if (ourEntity instanceof AbstractEntityCitizen citizen)
                    {
                        final int[] tpPos = citizen.getCitizenData().getHomePosition();
                        ourEntity.setPositionAndUpdate(tpPos[0], tpPos[1], tpPos[2]);
                        return null;
                    }

                    ourEntity.setPositionAndUpdate(dest[0], dest[1], dest[2]);
                }

                pauseTicks = 20 * 300;
                return null;
            }
        }

        finishTime = Long.MAX_VALUE;
        this.originalDestination = dest;
        if (safeDestination)
        {
            safeDestinationPos = dest;
        }

        this.walkSpeedFactor = speedFactor;

        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED)
        {
            Log.getLogger().error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return null;
        }

        job.setPathingOptions(getPathingOptions());
        pathResult = job.getResult();
        pathResult.startJob(Pathfinding.getExecutor());
        return (PathResult<T>) pathResult;
    }

    // [1.7.10] isDone() does not exist in PathNavigate; using noPath() equivalent
    public boolean isDone()
    {
        return (pathResult == null || pathResult.isDone() && pathResult.getStatus() != PathFindingStatus.CALCULATION_COMPLETE) && super.noPath();
    }

    @Override
    public void onUpdateNavigation() // [1.7.10] tick() -> onUpdateNavigation()
    {
        if (checkStuckDelay-- < 0)
        {
            checkStuckDelay = 10;
            stuckHandler.checkStuck(this);
        }

        if (pauseTicks > 0)
        {
            pauseTicks--;
        }

        if (pathResult != null)
        {
            if (!pathResult.isDone())
            {
                return;
            }
            else if (pathResult.getStatus() == PathFindingStatus.CALCULATION_COMPLETE)
            {
                processCompletedCalculationResult();
                wantedPosition.setEmpty();
            }
        }

        int oldIndex = this.isDone() ? 0 : (this.getPath() != null ? this.getPath().getCurrentPathIndex() : 0);

        // [1.7.10] setYya not available
        if (handleLadders(oldIndex))
        {
            followThePath();
            return;
        }

        if (isSneaking)
        {
            isSneaking = false;
            ourEntity.setSneaking(false);
        }

        if (handleRails())
        {
            return;
        }

        ++this.tick;
        if (this.hasDelayedRecomputation)
        {
            this.recomputePath();
        }

        // [1.7.10] Simplified: use super navigation path-following instead of 1.21 custom wantedPosition logic
        if (!this.isDone())
        {
            this.followThePath();
        }

        if (pathResult != null && isDone())
        {
            pathResult.setStatus(PathFindingStatus.COMPLETE);

            // Cleanup pathresult if the entity forgot about it
            if (ourEntity.worldObj.getTotalWorldTime() - finishTime > TICKS_SECOND * 20 + pauseTickBackupAmount)
            {
                pathResult = null;
            }
        }
    }

    /**
     * Similar to WalkNodeProcessor.getGroundY but not broken.
     * This checks if the block below the position we're trying to move to reaches into the block above, if so, it has to aim a little bit higher.
     *
     * @param world the world.
     * @param pos   the position to check.
     * @param orgY  original y World
     * @return the next y World to go to.
     */
    public static double getSmartGroundY(final IBlockAccess world, final int[] pos, final double orgY)
    {
        // [1.7.10] AxisAlignedBB/FenceGateBlock/DoorBlock/ShapeUtil not available; simplified stub
        final net.minecraft.block.state.BlockState state = net.minecraft.block.state.BlockState.of(world, pos[0], pos[1], pos[2]);

        if (!state.isAir())
        {
            return orgY;
        }

        final net.minecraft.block.state.BlockState stateBelow = net.minecraft.block.state.BlockState.of(world, pos[0], pos[1] - 1, pos[2]);
        if (!stateBelow.isAir())
        {
            return pos[1];
        }

        return orgY;
    }

    @Nullable
    protected PathResult<PathJobMoveToLocation> walkTo(final int[] desiredPos, final double speedFactor, final boolean safeDestination)
    {
        @NotNull final int[] start = PathfindingUtils.prepareStart(ourEntity);
        return setPathJob(
          new PathJobMoveToLocation(CompatibilityUtils.getWorldFromEntity(ourEntity),
            start,
            desiredPos,
            (int) ourEntity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).getAttributeValue(),
            ourEntity),
            desiredPos, speedFactor, safeDestination);
    }

    @Deprecated(since = "Do not use, always returns true, vanilla override")
    @Override
    public boolean walkTo(final int[] pos, final double speedFactor)
    {
        walkTo(pos, speedFactor, false);
        return true;
    }

    // [1.7.10] createPathFinder not in PathNavigate
    protected PathFinder createPathFinder(final int p_179679_1_)
    {
        return null;
    }

    // [1.7.10] canUpdatePath → canNavigate() in PathNavigate
    protected boolean canUpdatePath()
    {
        return true;
    }

    @NotNull
    // [1.7.10] getTempMobPos → getEntityPosition() in PathNavigate
    protected net.minecraft.util.Vec3 getTempMobPos()
    {
        // [1.7.10] return entity position as Vec3
        return net.minecraft.util.Vec3.createVectorHelper(this.ourEntity.posX, this.ourEntity.posY, this.ourEntity.posZ);
    }

    // [1.7.10] createPath not an override in PathNavigate
    public PathEntity createPath(final int[] pos, final int p_179680_2_)
    {
        //Because this directly returns Path we can't do it async.
        return null;
    }

    // [1.7.10] canMoveDirectly → isDirectPathBetweenPoints but signature differs; not an override
    protected boolean canMoveDirectly(final net.minecraft.util.Vec3 start, final net.minecraft.util.Vec3 end)
    {
        // TODO improve road walking.
        final net.minecraft.block.state.BlockState bs = net.minecraft.block.state.BlockState.of(ourEntity.worldObj,
            (int)start.xCoord, (int)(start.yCoord - 1), (int)start.zCoord);
        return !WorkerUtil.isPathBlock(bs.getBlock());
        // [1.7.10] isDirectPathBetweenPoints not available; always allow direct path if on road
    }

    public double getSpeedFactor()
    {
        if (ourEntity.isInWater())
        {
            speedModifier = walkSpeedFactor * swimSpeedFactor;
            return speedModifier;
        }

        speedModifier = walkSpeedFactor;
        return walkSpeedFactor;
    }

    // [1.7.10] setSpeedModifier not in PathNavigate
    public void setSpeedModifier(final double speedFactor)
    {
        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED)
        {
            Log.getLogger().error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return;
        }
        walkSpeedFactor = speedFactor;
    }

    // [1.7.10] moveTo(double,double,double,double) → setPath(getPathToXYZ(...), speed)
    public boolean moveTo(final double x, final double y, final double z, final double speedFactor)
    {
        walkTo(new int[]{(int)x, (int)y, (int)z}, speedFactor, false);
        return true;
    }

    // [1.7.10] moveTo(Entity, double) → setPath(getPathToEntityLiving(...), speed)
    public boolean moveTo(final Entity entityIn, final double speedFactor)
    {
        return walkTo(new int[]{(int)entityIn.posX, (int)entityIn.posY, (int)entityIn.posZ}, speedFactor);
    }

    // [1.7.10] trimPath does not exist in PathNavigate
    protected void trimPath() {}

    // [1.7.10] moveTo(PathEntity, double) → setPath(path, speed)
    public boolean moveTo(@Nullable final PathEntity path, final double speedFactor)
    {
        if (path == null)
        {
            super.clearPathEntity();
            return false;
        }
        return super.setPath(convertPath(path), speedFactor);
    }

    /**
     * Converts the given path to a minecolonies path if needed.
     *
     * @param path given path
     * @return resulting path
     */
    private PathEntity convertPath(final PathEntity path)
    {
        final int pathLength = path.getCurrentPathLength();
        PathEntity tempPath = null;
        if (pathLength > 0 && !(path.getPathPointFromIndex(0) instanceof PathPointExtended))
        {
            //  Fix vanilla PathPoints to be PathPointExtended
            @NotNull final PathPointExtended[] newPoints = new PathPointExtended[pathLength];

            for (int i = 0; i < pathLength; ++i)
            {
                final net.minecraft.pathfinding.PathPoint point = path.getPathPointFromIndex(i);
                if (!(point instanceof PathPointExtended))
                {
                    newPoints[i] = new PathPointExtended(new int[]{point.xCoord, point.yCoord, point.zCoord});
                }
                else
                {
                    newPoints[i] = (PathPointExtended) point;
                }
            }

            tempPath = new PathEntity(newPoints);
        }

        return tempPath == null ? path : tempPath;
    }

    /**
     * Processes the pathresult when it finished computing
     */
    private void processCompletedCalculationResult()
    {
        if (pathResult == null)
        {
            return;
        }

        if (pathResult != null)
        {
            pathResult.setStatus(IN_PROGRESS_FOLLOWING);
        }

        // Calculate an overtime-heuristic adjustment for pathfinding to use which fits the terrain
        if (pathResult.hasPath() && pathResult.getPathLength() > 2 && pathResult.costPerDist != 1)
        {
            final double factor = 1 + pathResult.getPathLength() / 30.0;
            heuristicAvg -= heuristicAvg / (50 / factor);
            heuristicAvg += pathResult.costPerDist / (50 / factor);
        }

        if (pathResult.failedToReachDestination())
        {
            pauseTicks = pauseTickBackupAmount;
            pauseTickBackupAmount += 10;

            if (pathResult.searchedNodes >= MAX_NODES)
            {
                pauseTicks += 50;
            }
        }
        else
        {
            pauseTickBackupAmount = 10;
        }

        moveTo(pathResult.getPath(), getSpeedFactor());
    }

    private boolean handleLadders(int oldIndex)
    {
        //  Ladder Workaround
        if (!this.isDone())
        {
            @NotNull final PathPointExtended pEx = (PathPointExtended) this.getPath().getPathPointFromIndex(this.getPath().getCurrentPathIndex());
            final PathPointExtended pExNext = this.getPath().getCurrentPathLength() > this.getPath().getCurrentPathIndex() + 1
                                                ? (PathPointExtended) this.getPath()
              .getPathPointFromIndex(this.getPath()
                .getCurrentPathIndex() + 1) : null;

            final int[] tempPos2 = new int[]{pEx.xCoord, pEx.yCoord, pEx.zCoord};
            final net.minecraft.block.state.BlockState ladderState = net.minecraft.block.state.BlockState.of(ourEntity.worldObj, pEx.xCoord, pEx.yCoord, pEx.zCoord);
            if (pEx.isOnLadder() && pExNext != null && (pEx.yCoord != pExNext.yCoord || ourEntity.posY > pEx.yCoord) && PathfindingUtils.isLadder(ladderState,
              pathResult != null ? pathResult.getJob().getPathingOptions() : getPathingOptions())
                // [1.7.10] getFluidState not available; skip fluid check
              )
            {
                return handlePathPointOnLadder(pEx);
            }
            else if (ourEntity.isInWater())
            {
                return handleEntityInWater(oldIndex, pEx);
            }
            else if (ourEntity.worldObj.rand.nextInt(20) == 0)
            {
                final net.minecraft.block.state.BlockState underState = net.minecraft.block.state.BlockState.of(ourEntity.worldObj,
                    (int)ourEntity.posX, (int)ourEntity.posY - 1, (int)ourEntity.posZ);
                if (!pEx.isOnLadder() && pExNext != null && pExNext.isOnLadder())
                {
                    speedModifier = getSpeedFactor() / 4.0;
                }
                else if (WorkerUtil.isPathBlock(underState.getBlock()))
                {
                    speedModifier = ON_PATH_SPEED_MULTIPLIER * getSpeedFactor();
                }
                else
                {
                    speedModifier = getSpeedFactor();
                }
            }
        }
        return false;
    }

    private int[] findBlockUnderEntity(@NotNull final Entity parEntity)
    {
        // [1.7.10] simplified
        return new int[]{(int)Math.round(parEntity.posX), (int)(parEntity.posY - 0.2), (int)Math.round(parEntity.posZ)};
    }

    /**
     * Handle rails navigation.
     *
     * @return true if block.
     */
    private boolean handleRails()
    {
        // [1.7.10] TODO: Rail navigation requires 1.21-specific APIs; stubbed out
        return false;
    }

    /**
     * Handle pathing on rails.
     */
    private boolean handlePathOnRails(final PathPointExtended pEx, final PathPointExtended pExNext)
    {
        // [1.7.10] TODO: stubbed out
        return false;
    }

    private boolean handlePathPointOnLadder(final PathPointExtended pEx)
    {
        // [1.7.10] TODO: ladder path handling stubbed out
        return false;
    }

    private boolean handleEntityInWater(int oldIndex, final PathPointExtended pEx)
    {
        // [1.7.10] TODO: water handling stubbed out
        return false;
    }

    // [1.7.10] followThePath - not an override in PathNavigate
    protected void followThePath()
    {
        // [1.7.10] PathNavigate base class does not have followThePath(); stub only
    }

    /**
     * Called upon reaching the path end, reset values
     */
    private void onPathFinish()
    {
        finishTime = ourEntity.worldObj.getTotalWorldTime();
        super.clearPathEntity(); // [1.7.10] stop() -> clearPathEntity()
    }

    public void recomputePath() {}

    /**
     * Don't let vanilla rapidly discard paths, set a timeout before its allowed to use stuck.
     */
    // [1.7.10] doStuckDetection not in PathNavigate
    protected void doStuckDetection(@NotNull final net.minecraft.util.Vec3 positionVec3)
    {
        // Do nothing, unstuck is checked on tick, not just when we have a path
    }

    /**
     * Stop indicates that the entity no longer desires to move.
     */
    // [1.7.10] stop → clearPathEntity
    public void stop()
    {
        if (pathResult != null)
        {
            pathResult.cancel();
            pathResult.setStatus(PathFindingStatus.CANCELLED);
            pathResult = null;
            if (ourEntity.ridingEntity != null && !(ourEntity.ridingEntity instanceof CavalryHorseEntity))
            {
                final Entity entity = ourEntity.ridingEntity;
                ourEntity.mountEntity(null);
                entity.setDead();
            }
        }

        safeDestinationPos = new int[]{0,0,0};
        stuckHandler.resetGlobalStuckTimers();

        super.clearPathEntity();
    }

    @Override
    public TreePathResult walkToTree(
      final int[] startRestriction,
      final int[] endRestriction,
      final double speed,
      final List<ItemStorage> excludedTrees,
      final int dyntreesize,
      final IColony colony)
    {
        @NotNull final int[] start = PathfindingUtils.prepareStart(ourEntity);
        final int[] furthestRestriction = BlockPosUtil.getFurthestCorner(start, startRestriction, endRestriction);

        final PathJobFindTree job =
          new PathJobFindTree(CompatibilityUtils.getWorldFromEntity(ourEntity),
            start,
            startRestriction,
            endRestriction,
            furthestRestriction,
            excludedTrees,
            dyntreesize,
            colony,
            ourEntity);

        return (TreePathResult) setPathJob(job, null, speed, true);
    }

    @Override
    public TreePathResult walkToTree(final int range, final double speed, final List<ItemStorage> excludedTrees, final int dyntreesize, final IColony colony)
    {
        @NotNull int[] start = PathfindingUtils.prepareStart(ourEntity);
        final int[] buildingPos = ((AbstractEntityCitizen) ourEntity).getCitizenColonyHandler().getWorkBuilding().getPosition();

        if (BlockPosUtil.getDistance2D(buildingPos, new int[]{(int)ourEntity.posX, 0, (int)ourEntity.posZ}) > range * 4)
        {
            start = buildingPos;
        }

        return (TreePathResult) setPathJob(
          new PathJobFindTree(CompatibilityUtils.getWorldFromEntity(ourEntity), start, buildingPos, range, excludedTrees, dyntreesize, colony, ourEntity), null, speed, true);
    }

    @Nullable
    @Override
    public PathResult<? extends IPathJob> walkToEntity(@NotNull final Entity e, final double speed)
    {
        return walkTo(new int[]{(int)e.posX, (int)e.posY, (int)e.posZ}, speed, false);
    }

    @Nullable
    @Override
    public PathResult<? extends IPathJob> moveAwayFromLivingEntity(@NotNull final Entity e, final double distance, final double speed)
    {
        return walkAwayFrom(new int[]{(int)e.posX, (int)e.posY, (int)e.posZ}, distance, speed, true);
    }

    // [1.7.10] setCanFloat not in PathNavigate
    public void setCanFloat(boolean canSwim)
    {
        getPathingOptions().setCanSwim(canSwim);
    }

    // [1.7.10] getSafeDestination not in IMinecoloniesNavigator parent but in IMinecoloniesNavigator interface
    @Override
    public int[] getSafeDestination()
    {
        return safeDestinationPos;
    }

    @Override
    public void recalc()
    {
        // [1.7.10] Triggers indirect recalculation by clearing current path
        super.clearPathEntity();
    }

    @Override
    public void setSafeDestinationPos(final int[] pos)
    {
        safeDestinationPos = pos;
    }

    /**
     * Sets the stuck handler
     *
     * @param stuckHandler handler to set
     */
    @Override
    public void setStuckHandler(final IStuckHandler stuckHandler)
    {
        this.stuckHandler = stuckHandler;
    }


    @Override
    public double getAvgHeuristicModifier()
    {
        return heuristicAvg;
    }

    @Override
    public void setPauseTicks(final int pauseTicks)
    {
        if (pauseTicks > TICKS_SECOND * 120)
        {
            Log.getLogger().warn("Tried to pause entity pathfinding for " + ourEntity + " too long for " + pauseTicks + " ticks.", new Exception());
            this.pauseTicks = 50;
        }
        else
        {
            this.pauseTicks = pauseTicks;
        }
    }

    @Override
    public PathResult getPathResult()
    {
        return pathResult;
    }

    @Override
    public IStuckHandler<MinecoloniesAdvancedPathNavigate> getStuckHandler()
    {
        return stuckHandler;
    }

    // [1.7.10] isStuck not in IMinecoloniesNavigator base
    public boolean isStuck()
    {
        return stuckHandler.getStuckLevel() >= 3;
    }
}















