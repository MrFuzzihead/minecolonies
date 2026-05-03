package com.minecolonies.api.client.render.modeltype;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.geom.ModelPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Citizen model (1.7.10 ModelBiped port).
 */
@SideOnly(Side.CLIENT)
public class CitizenModel<T extends AbstractEntityCitizen> extends ModelBiped
{
    /**
     * Working render meta.
     */
    private static final String RENDER_META_WORKING = "working";

    public static boolean isItApril1st = false;

    public CitizenModel()
    {
        super();
    }

    public CitizenModel(float modelSize)
    {
        super(modelSize);
    }

    /**
     * [1.7.10] Compatibility: 1.21 models pass a ModelPart. We ignore it and use default super().
     */
    public CitizenModel(ModelPart part)
    {
        super();
    }

    @Override
    public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, net.minecraft.entity.Entity entity)
    {
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
        if (entity instanceof AbstractEntityCitizen citizen)
        {
            if (citizen.getCitizenDataView() != null && citizen.getCitizenDataView().getCustomTextureUUID() != null)
            {
                bipedHead.isHidden = true;
                bipedHeadwear.isHidden = true;
            }
            else
            {
                bipedHead.isHidden = false;
                bipedHeadwear.isHidden = false;
            }
        }
    }

    /**
     * Override to change body rotation.
     *
     * @return the rotation.
     */
    public float getActualRotation(@NotNull final AbstractEntityCitizen entity)
    {
        return 0;
    }

    /**
     * Check if the citizen is supposed to be working.
     * @param citizen the citizen entity to check.
     * @return true if so.
     */
    public boolean isWorking(final AbstractEntityCitizen citizen)
    {
        return citizen.getRenderMetadata().contains(RENDER_META_WORKING);
    }
}
