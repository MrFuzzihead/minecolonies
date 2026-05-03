package net.minecraft.util;

/**
 * [1.7.10] Shim for 1.21 ClickEvent.
 * In 1.7.10, click events are on ChatStyle.
 */
public class ClickEvent
{
    public enum Action { OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD }

    private final Action action;
    private final String value;

    public ClickEvent(final Action action, final String value)
    {
        this.action = action;
        this.value  = value;
    }

    public Action getAction() { return action; }
    public String getValue()  { return value; }
}
