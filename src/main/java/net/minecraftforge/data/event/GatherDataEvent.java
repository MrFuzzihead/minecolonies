package net.minecraftforge.data.event;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import java.util.concurrent.CompletableFuture;

/** [1.7.10 bridge] GatherDataEvent - no 1.7.10 equivalent (data generation) */
public class GatherDataEvent
{
    private final DataGenerator generator;
    private final ExistingFileHelper existingFileHelper;
    private final CompletableFuture<?> lookupProvider;

    public GatherDataEvent(final DataGenerator generator, final ExistingFileHelper existingFileHelper, final CompletableFuture<?> lookupProvider)
    {
        this.generator = generator;
        this.existingFileHelper = existingFileHelper;
        this.lookupProvider = lookupProvider;
    }

    public DataGenerator getGenerator() { return generator; }
    public ExistingFileHelper getExistingFileHelper() { return existingFileHelper; }
    public CompletableFuture<?> getLookupProvider() { return lookupProvider; }
    public boolean includeClient() { return false; }
    public boolean includeServer() { return false; }
}

