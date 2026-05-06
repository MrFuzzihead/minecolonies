package mezz.jei.api.ingredients;

/** [1.7.10 stub] ITypedIngredient. */
public interface ITypedIngredient<T>
{
    T getIngredient();
    IIngredientType<T> getType();
}
