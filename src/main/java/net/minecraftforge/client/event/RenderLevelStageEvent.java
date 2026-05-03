package net.minecraftforge.client.event;
/** [1.7.10 stub] RenderLevelStageEvent */
public class RenderLevelStageEvent
{
    public enum Stage { AFTER_SKY, AFTER_SOLID_BLOCKS, AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS, AFTER_CUTOUT_BLOCKS, AFTER_ENTITIES, AFTER_BLOCK_ENTITIES, AFTER_TRANSLUCENT_BLOCKS, AFTER_TRIPWIRE_BLOCKS, AFTER_PARTICLES, AFTER_WEATHER, AFTER_LEVEL, AFTER_ALL }
    public Stage getStage() { return Stage.AFTER_ALL; }
    public Object getPoseStack() { return null; }
    public Object getCamera() { return null; }
    public float getPartialTick() { return 0; }
    public Object getProjectionMatrix() { return null; }
}
