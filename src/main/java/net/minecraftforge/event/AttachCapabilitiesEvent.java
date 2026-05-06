package net.minecraftforge.event;

/** [1.7.10 bridge] AttachCapabilitiesEvent - no 1.7.10 equivalent */
public class AttachCapabilitiesEvent<T>
{
    private final T object;
    public AttachCapabilitiesEvent(T obj) { this.object = obj; }
    public T getObject() { return object; }
}

