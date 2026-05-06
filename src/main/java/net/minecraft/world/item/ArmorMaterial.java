package net.minecraft.world.item;
import java.util.function.Supplier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvent;
/** [1.7.10] Stub for 1.21 ArmorMaterial interface */
public interface ArmorMaterial {
    int getDurabilityForType(ArmorItem.Type type);
    int getDefenseForType(ArmorItem.Type type);
    int getEnchantmentValue();
    SoundEvent getEquipSound();
    Supplier<Ingredient> getRepairIngredient();
    String getName();
    float getToughness();
    float getKnockbackResistance();
}