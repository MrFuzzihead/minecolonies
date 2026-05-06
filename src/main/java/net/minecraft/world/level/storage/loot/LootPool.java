package net.minecraft.world.level.storage.loot;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.function.Consumer;
/** [1.7.10 bridge] LootPool */
public class LootPool {
    public static Builder lootPool() { return new Builder(); }
    public static class Builder {
        public Builder setRolls(ConstantValue v) { return this; }
        public Builder setRolls(Object v) { return this; }
        public Builder add(Object entry) { return this; }
        public Builder when(Object condition) { return this; }
        public LootPool build() { return new LootPool(); }
    }
}
