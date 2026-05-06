package net.minecraftforge.common.brewing;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
/** [1.7.10 stub] BrewingRecipeRegistry */
public class BrewingRecipeRegistry { private static final List<IBrewingRecipe> recipes = new ArrayList<>(); public static List<IBrewingRecipe> getRecipes() { return recipes; } public static boolean hasOutput(ItemStack input, ItemStack ingredient) { return false; } }