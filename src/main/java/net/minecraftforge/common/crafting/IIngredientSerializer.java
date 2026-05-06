package net.minecraftforge.common.crafting;
import net.minecraft.world.item.crafting.Ingredient;
import com.google.gson.JsonObject;
/** [1.7.10] Stub for IIngredientSerializer */
public interface IIngredientSerializer<T extends Ingredient> {
    T parse(com.google.gson.JsonObject json);
    T parse(net.minecraft.network.PacketBuffer buf);
    void write(net.minecraft.network.PacketBuffer buf, T ingredient);
}