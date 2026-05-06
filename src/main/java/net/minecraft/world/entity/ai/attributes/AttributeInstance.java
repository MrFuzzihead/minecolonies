package net.minecraft.world.entity.ai.attributes;

import java.util.ArrayList;
import java.util.Collection;

/** [1.7.10 stub] AttributeInstance - maps to IAttributeInstance */
public class AttributeInstance
{
    private double value;
    private final java.util.List<AttributeModifier> modifiers = new ArrayList<>();

    public AttributeInstance(double baseValue) { this.value = baseValue; }

    public double getValue() { return value; }
    public double getBaseValue() { return value; }

    public void setBaseValue(double value) { this.value = value; }

    public Collection<AttributeModifier> getModifiers() { return modifiers; }

    public void addTransientModifier(AttributeModifier modifier) { modifiers.add(modifier); }

    public void addPermanentModifier(AttributeModifier modifier) { modifiers.add(modifier); }

    public void removeModifier(AttributeModifier modifier) { modifiers.remove(modifier); }
}
