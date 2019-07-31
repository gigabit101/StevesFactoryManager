package vswe.stevesfactory.ui.manager.selection;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI.TopLevelWidget;
import vswe.stevesfactory.ui.manager.components.DynamicWidthWidget;

import javax.annotation.Nonnull;
import java.util.*;

import static vswe.stevesfactory.ui.manager.FactoryManagerGUI.DOWN_RIGHT_4_STRICT_TABLE;

public final class SelectionPanel extends DynamicWidthWidget<ComponentSelectionButton> implements ContainerWidgetMixin<ComponentSelectionButton> {

    private final ImmutableList<ComponentSelectionButton> staticIcons;
    private final List<ComponentSelectionButton> addendumIcons;
    private final List<ComponentSelectionButton> icons;

    public SelectionPanel(TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MIN_WIDTH);

        this.staticIcons = createStaticIcons();
        this.addendumIcons = new ArrayList<>();
        this.icons = CompositeUnmodifiableList.of(staticIcons, addendumIcons);

        // Reset position of the components using a table DOWN_RIGHT_4_STRICT_TABLE
        reflow();
    }

    @SuppressWarnings("unchecked")
    private ImmutableList<ComponentSelectionButton> createStaticIcons() {
        ImmutableList.Builder<ComponentSelectionButton> icons = ImmutableList.builder();
        for (IProcedureType<?> factory : SFMAPI.getProceduresRegistry().getValues()) {
            icons.add(new ComponentSelectionButton(this, (IProcedureType<IProcedure>) factory));
        }
        return icons.build();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        for (IWidget icon : staticIcons) {
            icon.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public List<ComponentSelectionButton> getChildren() {
        return icons;
    }

    @Override
    public IContainer<ComponentSelectionButton> addChildren(ComponentSelectionButton widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<ComponentSelectionButton> addChildren(Collection<ComponentSelectionButton> widgets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reflow() {
        int w = getWidth();
        setWidth(Integer.MAX_VALUE);
        DOWN_RIGHT_4_STRICT_TABLE.reflow(getDimensions(), getChildren());
        setWidth(w);
    }

    @Nonnull
    @Override
    public TopLevelWidget getParentWidget() {
        return Objects.requireNonNull((TopLevelWidget) super.getParentWidget());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isInside(mouseX, mouseY)) {
            getWindow().setFocusedWidget(this);
            return true;
        }
        return false;
    }
}
