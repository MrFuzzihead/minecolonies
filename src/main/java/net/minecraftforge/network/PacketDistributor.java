package net.minecraftforge.network;

/**
 * [1.7.10] Stub for 1.21 PacketDistributor.
 * The actual network send operations are performed via NetworkChannel (SimpleNetworkWrapper).
 */
public class PacketDistributor
{
    /**
     * [1.7.10] TargetPoint replaces the 1.21 server-side area targeting.
     * Used only as a data holder; actual sending is done via NetworkChannel.sendToAllAround().
     */
    public static class TargetPoint
    {
        public final double x;
        public final double y;
        public final double z;
        public final double range;
        public final int    dimension;

        public TargetPoint(final double x, final double y, final double z, final double range, final int dimension)
        {
            this.x         = x;
            this.y         = y;
            this.z         = z;
            this.range     = range;
            this.dimension = dimension;
        }
    }
}

