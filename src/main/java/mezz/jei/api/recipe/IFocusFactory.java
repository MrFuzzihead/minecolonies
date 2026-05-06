package mezz.jei.api.recipe;

/** [1.7.10 stub] IFocusFactory. */
public interface IFocusFactory
{
    <V> IFocus<V> createFocus(RecipeIngredientRole role, Object type, V value);
    IFocusGroup createFocusGroup(java.util.List<? extends IFocus<?>> focuses);
}

