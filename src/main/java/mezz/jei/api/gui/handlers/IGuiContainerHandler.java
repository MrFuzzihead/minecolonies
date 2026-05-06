package mezz.jei.api.gui.handlers;

import java.util.Collection;

/** [1.7.10 stub] IGuiContainerHandler. */
public interface IGuiContainerHandler<G>
{
    default java.util.List<Object> getGuiExtraAreas(G screen) { return java.util.Collections.emptyList(); }
    default Collection<IGuiClickableArea> getGuiClickableAreas(G screen, double mouseX, double mouseY) { return java.util.Collections.emptyList(); }
}
