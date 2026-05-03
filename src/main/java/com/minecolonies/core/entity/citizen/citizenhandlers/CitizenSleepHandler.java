package com.minecolonies.core.entity.citizen.citizenhandlers;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.interactionhandling.ChatPriority;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSleepHandler;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.WorldUtil;
import com.minecolonies.core.colony.interactionhandling.SimpleNotificationInteraction;
import com.minecolonies.core.colony.interactionhandling.StandardInteraction;
import com.minecolonies.core.colony.jobs.JobMiner;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
import net.minecraft.util.IChatComponent;
// [1.7.10] tags removed
// [1.7.10] world.entity removed
import net.minecraft.world.level.block.BedBlock;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.properties.BedPart;
// [1.7.10] world.phys removed

import static com.minecolonies.api.entity.citizen.AbstractEntityCitizen.DATA_BED_POS;
import static com.minecolonies.api.entity.citizen.AbstractEntityCitizen.DATA_IS_ASLEEP;
import static com.minecolonies.api.research.util.ResearchConstants.WORK_LONGER;
import static com.minecolonies.api.util.constant.CitizenConstants.NIGHT;
import static com.minecolonies.api.util.constant.Constants.HALF_BLOCK;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_ENTITY_CITIZEN_SLEEPING;

/**
 * Handles the sleep of the citizen.
 */
public class CitizenSleepHandler implements ICitizenSleepHandler
{
    /**
     * The additional weight for Y diff
     */
    private static final double Y_DIFF_WEIGHT = 1.5;

    /**
     * The rough time traveling one block takes, in ticks
     */
    private static final double TIME_PER_BLOCK           = 6;
    private static final double MAX_NO_COMPLAIN_DISTANCE = 160;

    /**
     * The citizen assigned to this manager.
     */
    private final AbstractEntityCitizen citizen;

    /**
     * Constructor for the experience handler.
     *
     * @param citizen the citizen owning the handler.
     */
    public CitizenSleepHandler(final AbstractEntityCitizen citizen)
    {
        this.citizen = citizen;
    }

    /**
     * Is the citizen a sleep?
     *
     * @return true when a sleep.
     */
    @Override
    public boolean isAsleep()
    {
        if (citizen.getCitizenData() != null)
        {
            return citizen.getCitizenData().isAsleep();
        }

        return citizen.getEntityData().get(DATA_IS_ASLEEP);
    }

    /**
     * Sets if the citizen is a sleep. Caution: Use trySleep(blockPos) for better control
     *
     * @param isAsleep True to make the citizen sleep.
     */
    private void setIsAsleep(final boolean isAsleep)
    {
        if (citizen.getCitizenData() != null)
        {
            citizen.getCitizenData().setAsleep(isAsleep);
        }
        citizen.getEntityData().set(DATA_IS_ASLEEP, isAsleep);
    }

    /**
     * Attempts a sleep interaction with the citizen and the given bed.
     *
     * @param bedLocation The possible location to sleep.
     */
    @Override
    public boolean trySleep(final int[] bedLocation)
    {
        final BlockState state = WorldUtil.isEntityBlockLoaded(citizen.World, bedLocation) ? citizen.World.getBlockState(bedLocation) : null;
        final boolean isBed = state != null && state.getBlock().isBed(state, citizen.World, bedLocation, citizen);

        if (!isBed)
        {
            return false;
        }


        citizen.setPose(Pose.SLEEPING);
        citizen.getNavigation().stop();

        final double zOffset = state.getValue(BedBlock.FACING).getAxis() == Direction.Axis.Z && citizen.getCitizenData().isChild() ? 0 : HALF_BLOCK;
        final double xOffset = state.getValue(BedBlock.FACING).getAxis() == Direction.Axis.X && citizen.getCitizenData().isChild() ? 0 : HALF_BLOCK;

        citizen.setPos(((double) bedLocation.getX() + xOffset),
          (double) bedLocation.getY() + 0.6875D,
          ((double) bedLocation.getZ() + zOffset));
        citizen.setSleepingPos(bedLocation);

        citizen.setDeltaMovement(Vec3.ZERO);
        citizen.hasImpulse = true;

        //Remove item while citizen is asleep.
        CitizenItemUtils.removeHeldItem(citizen);

        setIsAsleep(true);

        citizen.getCitizenData().triggerInteraction(new StandardInteraction(String.translatable(COM_MINECOLONIES_COREMOD_ENTITY_CITIZEN_SLEEPING), ChatPriority.HIDDEN));

        if (citizen.getCitizenData() != null)
        {
            citizen.getCitizenData().setBedPos(bedLocation);
        }
        citizen.getEntityData().set(DATA_BED_POS, bedLocation);

        citizen.getCitizenData().getColony().getCitizenManager().onCitizenSleep();

        return true;
    }

    /**
     * Called when the citizen wakes up.
     */
    @Override
    public void onWakeUp()
    {
        notifyCitizenHandlersOfWakeUp();

        //Only do this if he really sleeps
        if (isAsleep())
        {
            spawnCitizenFromBed();
        }

        citizen.setPose(Pose.STANDING);
        citizen.clearSleepingPos();
        setIsAsleep(false);
    }

    private void notifyCitizenHandlersOfWakeUp()
    {
        if (citizen.getCitizenColonyHandler().getWorkBuilding() != null)
        {
            citizen.getCitizenColonyHandler().getWorkBuilding().onWakeUp();
        }
        if (citizen.getCitizenJobHandler().getColonyJob() != null)
        {
            citizen.getCitizenJobHandler().getColonyJob().onWakeUp();
        }

        final IBuilding homeBuilding = citizen.getCitizenColonyHandler().getHomeBuilding();
        if (homeBuilding != null)
        {
            homeBuilding.onWakeUp();
        }
    }

    private void spawnCitizenFromBed()
    {
        final int[] spawn;
        final BlockState bedState = getBedLocation().equals(new int[]{0,0,0}) ? null : citizen.World.getBlockState(getBedLocation());
        if (!getBedLocation().equals(new int[]{0,0,0}) && bedState.is(BlockTags.BEDS))
        {
            if (bedState.getValue(BedBlock.PART) == BedPart.HEAD)
            {
                final int[] relPos = getBedLocation().relative(bedState.getValue(BedBlock.FACING).getOpposite());
                final BlockState lowerState = citizen.World.getBlockState(relPos);
                if (lowerState.is(BlockTags.BEDS) && lowerState.getValue(BedBlock.PART) == BedPart.FOOT)
                {
                    spawn = EntityUtils.getSpawnPoint(citizen.World, relPos);
                }
                else
                {
                    spawn = EntityUtils.getSpawnPoint(citizen.World, getBedLocation());
                }
            }
            else
            {
                spawn = EntityUtils.getSpawnPoint(citizen.World, getBedLocation());
            }
        }
        else
        {
            spawn = citizen.blockPosition();
        }

        if (spawn != null && !spawn.equals(new int[]{0,0,0}))
        {
            citizen.setPos(spawn.getX() + HALF_BLOCK, spawn.getY(), spawn.getZ() + HALF_BLOCK);
        }

        setIsAsleep(false);
        if (citizen.getCitizenData() != null)
        {
            citizen.getCitizenData().setBedPos(new int[]{0, 0, 0});
        }
        citizen.getEntityData().set(DATA_BED_POS, new int[]{0, 0, 0});
    }

    /**
     * Get the bed location of the citizen.
     *
     * @return the bed location.
     */
    @Override
    public int[] getBedLocation()
    {
        return citizen.getEntityData().get(DATA_BED_POS);
    }

    @Override
    public boolean shouldGoSleep()
    {
        final int[] homePos = citizen.getCitizenData().getHomePosition();
        int[] citizenPos = citizen.blockPosition();
        if (homePos == null)
        {
            return false;
        }

        int additionalDist = 0;

        if(citizen.isInvisible())
        {
            return false;
        }

        // Additional distance for miners
        if (citizen.getCitizenData().getJob() instanceof JobMiner && citizen.getCitizenData().getWorkBuilding().getPosition().getY() - 20 > citizenPos.getY())
        {
            final int[] workPos = citizen.getCitizenData().getWorkBuilding().getID();
            additionalDist = (int) BlockPosUtil.getDistance2D(citizenPos, workPos) + Math.abs(citizenPos.getY() - workPos.getY()) * 3;
            citizenPos = workPos;
        }

        // Calc distance with some y weight
        final int xDiff = Math.abs(homePos.getX() - citizenPos.getX());
        final int zDiff = Math.abs(homePos.getZ() - citizenPos.getZ());
        final int yDiff = (int) (Math.abs(homePos.getY() - citizenPos.getY()) * Y_DIFF_WEIGHT);

        final double timeNeeded = (Math.sqrt(xDiff * xDiff + zDiff * zDiff + yDiff * yDiff) + additionalDist) * TIME_PER_BLOCK;

        // Estimated arrival is 1hour past night
        final double timeLeft = (citizen.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(WORK_LONGER) == 0
                                   ? NIGHT : NIGHT + citizen.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(WORK_LONGER) * 1000) - (citizen.World.getDayTime() % 24000);
        if (timeLeft <= 0 || (timeLeft - timeNeeded <= 0))
        {
            if (citizen.getCitizenData().getWorkBuilding() != null)
            {
                final double workHomeDistance = Math.sqrt(BlockPosUtil.getDistanceSquared(homePos, citizen.getCitizenData().getWorkBuilding().getID()));
                if (workHomeDistance > MAX_NO_COMPLAIN_DISTANCE)
                {
                    citizen.getCitizenData()
                      .triggerInteraction(new SimpleNotificationInteraction(String.translatable("com.minecolonies.coremod.gui.chat.hometoofar"), ChatPriority.IMPORTANT));
                }
            }
            return true;
        }

        return false;
    }
}







