package net.minecraft.nbt;

/**
 * [1.7.10] Compatibility stub for 1.21 NbtUtils.
 */
public class NbtUtils
{
    public static int[] readBlockPos(NBTTagCompound tag)
    {
        return new int[]{tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z")};
    }

    public static NBTTagCompound writeBlockPos(int[] pos)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos[0]);
        tag.setInteger("Y", pos[1]);
        tag.setInteger("Z", pos[2]);
        return tag;
    }
}

