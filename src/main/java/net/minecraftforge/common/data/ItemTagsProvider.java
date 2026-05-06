package net.minecraftforge.common.data;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
/** [1.7.10 bridge] ItemTagsProvider */
public abstract class ItemTagsProvider extends TagsProvider<Object> {
    protected ItemTagsProvider(PackOutput output, CompletableFuture<?> provider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper helper) {
        super(output, null, provider, null, helper);
    }
    protected ItemTagsProvider(PackOutput output, CompletableFuture<?> provider, String modId, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper helper) {
        super(output, null, provider, modId, helper);
    }
}
