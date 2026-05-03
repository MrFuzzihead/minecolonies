package net.minecraft.util;
/** [1.7.10 stub] FormattedCharSequence */
public interface FormattedCharSequence { boolean accept(int index, Object style, int codePoint); static FormattedCharSequence EMPTY = (i, s, c) -> true; }