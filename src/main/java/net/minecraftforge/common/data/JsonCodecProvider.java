package net.minecraftforge.common.data;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.ResourceLocation;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
/** [1.7.10 bridge] JsonCodecProvider */
public abstract class JsonCodecProvider<T> implements DataProvider {
    protected JsonCodecProvider(PackOutput output, ExistingFileHelper helper, String modId, DynamicOps<?> ops, Object packType, String directory, Codec<T> codec, Map<ResourceLocation, T> entries) {}
    @Override public String getName() { return "JsonCodecProvider"; }
    @Override public CompletableFuture<?> run(CachedOutput cache) { return CompletableFuture.completedFuture(null); }
}
