package com.minecolonies.core.entity.mobs.aitasks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.minecolonies.api.blocks.decorative.AbstractBlockGate;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] BlockState -> int metadata
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
// [1.7.10] world.phys removed

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import static com.minecolonies.api.research.util.ResearchConstants.MECHANIC_ENHANCED_GATES;

/**
 * Break door entity AI with mutex.
 */
public class EntityAIBreakDoor extends BreakDoorGoal
{
    /**
     * Previous break pos
     */
    private int[] prevDoorPos = new int[]{0,0,0};

    /**
     * The door's hardness we're breaking
     */
    private int hardness = 0;

    /**
     * Amount of nearby raiders
     */
    private int breakChance = 1;

    public EntityAIBreakDoor(final EntityCreature entityIn)
    {
        super(entityIn, difficulty -> difficulty.getId() > 0);
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse()
    {
        return super.canContinueToUse() && !EntityCreature.World.isEmptyBlock(doorPos);
    }

    @Override
    public void start()
    {
        super.start();
        if (!Arrays.equals(doorPos, prevDoorPos))
        {
            this.breakTime = 0;
        }
        prevDoorPos = doorPos;
        hardness = (int) (1 + EntityCreature.World.getBlockState(doorPos).getDestroySpeed(EntityCreature.World, doorPos));

        // No stuck during door break
        if (EntityCreature instanceof AbstractEntityMinecoloniesRaider)
        {
            ((AbstractEntityMinecoloniesRaider) EntityCreature).setCanBeStuck(false);
        }
    }

    public void stop()
    {
        super.stop();
        this.EntityCreature.World.destroyBlockProgress(this.EntityCreature.getId(), this.doorPos, -1);
        if (EntityCreature instanceof AbstractEntityMinecoloniesRaider)
        {
            ((AbstractEntityMinecoloniesRaider) EntityCreature).setCanBeStuck(true);
        }
    }

    @Override
    public void tick()
    {
        if (EntityCreature.getCommandSenderWorld().getDifficulty().getId() < 2 || !MineColonies.getConfig().getServer().raidersbreakdoors.get())
        {
            breakTime = 0;
            return;
        }

        // Only advances breaking time in relation to hardness
        if (this.EntityCreature.getRandom().nextInt(breakChance) != 0)
        {
            this.breakTime--;
        }
        else
        {
            double fasterBreakPerXNearby = 5;

            if (EntityCreature instanceof AbstractEntityMinecoloniesRaider && !EntityCreature.World.isClientSide() && EntityCreature.World.getBlockState(doorPos).getBlock() instanceof AbstractBlockGate)
            {
                final IColony colony = ((AbstractEntityMinecoloniesRaider) EntityCreature).getColony();

                fasterBreakPerXNearby += colony.getResearchManager().getResearchEffects().getEffectStrength(MECHANIC_ENHANCED_GATES);
            }

            fasterBreakPerXNearby /= 2;
            breakChance = (int) Math.max(1,
              hardness / (1 + (EntityCreature.World.getEntitiesOfClass(AbstractEntityMinecoloniesRaider.class, EntityCreature.getBoundingBox().inflate(5)).size() / fasterBreakPerXNearby)));

            // Alert nearby guards
            if (this.EntityCreature.getRandom().nextInt(breakChance) == 0 && EntityCreature instanceof AbstractEntityMinecoloniesRaider raider && EntityCreature.World.getBlockState(doorPos)
                .getBlock() instanceof AbstractBlockGate)
            {
                // Alerts guards of raiders reaching a building
                final List<AbstractEntityCitizen> possibleGuards = new ArrayList<>();

                for (final ICitizenData entry : raider.getColony().getCitizenManager().getCitizens())
                {
                    if (entry.getEntity().isPresent()
                        && entry.getJob() instanceof AbstractJobGuard
                        && BlockPosUtil.getDistanceSquared(entry.getEntity().get().blockPosition(), doorPos) < 100 * 100 && entry.getJob().getWorkerAI() != null)
                    {
                        if (((AbstractEntityAIGuard<?, ?>) entry.getJob().getWorkerAI()).canHelp(doorPos) && !doorPos.equals(((AbstractEntityAIGuard<?, ?>) entry.getJob()
                            .getWorkerAI()).getCurrentPatrolPoint()))
                        {
                            possibleGuards.add(entry.getEntity().get());
                        }
                    }
                }

                possibleGuards.sort(Comparator.comparingInt(guard -> (int) BlockPosUtil.distSqr(doorPos, (int)guard.posX, (int)guard.posY, (int)guard.posZ)));
                // [1.7.10] Vec3.atCenterOf not available; compute center offset manually
                final int[] colonyCenter = raider.getColony().getCenter();
                final double dx = colonyCenter[0] - doorPos[0];
                final double dz = colonyCenter[2] - doorPos[2];
                final double len = Math.sqrt(dx * dx + dz * dz);
                final int[] gotoPos = new int[]{doorPos[0] + (len > 0 ? (int)(dx / len * 3) : 0), doorPos[1], doorPos[2] + (len > 0 ? (int)(dz / len * 3) : 0)};

                for (int i = 0; i < possibleGuards.size() && i <= 3; i++)
                {
                    ((AbstractBuildingGuards) possibleGuards.get(i).getCitizenData().getWorkBuilding()).setTempNextPatrolPoint(gotoPos);
                }
            }
        }

        if (this.breakTime == this.getDoorBreakTime() - 1)
        {
            final BlockState toBreak = EntityCreature.World.getBlockState(doorPos);
            if (toBreak.getBlock() instanceof AbstractBlockGate)
            {
                ((AbstractBlockGate) toBreak.getBlock()).removeGate(EntityCreature.World, doorPos, toBreak.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
            }
        }

        super.tick();
    }
}




