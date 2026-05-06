package net.minecraftforge.resource;
import net.minecraft.server.packs.PackResources;
/** [1.7.10 bridge] PathPackResources */
public class PathPackResources implements PackResources {
    public PathPackResources(String name, boolean b, java.nio.file.Path path) {}
    @Override public String packId() { return ""; }
    @Override public boolean isBuiltin() { return false; }
    @Override public void close() {}
}
