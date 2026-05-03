package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

/**
 * [1.7.10] Compatibility stub for 1.21 HumanoidModel.
 */
public class HumanoidModel<T>
{
    /** [1.7.10] ArmPose inner enum from 1.21 HumanoidModel */
    public enum ArmPose { EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_SPEAR, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH }
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public ArmPose leftArmPose = ArmPose.EMPTY;
    public boolean riding;
    public boolean young;
    /** [1.7.10] hat == bipedHeadwear overlay layer */
    public ModelPart hat = new ModelPart();

    public static MeshDefinition createMesh(Object cubeDeformation, float offset)
    {
        return new MeshDefinition();
    }

    public HumanoidModel(ModelPart root) {}
}

