package net.minecraft.world.entity;

/**
 * [1.7.10] Compatibility shim for 1.21 EntityType class.
 * In 1.7.10, entities are registered by class and string name.
 *
 * @param <T> the entity type.
 */
public class EntityType<T>
{
    private final String id;

    private EntityType(final String id)
    {
        this.id = id;
    }

    public String id()
    {
        return id;
    }

    public String toString()
    {
        return id;
    }
}

