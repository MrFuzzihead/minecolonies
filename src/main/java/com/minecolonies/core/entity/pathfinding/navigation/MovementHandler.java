package com.minecolonies.core.entity.pathfinding.navigation;

import com.minecolonies.api.util.ShapeUtil;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
// [1.7.10] tags removed
import net.minecraft.util.MathHelper;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
// [1.7.10] BlockPathTypes removed
import net.minecraft.pathfinding.PathPointEvaluator;
// [1.7.10] world.phys removed

/**
 * Custom movement handler for minecolonies citizens (avoid jumping so much).
 * Note that the "speed" variable of the super is a speedFactor to our attributes base speed.
 */
public class MovementHandler extends MoveControl
{

    /**
     * Speed attribute holder
     */
    final AttributeInstance speedAtr;

    /**
     * Step height
     */
    private float stepHeight;

    /**
     * Speed value
     */
    private float speedValue;

    /**
     * Tick timer for jumping to not get stuck in jumping
     */
    private int jumpingticks = 0;

    public MovementHandler(EntityCreature EntityCreature)
    {
        super(EntityCreature);
        this.speedAtr = this.EntityCreature.getAttribute(Attributes.MOVEMENT_SPEED);
        stepHeight = EntityCreature.getStepHeight();
        speedValue = (float) speedAtr.getValue();
    }

    @Override
    public void tick()
    {
        if (EntityCreature.tickCount % 20 == 0)
        {
            stepHeight = this.EntityCreature.getStepHeight();
            speedValue = (float) speedAtr.getValue();
        }

        if (this.operation == net.minecraft.world.entity.ai.control.MoveControl.Operation.STRAFE)
        {
            final float speedAtt = speedValue;
            float speed = (float) this.speedModifier * speedAtt;
            float forward = this.strafeForwards;
            float strafe = this.strafeRight;
            float totalMovement = Mth.sqrt(forward * forward + strafe * strafe);
            if (totalMovement < 1.0F)
            {
                totalMovement = 1.0F;
            }

            totalMovement = speed / totalMovement;
            forward = forward * totalMovement;
            strafe = strafe * totalMovement;
            final float sinRotation = Mth.sin(this.EntityCreature.getYRot() * ((float) Math.PI / 180F));
            final float cosRotation = Mth.cos(this.EntityCreature.getYRot() * ((float) Math.PI / 180F));
            final float rot1 = forward * cosRotation - strafe * sinRotation;
            final float rot2 = strafe * cosRotation + forward * sinRotation;
            final PathNavigate pathnavigator = this.EntityCreature.getNavigation();

            final NodeEvaluator nodeprocessor = pathnavigator.getNodeEvaluator();
            if (nodeprocessor.getBlockPathType(this.EntityCreature.World,
              Mth.floor(this.EntityCreature.getX() + (double) rot1),
              Mth.floor(this.EntityCreature.getY()),
              Mth.floor(this.EntityCreature.getZ() + (double) rot2)) != BlockPathTypes.WALKABLE)
            {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
                speed = speedAtt;
            }

            this.EntityCreature.setSpeed(speed);
            this.EntityCreature.setZza(this.strafeForwards);
            this.EntityCreature.setXxa(this.strafeRight);
            this.operation = net.minecraft.world.entity.ai.control.MoveControl.Operation.WAIT;
        }
        else if (this.operation == net.minecraft.world.entity.ai.control.MoveControl.Operation.MOVE_TO)
        {
            this.operation = net.minecraft.world.entity.ai.control.MoveControl.Operation.WAIT;
            final double xDif = this.wantedX - this.EntityCreature.getX();
            final double zDif = this.wantedZ - this.EntityCreature.getZ();
            final double yDif = this.wantedY - this.EntityCreature.getY();
            final double dist = xDif * xDif + yDif * yDif + zDif * zDif;
            if (dist < (double) 2.5000003E-7F)
            {
                this.EntityCreature.setZza(0.0F);
                return;
            }

            final float range = (float) (Mth.atan2(zDif, xDif) * (double) (180F / (float) Math.PI)) - 90.0F;
            this.EntityCreature.setYRot(this.rotlerp(this.EntityCreature.getYRot(), range, 90.0F));
            this.EntityCreature.setSpeed((float) (this.speedModifier * speedValue));
            final int[] blockPos = this.EntityCreature.blockPosition();
            final BlockState blockstate = this.EntityCreature.World.getBlockState(blockPos);

            if (PathfindingUtils.isWater(EntityCreature.World, EntityCreature.blockPosition(), blockstate, blockstate.getFluidState())
                  && PathfindingUtils.isWater(EntityCreature.World, EntityCreature.blockPosition().above(), null, null))
            {
                if (yDif != 0.0D)
                {
                    double d3 = Math.sqrt(xDif * xDif + yDif * yDif + zDif * zDif);
                    this.EntityCreature.setDeltaMovement(this.EntityCreature.getDeltaMovement().add(0, (double) this.EntityCreature.getSpeed() * ((yDif + 0.3) / d3) * 0.1D, 0));
                }

                return;
            }

            final Block block = blockstate.getBlock();
            final VoxelShape voxelshape = blockstate.getCollisionShape(this.EntityCreature.World, blockPos);
            if (((yDif > (double) stepHeight || yDif > 0 && !EntityCreature.onGround()) && xDif * xDif + zDif * zDif < (double) Math.max(1.0F, this.EntityCreature.getBbWidth()))
                  || (!ShapeUtil.isEmpty(voxelshape) && this.EntityCreature.getY() < ShapeUtil.max(voxelshape, Direction.Axis.Y) + (double) blockPos[1] && !blockstate.is(BlockTags.DOORS)
                        && !blockstate.is(
              BlockTags.FENCES) && !blockstate.is(BlockTags.FENCE_GATES))
                       && !block.isLadder(blockstate, this.EntityCreature.World, blockPos, this.EntityCreature))
            {
                jumpingticks = 0;
                this.EntityCreature.getJumpControl().jump();
                this.operation = net.minecraft.world.entity.ai.control.MoveControl.Operation.JUMPING;
            }
        }
        else if (this.operation == net.minecraft.world.entity.ai.control.MoveControl.Operation.JUMPING)
        {
            jumpingticks++;
            this.EntityCreature.setSpeed((float) (this.speedModifier * speedValue));

            // Avoid beeing stuck in jumping while in liquids
            final int[] blockPos = this.EntityCreature.blockPosition();
            final BlockState blockstate = this.EntityCreature.World.getBlockState(blockPos);
            if (this.EntityCreature.onGround() || blockstate.liquid())
            {
                this.operation = net.minecraft.world.entity.ai.control.MoveControl.Operation.WAIT;
            }
        }
        else
        {
            this.EntityCreature.setZza(0.0F);
        }
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speedIn)
    {
        super.setWantedPosition(x, y, z, speedIn);
        if (this.operation != MoveControl.Operation.JUMPING || (wantedX != x || wantedY != y || wantedZ != z) || jumpingticks > 20)
        {
            this.operation = MoveControl.Operation.MOVE_TO;
        }
    }
}






