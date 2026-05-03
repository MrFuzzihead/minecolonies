package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.items.ModTags;
// [1.7.10] HolderLookup removed
// [1.7.10] data removed
// [1.7.10] data removed
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
// [1.7.10] world.entity removed
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

public class DefaultEntityTypeTagsProvider extends EntityTypeTagsProvider
{
    public DefaultEntityTypeTagsProvider(final PackOutput output,
      final CompletableFuture<HolderLookup.Provider> lookupProvider,
      @Nullable final ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider holder)
    {
        NBTBase(ModTags.hostile).add(EntityType.SLIME);
        NBTBase(ModTags.mobAttackBlacklist).add(EntityType.ENDERMAN, EntityType.LLAMA);
        NBTBase(ModTags.freeToInteractWith).addOptional(new ResourceLocation("corpse", "corpse"));

        final TagAppender<EntityType<?>> raiderTagAppender = NBTBase(ModTags.raiders);
        ModEntities.getRaiders().forEach(raiderType -> raiderTagAppender.add(TagEntry.element(EntityType.getKey(raiderType))));
    }
}



