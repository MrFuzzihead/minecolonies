package com.minecolonies.api.util;

/**
 * [1.7.10] TriPredicate functional interface.
 * Replacement for net.minecraftforge.common.util.TriPredicate not available in 1.7.10.
 *
 * @param <A> first argument type
 * @param <B> second argument type
 * @param <C> third argument type
 */
@FunctionalInterface
public interface TriPredicate<A, B, C>
{
    boolean test(A a, B b, C c);
}

