package com.minecolonies.api.client.render.modeltype;

import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesMonster;
import net.minecraft.client.model.ModelBiped;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Amazon model (1.7.10 ModelBiped port).
 */
@SideOnly(Side.CLIENT)
public class AmazonModel<T extends AbstractEntityMinecoloniesMonster> extends ModelBiped
{
    public AmazonModel()
    {
        super();
    }

    @Override
    public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, net.minecraft.entity.Entity entity)
    {
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
        bipedHead.rotationPointY -= 3;
        bipedRightLeg.rotationPointY -= 3.5f;
        bipedLeftLeg.rotationPointY -= 3.5f;
        bipedRightArm.rotationPointY -= 2;
        bipedLeftArm.rotationPointY -= 2;
    }
}
