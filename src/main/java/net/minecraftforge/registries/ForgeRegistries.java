package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.ArrayList;

/**
 * [1.7.10] Compatibility shim for 1.21's ForgeRegistries.
 * Wraps 1.7.10 registry APIs into the 1.21-style interface.
 */
public class ForgeRegistries
{
    public static final Registry<Item> ITEMS = new Registry<Item>()
    {
        @Override
        public ResourceLocation getKey(Item item)
        {
            Object name = Item.itemRegistry.getNameForObject(item);
            return name != null ? new ResourceLocation(name.toString()) : null;
        }

        @Override
        public Item getValue(ResourceLocation key)
        {
            return (Item) Item.itemRegistry.getObject(key);
        }

        @Override
        public Collection<Item> getValues()
        {
            // Not commonly used for items, return empty
            return new ArrayList<>();
        }
    };

    public static final Registry<Block> BLOCKS = new Registry<Block>()
    {
        @Override
        public ResourceLocation getKey(Block block)
        {
            Object name = Block.blockRegistry.getNameForObject(block);
            return name != null ? new ResourceLocation(name.toString()) : null;
        }

        @Override
        public Block getValue(ResourceLocation key)
        {
            return (Block) Block.blockRegistry.getObject(key);
        }

        @Override
        public Collection<Block> getValues()
        {
            Collection<Block> values = new ArrayList<>();
            for (Object key : Block.blockRegistry.getKeys())
            {
                Block b = (Block) Block.blockRegistry.getObject(key);
                if (b != null)
                {
                    values.add(b);
                }
            }
            return values;
        }
    };

    /**
     * [1.7.10] Entity types registry not available in 1.7.10.
     * Returns a stub that always returns null.
     */
    public static final Registry<Object> ENTITY_TYPES = new Registry<Object>()
    {
        @Override
        public ResourceLocation getKey(Object entity)
        {
            return null;
        }

        @Override
        public Object getValue(ResourceLocation key)
        {
            return null;
        }

        @Override
        public Collection<Object> getValues()
        {
            return new ArrayList<>();
        }
    };

    /**
     * Simple registry interface bridging 1.21 API to 1.7.10 registries.
     */
    public interface Registry<T>
    {
        ResourceLocation getKey(T value);

        T getValue(ResourceLocation key);

        Collection<T> getValues();

        default boolean containsKey(ResourceLocation key)
        {
            return getValue(key) != null;
        }

        default boolean containsValue(T value)
        {
            return getKey(value) != null;
        }
    }
}

