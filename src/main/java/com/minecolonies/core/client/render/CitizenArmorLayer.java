package com.minecolonies.core.client.render;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

/**
 * Armor layer renderer for citizens.
 * [1.7.10] STUBBED: HumanoidArmorLayer, PoseStack, MultiBufferSource, EquipmentSlot etc. don't exist in 1.7.10.
 * Armor rendering on citizens is handled via ModelBiped in 1.7.10 and is not layered.
 * TODO: Implement using 1.7.10 RenderBiped/ModelBiped layer approach if needed.
 */
@SideOnly(Side.CLIENT)
public class CitizenArmorLayer<T extends AbstractEntityCitizen>
{
    // [1.7.10] no-op stub; armor rendering handled elsewhere
}
