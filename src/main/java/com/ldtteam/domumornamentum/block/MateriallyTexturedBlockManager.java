package com.ldtteam.domumornamentum.block;

import net.minecraft.block.Block;
import java.util.Collections;
import java.util.List;

/**
 * [1.7.10] Compatibility stub for DomumOrnamentum MateriallyTexturedBlockManager.
 */
public class MateriallyTexturedBlockManager
{
    private static final MateriallyTexturedBlockManager INSTANCE = new MateriallyTexturedBlockManager();

    public static MateriallyTexturedBlockManager getInstance()
    {
        return INSTANCE;
    }

    public List<Block> getTexturedBlocks()
    {
        return Collections.emptyList();
    }

    public List<IMateriallyTexturedBlock> getRegisteredBlocks()
    {
        return Collections.emptyList();
    }
}

