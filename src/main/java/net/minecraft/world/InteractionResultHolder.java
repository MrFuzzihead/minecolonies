package net.minecraft.world;
/** [1.7.10] Stub for 1.21 InteractionResultHolder */
public class InteractionResultHolder<T> {
    private final net.minecraft.world.item.InteractionResult result;
    private final T object;
    public InteractionResultHolder(net.minecraft.world.item.InteractionResult result, T object) {
        this.result = result; this.object = object;
    }
    public net.minecraft.world.item.InteractionResult getResult() { return result; }
    public T getObject() { return object; }
    public static <T> InteractionResultHolder<T> success(T obj) { return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.SUCCESS, obj); }
    public static <T> InteractionResultHolder<T> fail(T obj) { return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.FAIL, obj); }
    public static <T> InteractionResultHolder<T> pass(T obj) { return new InteractionResultHolder<>(net.minecraft.world.item.InteractionResult.PASS, obj); }
}