package net.minecraft.network.chat;

/** [1.7.10 stub] TextColor. */
public class TextColor
{
    private final int rgb;
    private TextColor(int rgb) { this.rgb = rgb; }
    public int getValue() { return rgb; }
    public static TextColor fromRgb(int rgb) { return new TextColor(rgb); }
    public static TextColor fromLegacyFormat(net.minecraft.ChatFormatting format) { return new TextColor(0); }
    public static TextColor fromLegacyFormat(net.minecraft.util.EnumChatFormatting format) { return new TextColor(0); }
}


