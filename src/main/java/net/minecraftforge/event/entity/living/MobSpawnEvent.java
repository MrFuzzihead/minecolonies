package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

/** [1.7.10 bridge] MobSpawnEvent */
public class MobSpawnEvent
{
    public static class PositionCheck
    {
        private final EntityLiving entity;
        private final World world;
        private final double x, y, z;
        private boolean canceled = false;
        private Result result = Result.DEFAULT;

        public enum Result { DEFAULT, ALLOW, DENY }

        public PositionCheck(EntityLiving e, World w, double x, double y, double z)
        {
            this.entity = e;
            this.world = w;
            this.x = x; this.y = y; this.z = z;
        }
        public EntityLiving getEntity() { return entity; }
        public World getLevel() { return world; }
        public boolean isCanceled() { return canceled; }
        public void setCanceled(boolean v) { this.canceled = v; }
        public Result getResult() { return result; }
        public void setResult(Result r) { this.result = r; }
    }
}

