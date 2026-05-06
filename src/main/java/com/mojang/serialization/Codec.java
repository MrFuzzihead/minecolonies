package com.mojang.serialization;

/** [1.7.10 stub] Codec — DataFixerUpper codec. Not available in 1.7.10. */
public interface Codec<A>
{
    static <A> Codec<A> unit(A defaultValue) { return new Codec<A>(){}; }
    static <A> Codec<A> of(Encoder<A> encoder, Decoder<A> decoder) { return new Codec<A>(){}; }
}

