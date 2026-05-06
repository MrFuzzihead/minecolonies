package com.minecolonies.core.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Collection;
import java.util.UUID;

/**
 * Utility class for handling add/removal of attribute modifiers.
 * [1.7.10] Uses 1.7.10 SharedMonsterAttributes and IAttributeInstance API.
 */
public abstract class AttributeModifierUtils
{
    /**
     * Remove all healthmodifiers from a citizen
     *
     * @param entity the entity to remove the modifiers from
     */
    public static void removeAllHealthModifiers(final EntityLivingBase entity)
    {
        if (entity == null) return;
        IAttributeInstance inst = entity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (inst == null) return;
        for (final AttributeModifier mod : (Collection<AttributeModifier>) inst.func_111122_c())
        {
            inst.removeModifier(mod);
        }
        if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());
    }

    /**
     * Removes healthmodifier by name. Reduces HP if needed
     *
     * @param entity       the entity to remove the modifier from
     * @param modifierName Name of the modifier to remove, see e.g. GUARD_HEALTH_MOD_LEVEL_NAME
     */
    public static void removeHealthModifier(final EntityLivingBase entity, final String modifierName)
    {
        if (entity == null) return;
        IAttributeInstance inst = entity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (inst == null) return;
        for (final AttributeModifier mod : (Collection<AttributeModifier>) inst.func_111122_c())
        {
            if (modifierName.equals(mod.getName())) inst.removeModifier(mod);
        }
        if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());
    }

    /**
     * Adds a health modifier, overwriting old modifier with the same name. Keeps health percentage.
     *
     * @param entity   entity to add a healthmodifier to
     * @param modifier the modifier to add.
     */
    public static void addHealthModifier(final EntityLivingBase entity, final net.minecraft.world.entity.ai.attributes.AttributeModifier modifier)
    {
        if (entity == null) return;
        final float prevHealthPct = entity.getHealth() / entity.getMaxHealth();
        removeHealthModifier(entity, modifier.getName());
        IAttributeInstance inst = entity.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (inst != null)
        {
            inst.applyModifier(new AttributeModifier(modifier.getName(), modifier.getAmount(), 0));
        }
        entity.setHealth(entity.getMaxHealth() * prevHealthPct);
    }

    /**
     * Remove a specific modifier from an entity.
     * @param entity the entity.
     * @param modifierName the name of the modifier.
     * @param attribute the type of attribute.
     */
    public static void removeModifier(final EntityLivingBase entity, final String modifierName, final Attribute attribute)
    {
        // [1.7.10] Attribute system stub - no-op; 1.7.10 uses SharedMonsterAttributes
    }

    /**
     * Add a specific new modifier.
     * @param entity the entity to add it to.
     * @param modifier the modifier to add.
     * @param attribute the type of the attribute.
     */
    public static void addModifier(final EntityLivingBase entity, final net.minecraft.world.entity.ai.attributes.AttributeModifier modifier, final Attribute attribute)
    {
        // [1.7.10] Attribute system stub - no-op; 1.7.10 uses SharedMonsterAttributes
    }
}
