package net.minecraft.world;

/**
 * [1.7.10] Compatibility stub for 1.21 InteractionResultHolder<T>.
 */
public class InteractionResultHolder<T>
{
    private final net.minecraft.world.item.InteractionResult result;
    private final T object;

    public InteractionResultHolder(final net.minecraft.world.item.InteractionResult result, final T object)
    {
        this.result = result;
        this.object = object;
    }

    public net.minecraft.world.item.InteractionResult getResult() { return result; }
    public T getObject() { return object; }

    public static <T> InteractionResultHolder<T> success(T object)
    {
        return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.SUCCESS, object);
    }

    public static <T> InteractionResultHolder<T> consume(T object)
    {
        return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.CONSUME, object);
    }

    public static <T> InteractionResultHolder<T> pass(T object)
    {
        return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.PASS, object);
    }

    public static <T> InteractionResultHolder<T> fail(T object)
    {
        return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.FAIL, object);
    }
}

