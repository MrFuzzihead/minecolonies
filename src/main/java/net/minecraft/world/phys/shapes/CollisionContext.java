package net.minecraft.world.phys.shapes;
/** [1.7.10] Stub for CollisionContext */
public interface CollisionContext {
    static CollisionContext empty() { return new CollisionContext() {}; }
    static CollisionContext of(Object entity) { return new CollisionContext() {}; }
}