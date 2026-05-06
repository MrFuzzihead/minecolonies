package net.minecraftforge.client.model.generators;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import java.util.concurrent.CompletableFuture;
/** [1.7.10 bridge] ItemModelProvider */
public abstract class ItemModelProvider implements DataProvider {
    protected final String modid;
    public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper helper) { this.modid = modid; }
    protected abstract void registerModels();
    protected ModelFile.ExistingModelFile getExistingFile(ResourceLocation loc) { return new ModelFile.ExistingModelFile(loc); }
    protected Object generated(Object... args) { return null; }
    protected Object handheld(Object... args) { return null; }
    public ModelFile.ExistingModelFile existingFile(ResourceLocation loc) { return new ModelFile.ExistingModelFile(loc); }
    @Override public String getName() { return "ItemModelProvider"; }
    @Override public CompletableFuture<?> run(CachedOutput cache) { return CompletableFuture.completedFuture(null); }
    public Object withExistingParent(String name, String parent) { return null; }
    public Object withExistingParent(String name, ResourceLocation parent) { return null; }
    public Object basicItem(Object item) { return null; }
    public Object basicItem(ResourceLocation loc) { return null; }
}
