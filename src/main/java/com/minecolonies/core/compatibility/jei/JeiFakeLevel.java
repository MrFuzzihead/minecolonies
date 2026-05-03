package com.minecolonies.core.compatibility.jei;

import com.ldtteam.structurize.client.fakelevel.SingleBlockFakeLevel;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@OnlyIn(Dist.CLIENT)
public class JeiFakeLevel extends SingleBlockFakeLevel
{
    public JeiFakeLevel()
    {
        super(null);
        prepare(Blocks.AIR.defaultBlockState(), null, null);
    }

    @Override
    public World realLevel()
    {
        return Minecraft.getInstance().World;
    }
}



