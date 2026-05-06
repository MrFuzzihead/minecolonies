package mezz.jei.api.gui.handlers;

import net.minecraft.client.renderer.Rect2i;
import java.util.List;

/** [1.7.10 stub] IGhostIngredientHandler. */
public interface IGhostIngredientHandler<G>
{
    interface Target<I>
    {
        Rect2i getArea();
        void accept(I ingredient);
    }

    <I> List<Target<I>> getTargetsTyped(G gui, mezz.jei.api.ingredients.ITypedIngredient<I> ingredient, boolean doStart);
    default void onComplete() {}
}
