package net.minecraft.world.entity.ai.control;

import net.minecraft.entity.EntityCreature;

/** [1.7.10 bridge] MoveControl - maps to EntityMoveHelper */
public class MoveControl
{
    public enum Operation { WAIT, MOVE_TO, STRAFE, JUMPING }

    protected final EntityCreature EntityCreature;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected double speedModifier;
    protected float strafeForwards;
    protected float strafeRight;
    protected Operation operation = Operation.WAIT;

    public MoveControl(EntityCreature mob)
    {
        this.EntityCreature = mob;
    }

    public void tick() {}

    public void setWantedPosition(double x, double y, double z, double speedIn)
    {
        this.wantedX = x;
        this.wantedY = y;
        this.wantedZ = z;
        this.speedModifier = speedIn;
        if (this.operation != Operation.JUMPING)
        {
            this.operation = Operation.MOVE_TO;
        }
    }
}
