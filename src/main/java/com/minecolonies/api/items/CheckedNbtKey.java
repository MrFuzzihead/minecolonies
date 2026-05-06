package com.minecolonies.api.items;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * Representation of a nbt key to match two item stacks.
 */
public class CheckedNbtKey
{
    @NotNull
    public String key;

    @NotNull
    public Set<CheckedNbtKey> children;

    public CheckedNbtKey(@NotNull final String key, @NotNull final Set<CheckedNbtKey> children)
    {
        this.key = key;
        this.children = children;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final CheckedNbtKey keyObject = (CheckedNbtKey) o;
        return key.equals(keyObject.key) && children.equals(keyObject.children);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, children);
    }

    /**
     * Check if two nbt match according to this checked nbt keys rules.
     * @param nbt1 the first nbt.
     * @param nbt2 the second nbt.
     * @return true if they match.
     */
    public boolean matches(final NBTTagCompound nbt1, final NBTTagCompound nbt2)
    {
        final NBTBase tag1 = nbt1.getTag(key); // [1.7.10] get(String) -> getTag(String)
        final NBTBase tag2 = nbt2.getTag(key);

        if (tag1 == null || tag2 == null)
        {
            return (tag1 == null) == (tag2 == null);
        }
        else
        {
            if (children.isEmpty())
            {
                return tag1.equals(tag2);
            }

            if (tag1 instanceof NBTTagCompound && tag2 instanceof NBTTagCompound)
            {
                for (final CheckedNbtKey key : children)
                {
                    if (!key.matches((NBTTagCompound) tag1, (NBTTagCompound) tag2))
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}




