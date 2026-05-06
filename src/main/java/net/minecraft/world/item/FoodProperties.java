package net.minecraft.world.item;
import java.util.ArrayList;
import java.util.List;
/** [1.7.10] Stub for 1.21 FoodProperties */
public class FoodProperties {
    private final int nutrition;
    private final float saturationModifier;
    private final boolean isMeat;
    private final boolean canAlwaysEat;
    private final boolean isFastFood;
    private FoodProperties(Builder b) {
        this.nutrition = b.nutrition;
        this.saturationModifier = b.saturationModifier;
        this.isMeat = b.isMeat;
        this.canAlwaysEat = b.canAlwaysEat;
        this.isFastFood = b.isFastFood;
    }
    public int getNutrition() { return nutrition; }
    public float getSaturationModifier() { return saturationModifier; }
    public boolean isMeat() { return isMeat; }
    public boolean canAlwaysEat() { return canAlwaysEat; }
    public boolean isFastFood() { return isFastFood; }
    public static class Builder {
        int nutrition;
        float saturationModifier;
        boolean isMeat, canAlwaysEat, isFastFood;
        public Builder nutrition(int n) { this.nutrition = n; return this; }
        public Builder saturationMod(float s) { this.saturationModifier = s; return this; }
        public Builder meat() { this.isMeat = true; return this; }
        public Builder alwaysEat() { this.canAlwaysEat = true; return this; }
        public Builder fast() { this.isFastFood = true; return this; }
        public Builder effect(java.util.function.Supplier<?> e, float p) { return this; }
        public FoodProperties build() { return new FoodProperties(this); }
    }
}