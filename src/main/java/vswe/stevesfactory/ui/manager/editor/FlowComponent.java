package vswe.stevesfactory.ui.manager.editor;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.actionmenu.ActionMenu;
import vswe.stevesfactory.library.gui.actionmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.box.Box;
import vswe.stevesfactory.library.gui.widget.button.AbstractIconButton;
import vswe.stevesfactory.ui.manager.editor.ControlFlowNodes.Node;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class FlowComponent<P extends IProcedure> extends AbstractContainer<IWidget> implements Comparable<IZIndexProvider>, IZIndexProvider {

    public enum State {
        COLLAPSED(TextureWrapper.ofFlowComponent(0, 0, 64, 20),
                TextureWrapper.ofFlowComponent(0, 20, 9, 10),
                TextureWrapper.ofFlowComponent(0, 30, 9, 10),
                54, 5,
                43, 6,
                45, 3,
                45, 11) {
            @Override
            public void changeState(FlowComponent flowComponent) {
                flowComponent.expand();
            }
        },
        EXPANDED(TextureWrapper.ofFlowComponent(64, 0, 124, 152),
                TextureWrapper.ofFlowComponent(9, 20, 9, 10),
                TextureWrapper.ofFlowComponent(9, 30, 9, 10),
                114, 5,
                103, 6,
                105, 3,
                105, 11) {
            @Override
            public void changeState(FlowComponent flowComponent) {
                flowComponent.collapse();
            }
        };

        public final TextureWrapper background;
        public final TextureWrapper toggleStateNormal;
        public final TextureWrapper toggleStateHovered;

        public final int toggleStateButtonX;
        public final int toggleStateButtonY;
        public final int renameButtonX;
        public final int renameButtonY;
        public final int submitButtonX;
        public final int submitButtonY;
        public final int cancelButtonX;
        public final int cancelButtonY;

        public final Dimension dimensions;

        State(TextureWrapper background, TextureWrapper toggleStateNormal, TextureWrapper toggleStateHovered, int toggleStateButtonX, int toggleStateButtonY, int renameButtonX, int renameButtonY, int submitButtonX, int submitButtonY, int cancelButtonX, int cancelButtonY) {
            this.background = background;
            this.toggleStateNormal = toggleStateNormal;
            this.toggleStateHovered = toggleStateHovered;
            this.toggleStateButtonX = toggleStateButtonX;
            this.toggleStateButtonY = toggleStateButtonY;
            this.renameButtonX = renameButtonX;
            this.renameButtonY = renameButtonY;
            this.submitButtonX = submitButtonX;
            this.submitButtonY = submitButtonY;
            this.cancelButtonX = cancelButtonX;
            this.cancelButtonY = cancelButtonY;

            this.dimensions = new Dimension(componentWidth(), componentHeight());
        }

        public int componentWidth() {
            return background.getPortionWidth();
        }

        public int componentHeight() {
            return background.getPortionHeight();
        }

        public abstract void changeState(FlowComponent flowComponent);
    }

    public static class ToggleStateButton extends AbstractIconButton {

        public ToggleStateButton(FlowComponent parent) {
            super(-1, -1, 9, 10);
            setParentWidget(parent);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return getParentWidget().getState().toggleStateNormal;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return getParentWidget().getState().toggleStateHovered;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                getParentWidget().toggleState();
                return true;
            }
            return false;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }

        public void updateTo(State state) {
            setLocation(state.toggleStateButtonX, state.toggleStateButtonY);
        }
    }

    public static class RenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(32, 188, 9, 9);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(41, 188, 9, 9);

        public RenameButton(FlowComponent parent) {
            super(-1, -1, 9, 9);
            setParentWidget(parent);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.submitButton.setEnabled(true);
                parent.cancelButton.setEnabled(true);
                parent.nameBox.setEditable(true);
                getWindow().setFocusedWidget(parent.nameBox);
                return true;
            }
            return false;
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }

        public void updateTo(State state) {
            setLocation(state.renameButtonX, state.renameButtonY);
        }
    }

    public static class SubmitButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(32, 197, 7, 7);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(39, 197, 7, 7);

        public SubmitButton(FlowComponent parent) {
            super(-1, -1, 7, 7);
            setParentWidget(parent);
            setEnabled(false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.cancelButton.setEnabled(false);
                parent.renameButton.setEnabled(true);
                parent.nameBox.scrollToFront();
                parent.nameBox.setEditable(false);
                getWindow().changeFocus(parent.nameBox, false);
                return true;
            }
            return false;
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }

        public void updateTo(State state) {
            setLocation(state.submitButtonX, state.submitButtonY);
        }
    }

    public static class CancelButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(32, 204, 7, 7);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(39, 204, 7, 7);

        private String previousName;

        public CancelButton(FlowComponent parent) {
            super(-1, -1, 7, 7);
            setParentWidget(parent);
            setEnabled(false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.submitButton.setEnabled(false);
                parent.renameButton.setEnabled(true);
                parent.setName(previousName);
                parent.nameBox.scrollToFront();
                parent.nameBox.setEditable(false);
                getWindow().changeFocus(parent.nameBox, false);
                previousName = "";
                return true;
            }
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                this.previousName = getParentWidget().getName();
            }
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }

        public void updateTo(State state) {
            setLocation(state.cancelButtonX, state.cancelButtonY);
        }
    }

    private static final ResourceLocation DELETE_ICON = RenderingHelper.linkTexture("gui/actions/delete.png");
    private static final ResourceLocation COPY_ICON = RenderingHelper.linkTexture("gui/actions/copy.png");
    private static final ResourceLocation CUT_ICON = RenderingHelper.linkTexture("gui/actions/cut.png");
    private static final ResourceLocation PASTE_ICON = RenderingHelper.linkTexture("gui/actions/paste.png");

    private P procedure;

    private int id;

    private final ToggleStateButton toggleStateButton;
    private final RenameButton renameButton;
    private final SubmitButton submitButton;
    private final CancelButton cancelButton;
    private final TextField nameBox;
    private final ControlFlowNodes inputNodes;
    private final ControlFlowNodes outputNodes;
    private final Box<Menu<P>> menus;
    // A list that refers to all the widgets above
    private final List<IWidget> children;

    private State state;
    private ActionMenu openedActionMenu;
    private int zIndex;

    // Temporary data
    private int initialDragLocalX;
    private int initialDragLocalY;

    public FlowComponent(P procedure, int amountInputsNodes, int amountOutputNodes) {
        super(0, 0, 0, 0);
        this.procedure = procedure;
        this.toggleStateButton = new ToggleStateButton(this);
        this.renameButton = new RenameButton(this);
        this.submitButton = new SubmitButton(this);
        this.cancelButton = new CancelButton(this);
        // The cursor looks a bit to short (and cute) with these numbers, might want change them?
        this.nameBox = new TextField(8, 8, 35, 10)
                .setBackgroundStyle(TextField.BackgroundStyle.NONE)
                .setEditable(false);
        this.inputNodes = ControlFlowNodes.inputNodes(amountInputsNodes);
        this.outputNodes = ControlFlowNodes.outputNodes(amountOutputNodes);
        this.menus = new Box<>(2, 20, 120, 130);
        this.menus.setLayout(m -> {
            if (menus.isEnabled()) {
                FlowLayout.reflow(m);
            }
        });
        this.children = ImmutableList.of(toggleStateButton, renameButton, submitButton, cancelButton, nameBox, inputNodes, outputNodes, menus);

        this.collapse();
        this.reflow();
    }

    @Override
    public Dimension getDimensions() {
        return state.dimensions;
    }

    public TextureWrapper getBackgroundTexture() {
        return state.background;
    }

    public State getState() {
        return state;
    }

    public void toggleState() {
        state.changeState(this);
    }

    public void expand() {
        state = State.EXPANDED;

        nameBox.setWidth(95);
        menus.setEnabled(true);
        reflow();
    }

    public void collapse() {
        state = State.COLLAPSED;

        nameBox.setWidth(35);
        menus.setEnabled(false);
        reflow();
    }

    public String getName() {
        return nameBox.getText();
    }

    public void setName(String name) {
        this.nameBox.setText(name);
        this.cancelButton.previousName = getName();
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    public Box<Menu<P>> getMenusBox() {
        return menus;
    }

    @Override
    public void reflow() {
        toggleStateButton.updateTo(state);
        renameButton.updateTo(state);
        submitButton.updateTo(state);
        cancelButton.updateTo(state);
        inputNodes.setWidth(state.dimensions.width);
        inputNodes.setY(-Node.HEIGHT);
        inputNodes.reflow();
        outputNodes.setWidth(state.dimensions.width);
        outputNodes.setY(getHeight());
        outputNodes.reflow();
        nameBox.scrollToFront();
        menus.reflow();
    }

    public FlowComponent<P> addMenu(Menu<P> menu) {
        menus.addChildren(menu);
        menu.onLinkFlowComponent(this);
        return this;
    }

    @Override
    public FlowComponent<P> addChildren(IWidget widget) {
        if (widget instanceof Menu) {
            @SuppressWarnings("unchecked") Menu<P> menu = (Menu<P>) widget;
            return addMenu(menu);
        } else {
            throw new IllegalArgumentException("Flow components do not accept new child widgets with type other than Menu");
        }
    }

    @Override
    public FlowComponent<P> addChildren(Collection<IWidget> widgets) {
        for (IWidget widget : widgets) {
            addChildren(widget);
        }
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        getBackgroundTexture().draw(getAbsoluteX(), getAbsoluteY());

        // Renaming state (showing different buttons at different times) is handled inside the widgets' render method
        toggleStateButton.render(mouseX, mouseY, particleTicks);
        renameButton.render(mouseX, mouseY, particleTicks);
        submitButton.render(mouseX, mouseY, particleTicks);
        cancelButton.render(mouseX, mouseY, particleTicks);
        nameBox.render(mouseX, mouseY, particleTicks);
        inputNodes.render(mouseX, mouseY, particleTicks);
        outputNodes.render(mouseX, mouseY, particleTicks);
        menus.render(mouseX, mouseY, particleTicks);

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            clearDrag();
            return true;
        }
        if (!isInside(mouseX, mouseY)) {
            clearDrag();
            return false;
        }

        initialDragLocalX = (int) mouseX - getAbsoluteX();
        initialDragLocalY = (int) mouseY - getAbsoluteY();
        getWindow().setFocusedWidget(this);

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            openActionMenu(mouseX, mouseY);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (submitButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (isFocused() && isDragging()) {
            EditorPanel parent = getParentWidget();
            setLocation((int) mouseX - parent.getAbsoluteX() - initialDragLocalX, (int) mouseY - parent.getAbsoluteY() - initialDragLocalY);
            return true;
        }
        return false;
    }

    private void clearDrag() {
        initialDragLocalX = -1;
        initialDragLocalY = -1;
    }

    private boolean isDragging() {
        return initialDragLocalX != -1 && initialDragLocalY != -1;
    }

    public void setParentWidget(EditorPanel parent) {
        this.setParentWidget((IWidget) parent);
        id = parent.nextID();
    }

    private void openActionMenu(double mouseX, double mouseY) {
        openedActionMenu = ActionMenu.atCursor(mouseX, mouseY, ImmutableList.of(
                new CallbackEntry(DELETE_ICON, "gui.sfm.ActionMenu.General.Delete", button -> {
                    removeSelf();
                    // Delay this to avoid ConcurrentModificationException
                    WidgetScreen.getCurrentScreen().deferRemovePopupWindow(openedActionMenu);
                }),
                // TODO implement these
                new CallbackEntry(CUT_ICON, "gui.sfm.ActionMenu.General.Cut", button -> {
                }),
                new CallbackEntry(COPY_ICON, "gui.sfm.ActionMenu.General.Copy", button -> {
                }),
                new CallbackEntry(PASTE_ICON, "gui.sfm.ActionMenu.General.Paste", button -> {
                })
        ));
        WidgetScreen.getCurrentScreen().addPopupWindow(openedActionMenu);
    }

    public void removeSelf() {
        inputNodes.removeAllConnections();
        outputNodes.removeAllConnections();

        getParentWidget().removeFlowComponent(this);
    }

    public ControlFlowNodes getInputNodes() {
        return inputNodes;
    }

    public ControlFlowNodes getOutputNodes() {
        return outputNodes;
    }

    @Nonnull
    @Override
    public EditorPanel getParentWidget() {
        return Objects.requireNonNull((EditorPanel) super.getParentWidget());
    }

    public int getID() {
        return id;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void setZIndex(int z) {
        this.zIndex = z;
    }

    @Override
    public int compareTo(IZIndexProvider that) {
        return this.getZIndex() - that.getZIndex();
    }

    public P getLinkedProcedure() {
        return procedure;
    }

    public FlowComponent<P> setLinkedProcedure(P procedure) {
        this.procedure = procedure;
        return this;
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("Z=" + this.getZIndex());
    }
}