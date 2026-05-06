package net.minecraft.client;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.client.gui.GuiScreen;
/** [1.7.10] Stub for Minecraft singleton - shadowing real class, providing both 1.7.10 and 1.21 APIs */
public class Minecraft {
    public static Minecraft instance;
    public static Minecraft getMinecraft() { return instance; }
    public static Minecraft getInstance() { return instance; }
    public EntityPlayer thePlayer;
    public EntityPlayer player;
    public World theWorld;
    public World level;
    public GuiScreen currentScreen;
    public GuiScreen screen;
    public net.minecraft.client.renderer.EntityRenderer entityRenderer;
    public net.minecraft.client.multiplayer.WorldClient theWorldClient;
    public net.minecraft.client.settings.GameSettings gameSettings;
    public net.minecraft.client.renderer.RenderGlobal renderGlobal;
    public net.minecraft.client.audio.SoundHandler getSoundHandler() { return null; }
    public net.minecraft.client.renderer.texture.TextureManager getTextureManager() { return null; }
    public net.minecraft.client.gui.FontRenderer fontRendererObj;
}
