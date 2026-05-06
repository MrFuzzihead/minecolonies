package net.minecraft.world.entity.ai.attributes;
import java.util.UUID;
/** [1.7.10] Stub for 1.21 AttributeModifier */
public class AttributeModifier {
    public enum Operation { ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL }
    private final String name;
    private final double amount;
    private final Operation operation;
    public AttributeModifier(String name, double amount, Operation operation) { this.name = name; this.amount = amount; this.operation = operation; }
    public AttributeModifier(UUID id, String name, double amount, Operation operation) { this.name = name; this.amount = amount; this.operation = operation; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public Operation getOperation() { return operation; }
}
