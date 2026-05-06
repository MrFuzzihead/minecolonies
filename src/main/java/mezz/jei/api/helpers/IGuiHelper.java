package mezz.jei.api.helpers;

/** [1.7.10 stub] IGuiHelper. */
public interface IGuiHelper
{
    mezz.jei.api.gui.drawable.IDrawable createDrawable(net.minecraft.util.ResourceLocation location, int u, int v, int width, int height);
    mezz.jei.api.gui.drawable.IDrawable createDrawableItemStack(net.minecraft.item.ItemStack stack);
    mezz.jei.api.gui.ITickTimer createAnimatedRecipeArrow(int ticksPerCycle);
    mezz.jei.api.gui.ITickTimer createTickTimer(int ticksPerCycle, int maxValue, boolean countDown);
}

