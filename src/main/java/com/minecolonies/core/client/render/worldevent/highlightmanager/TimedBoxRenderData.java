package com.minecolonies.core.client.render.worldevent.highlightmanager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.ldtteam.structurize.client.rendertask.util.WorldRenderMacros;
import com.minecolonies.core.client.render.worldevent.ColonyWorldRenderMacros;
import com.minecolonies.core.client.render.worldevent.WorldEventContext;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] world.phys removed

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Highlight render data for marking blocks in the world with potential warnings on them.
 */
public class TimedBoxRenderData implements IHighlightRenderData
{
    /**
     * List of texts to display.
     */
    private final List<String> text = new ArrayList<>();

    /**
     * Position where to render the box.
     */
    private final int[] pos;

    /**
     * How long the box should stay.
     */
    private Duration duration;

    /**
     * The colour at which the box should render.
     */
    private int argbColor = 0xffffffff;

    /**
     * Default constructor.
     */
    public TimedBoxRenderData(final int[] pos)
    {
        this.pos = pos;
    }

    @Override
    public void render(final WorldEventContext context)
    {
        final MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        ColonyWorldRenderMacros.renderLineBox(context.poseStack, buffer, new AABB(pos), 0.025f, argbColor, true);
        if (!text.isEmpty())
        {
            WorldRenderMacros.renderDebugText(pos, text, context.poseStack, true, 3, buffer);
        }
        ColonyWorldRenderMacros.endRenderLineBox(buffer);
        buffer.endBatch();
    }

    @Override
    public Duration getDuration()
    {
        return duration;
    }

    /**
     * Duration of the box.
     */
    public TimedBoxRenderData setDuration(final Duration duration)
    {
        this.duration = duration;
        return this;
    }

    /**
     * List of strings to display
     */
    public TimedBoxRenderData addText(final String text)
    {
        this.text.add(text);
        return this;
    }

    /**
     * Color code for the box, argb format
     */
    public TimedBoxRenderData setColor(final int argbColor)
    {
        this.argbColor = argbColor;
        return this;
    }
}




