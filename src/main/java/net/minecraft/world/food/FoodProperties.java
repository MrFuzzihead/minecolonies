package net.minecraft.world.food;

/**
 * [1.7.10] Shim for 1.21 FoodProperties.
 * In 1.7.10, food stats are accessed via ItemFood methods:
 * - getHealAmount() -> ItemFood.func_150906_h()
 * - getSaturationModifier() -> ItemFood.func_150905_g(stack)
 */
public class FoodProperties
{
    private final int   nutrition;
    private final float saturationModifier;

    public FoodProperties(final int nutrition, final float saturationModifier)
    {
        this.nutrition          = nutrition;
        this.saturationModifier = saturationModifier;
    }

    /** Returns hunger points restored. */
    public int getNutrition()
    {
        return nutrition;
    }

    /** Returns saturation modifier. */
    public float getSaturationModifier()
    {
        return saturationModifier;
    }
}

