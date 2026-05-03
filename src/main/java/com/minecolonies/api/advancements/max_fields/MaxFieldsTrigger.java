package com.minecolonies.api.advancements.max_fields;
import com.minecolonies.api.advancements.AbstractCriterionTrigger;
import com.minecolonies.api.advancements.CriterionListeners;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.util.ResourceLocation;
// [1.7.10 BACKPORT] Stubbed out — advancements do not exist in Minecraft 1.7.10.
// All trigger/listener methods are no-ops.
/** Stub trigger — no-op in 1.7.10. */
public class MaxFieldsTrigger extends AbstractCriterionTrigger<CriterionListeners<MaxFieldsCriterionInstance>, MaxFieldsCriterionInstance>
{
    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_MAX_FIELDS);
    public MaxFieldsTrigger()
    {
        super(ID, CriterionListeners::new);
    }
    // [1.7.10 BACKPORT] trigger(...) methods are no-ops; no advancements system.
}
