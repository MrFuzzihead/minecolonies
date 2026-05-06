package net.minecraftforge.common.data;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
/** [1.7.10 bridge] EntityTypeTagsProvider */
public abstract class EntityTypeTagsProvider extends TagsProvider<Object> {
    protected EntityTypeTagsProvider(PackOutput output, CompletableFuture<?> provider, String modId, @Nullable ExistingFileHelper helper) {
        super(output, null, provider, modId, helper);
    }
}
