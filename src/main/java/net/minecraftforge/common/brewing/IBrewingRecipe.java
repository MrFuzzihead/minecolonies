package net.minecraftforge.common.brewing;
import net.minecraft.item.ItemStack;
/** [1.7.10 stub] IBrewingRecipe */
public interface IBrewingRecipe { boolean isInput(ItemStack stack); boolean isIngredient(ItemStack stack); ItemStack getOutput(ItemStack input, ItemStack ingredient); }