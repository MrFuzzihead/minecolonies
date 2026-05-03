package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.util.DamageSourceKeys;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10] HolderLookup removed
// [1.7.10] Registries removed
// [1.7.10] data removed
// [1.7.10] data removed
// [1.7.10] tags removed
// [1.7.10] net.minecraft.util.DamageSource removed
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


@SuppressWarnings("unchecked")
public class DefaultDamageTagsProvider extends TagsProvider<DamageType>
{
    public DefaultDamageTagsProvider(
      @NotNull final PackOutput output,
      final CompletableFuture<HolderLookup.Provider> lookupProvider, final ExistingFileHelper helper)
    {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Constants.MOD_ID, helper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider lookup)
    {
        NBTBase(DamageTypeTags.BYPASSES_ARMOR).add(DamageSourceKeys.WAKEY, DamageSourceKeys.GUARD_PVP);
        NBTBase(DamageTypeTags.IS_PROJECTILE).add(DamageSourceKeys.SPEAR);
    }
}


