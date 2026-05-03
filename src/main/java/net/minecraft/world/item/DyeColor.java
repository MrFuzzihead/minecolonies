package net.minecraft.world.item;

/**
 * [1.7.10] Compatibility shim for 1.21 DyeColor.
 * In 1.7.10, dye colors are represented by net.minecraft.item.EnumDyeColor.
 */
public enum DyeColor
{
    WHITE(0), ORANGE(1), MAGENTA(2), LIGHT_BLUE(3), YELLOW(4), LIME(5), PINK(6), GRAY(7),
    LIGHT_GRAY(8), CYAN(9), PURPLE(10), BLUE(11), BROWN(12), GREEN(13), RED(14), BLACK(15);

    private final int id;

    DyeColor(final int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name().toLowerCase();
    }

    public int getColorValue()
    {
        return id;
    }

    public int getTextColor()
    {
        // approximate text color for each dye
        return id * 0x111111;
    }
}


