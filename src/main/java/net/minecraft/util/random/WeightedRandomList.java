package net.minecraft.util.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/** [1.7.10 stub] WeightedRandomList */
public class WeightedRandomList<T>
{
    private final List<T> items = new ArrayList<>();

    public static <T> WeightedRandomList<T> create() { return new WeightedRandomList<>(); }

    public void add(T item) { items.add(item); }

    public Optional<T> getRandom(Object random)
    {
        if (items.isEmpty()) return Optional.empty();
        return Optional.of(items.get(new Random().nextInt(items.size())));
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public int size() { return items.size(); }
}

