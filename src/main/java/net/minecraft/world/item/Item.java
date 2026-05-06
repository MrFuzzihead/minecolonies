package net.minecraft.world.item;
/** [1.7.10 bridge] Compatibility stub for 1.21 net.minecraft.world.item.Item */
public class Item extends net.minecraft.item.Item {
    public Item() { super(); }
    public Item(Properties properties) { super(); }
    public net.minecraft.world.item.Item asItem() { return this; }
    /** Alias for net.minecraft.world.item.Properties as inner class */
    public static class Properties extends net.minecraft.world.item.Properties {
        public Properties() { super(); }
    }
}