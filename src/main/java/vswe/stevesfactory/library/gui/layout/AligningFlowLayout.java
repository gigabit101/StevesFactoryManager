package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

public class AligningFlowLayout extends FlowLayout {

    public enum Alignment {
        LEFT {
            @Override
            public void alignTo(RelocatableWidgetMixin widget, int x) {
                widget.setX(x);
            }
        },
        RIGHT {
            @Override
            public void alignTo(RelocatableWidgetMixin widget, int x) {
                int leftX = x - widget.getWidth();
                if (leftX >= 0) {
                    widget.setX(leftX);
                } else {
                    widget.setX(0);
                }
            }
        };

        public abstract void alignTo(RelocatableWidgetMixin widget, int x);
    }

    public Alignment alignment;
    public int alignmentX;

    public AligningFlowLayout(Alignment alignment, int alignmentX) {
        this.alignment = alignment;
        this.alignmentX = alignmentX;
    }

    @Override
    public <T extends IWidget & RelocatableWidgetMixin> void adjustPosition(T widget, int y) {
        super.adjustPosition(widget, y);
        alignment.alignTo(widget, alignmentX);
    }
}
