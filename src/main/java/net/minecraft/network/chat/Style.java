package net.minecraft.network.chat;
import net.minecraft.network.chat.Style;

/** [1.7.10 stub] Style - text formatting style */
public class Style
{
    public static final Style EMPTY = new Style();

    public Style withColor(int color) { return this; }
    public Style withColor(net.minecraft.util.EnumChatFormatting format) { return this; }
    public Style withColor(TextColor color) { return this; }
    public Style withBold(boolean bold) { return this; }
    public Style withItalic(boolean italic) { return this; }
    public Style withUnderlined(boolean underlined) { return this; }
    public Style withStrikethrough(boolean strikethrough) { return this; }
    public Style withObfuscated(boolean obfuscated) { return this; }
    public Style withClickEvent(Object event) { return this; }
    public Style withHoverEvent(Object event) { return this; }
    public Style applyTo(Style parent) { return this; }
    public Style applyLegacyFormat(net.minecraft.ChatFormatting format) { return this; }
}

