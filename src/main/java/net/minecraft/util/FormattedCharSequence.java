package net.minecraft.util;
import net.minecraft.network.chat.Style;
/** [1.7.10 stub] FormattedCharSequence */
public interface FormattedCharSequence { boolean accept(int index, Object style, int codePoint); static FormattedCharSequence EMPTY = (i, s, c) -> true; }