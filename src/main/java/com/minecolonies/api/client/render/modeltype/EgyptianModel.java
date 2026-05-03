package com.minecolonies.api.client.render.modeltype;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import net.minecraft.client.model.ModelBiped;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Egyptian model (1.7.10 ModelBiped port).
 */
@SideOnly(Side.CLIENT)
public class EgyptianModel<T extends AbstractEntityMinecoloniesMonster> extends ModelBiped
{
    public EgyptianModel() { super(); }
    public EgyptianModel(float modelSize) { super(modelSize); }
}
