package com.minecolonies.apiimp.initializer;

/**
 * [1.7.10 BACKPORT] ModContainerInitializers stubbed.
 * Container/MenuType registration uses 1.21 Forge APIs not available in 1.7.10.
 * Containers are registered via the 1.7.10 GuiHandler system instead.
 */
public class ModContainerInitializers
{
    private ModContainerInitializers() {}

    /** No-op stub — containers registered elsewhere in 1.7.10. */
    public static void init() {}
}
