package org.apache.logging.log4j.util;

/**
 * [1.7.10] TriConsumer not available in older log4j bundled with 1.7.10.
 * This shim provides the same functional interface contract.
 */
@FunctionalInterface
public interface TriConsumer<A, B, C>
{
    void accept(A a, B b, C c);
}

