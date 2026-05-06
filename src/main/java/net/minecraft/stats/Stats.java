package net.minecraft.stats;
import net.minecraft.util.ResourceLocation;
/** [1.7.10] Stub for 1.21 Stats */
public class Stats {
    public static final ResourceLocation ITEM_USED = new ResourceLocation("minecraft", "item_used");
    public static final ResourceLocation ITEM_PICKED_UP = new ResourceLocation("minecraft", "item_picked_up");
    public static final ResourceLocation ITEM_DROPPED = new ResourceLocation("minecraft", "item_dropped");
    public static final ResourceLocation ITEM_BROKEN = new ResourceLocation("minecraft", "item_broken");
    public static ResourceLocation ITEM_CRAFTED = new ResourceLocation("minecraft", "item_crafted");
    public static ResourceLocation makeItemStatId(net.minecraft.item.Item item, String stat) { return new ResourceLocation("minecraft", stat); }
}