package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayList;
import java.util.List;

/** [1.7.10 stub] CustomizeGuiOverlayEvent - no 1.7.10 equivalent; used for F3 debug overlay */
public class CustomizeGuiOverlayEvent extends Event
{
    public static class DebugText extends CustomizeGuiOverlayEvent
    {
        private final List<String> left  = new ArrayList<>();
        private final List<String> right = new ArrayList<>();

        public List<String> getLeft()  { return left; }
        public List<String> getRight() { return right; }
    }
}

