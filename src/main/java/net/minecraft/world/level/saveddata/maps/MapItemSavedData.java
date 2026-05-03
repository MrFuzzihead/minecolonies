package net.minecraft.world.level.saveddata.maps;

/**
 * [1.7.10] Compatibility shim for 1.21 MapItemSavedData.
 * In 1.7.10, maps use MapData.
 */
public class MapItemSavedData
{
    public int scale;
    public int centerX;
    public int centerZ;
    public int dimension;
    public boolean trackingPosition;
    public boolean unlimitedTracking;
    public boolean locked;
    public byte[] colors = new byte[128 * 128];

    public MapItemSavedData() {}
}

