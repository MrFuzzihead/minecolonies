package mezz.jei.api.registration;

/** [1.7.10 stub] IGuiHandlerRegistration. */
public interface IGuiHandlerRegistration
{
    <G> void addGuiContainerHandler(Class<G> guiClass, mezz.jei.api.gui.handlers.IGuiContainerHandler<G> handler);
    <G> void addGhostIngredientHandler(Class<G> guiClass, mezz.jei.api.gui.handlers.IGhostIngredientHandler<G> handler);
}
