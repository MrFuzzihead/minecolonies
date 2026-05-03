package com.minecolonies.api.client.render.modeltype;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import net.minecraft.client.model.ModelBiped;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Norsemen model (1.7.10 ModelBiped port).
 */
@SideOnly(Side.CLIENT)
public class NorsemenModel extends ModelBiped
{
    public NorsemenModel() { super(); }
    public NorsemenModel(float modelSize) { super(modelSize); }
}
