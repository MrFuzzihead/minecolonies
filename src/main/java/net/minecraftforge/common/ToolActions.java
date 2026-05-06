package net.minecraftforge.common;

/**
 * [1.7.10 stub] ToolActions — no equivalent in 1.7.10; tool capability system is 1.21 only.
 */
public class ToolActions
{
    public static final ToolAction DEFAULT_AXE_ACTIONS = new ToolAction("axe");
    public static final ToolAction DEFAULT_HOE_ACTIONS = new ToolAction("hoe");
    public static final ToolAction DEFAULT_SHOVEL_ACTIONS = new ToolAction("shovel");
    public static final ToolAction DEFAULT_PICKAXE_ACTIONS = new ToolAction("pickaxe");
    public static final ToolAction DEFAULT_SWORD_ACTIONS = new ToolAction("sword");

    public static class ToolAction
    {
        private final String name;
        public ToolAction(final String name) { this.name = name; }
        public String name() { return name; }
    }
}

