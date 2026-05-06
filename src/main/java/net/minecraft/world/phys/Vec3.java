package net.minecraft.world.phys;
/** [1.7.10] Stub for Vec3 */
public class Vec3 {
    public final double x, y, z;
    public Vec3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    public static Vec3 atCenterOf(Object pos) { return new Vec3(0, 0, 0); }
    public Vec3 add(double x, double y, double z) { return new Vec3(this.x+x, this.y+y, this.z+z); }
    public Vec3 scale(double s) { return new Vec3(x*s, y*s, z*s); }
}