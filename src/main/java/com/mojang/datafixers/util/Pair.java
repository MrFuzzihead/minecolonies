package com.mojang.datafixers.util;

/**
 * [1.7.10] Compatibility stub for Datafixers Pair<A,B>.
 */
public class Pair<A, B>
{
    private final A first;
    private final B second;

    public Pair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }

    public static <A, B> Pair<A, B> of(A first, B second)
    {
        return new Pair<>(first, second);
    }
}

