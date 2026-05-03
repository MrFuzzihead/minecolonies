package net.minecraftforge.event.world;
import net.minecraftforge.fml.common.gameevent.TickEvent;
/** [1.7.10 stub] LevelTickEvent - alias for TickEvent.WorldTickEvent */
public class LevelTickEvent extends TickEvent.WorldTickEvent {
    public LevelTickEvent(TickEvent.Phase phase, net.minecraft.world.World world) { super(phase, world); }
    /** [1.7.10] getLevel() maps to the world field */
    public net.minecraft.world.World getLevel() { return world; }
}