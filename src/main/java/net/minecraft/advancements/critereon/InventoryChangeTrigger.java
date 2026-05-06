package net.minecraft.advancements.critereon;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.CriterionTriggerInstance;
/** [1.7.10 bridge] InventoryChangeTrigger */
public class InventoryChangeTrigger {
    public static class TriggerInstance implements CriterionTriggerInstance {
        public static TriggerInstance hasItems(ItemLike... items) { return new TriggerInstance(); }
        public static TriggerInstance hasItems(ItemPredicate... predicates) { return new TriggerInstance(); }
    }
}
