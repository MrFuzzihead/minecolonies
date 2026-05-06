package net.minecraft.world.item;
import net.minecraft.item.Item;
/** [1.7.10] Stub for 1.21 ArmorItem */
public class ArmorItem extends net.minecraft.world.item.Item {
    public enum Type { BOOTS, LEGGINGS, CHESTPLATE, HELMET, BODY }
    public ArmorItem() { super(); }
    public ArmorItem(ArmorMaterial material, Type type, Properties properties) { super(); }
    public ArmorMaterial getMaterial() { return null; }
    public Type getType() { return Type.HELMET; }
}