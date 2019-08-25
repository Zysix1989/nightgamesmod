package nightgames.gui.commandpanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class ViewportLayout extends javax.swing.ViewportLayout {
    static ViewportLayout SHARED_INSTANCE = new ViewportLayout();

    @Override
    public void layoutContainer(Container parent) {
        JViewport vp = (JViewport) parent;
        Component view = vp.getView();
        Scrollable scrollableView = null;
        if (view != null) {
            if (view instanceof Scrollable) {
                scrollableView = (Scrollable) view;
            }

            Dimension viewPrefSize = view.getPreferredSize();
            Dimension vpSize = vp.getSize();
            Dimension extentSize = vp.toViewCoordinates(vpSize);
            Dimension viewSize = new Dimension(viewPrefSize);
            if (scrollableView != null) {
                if (scrollableView.getScrollableTracksViewportWidth()) {
                    viewSize.width = vpSize.width;
                }

                if (scrollableView.getScrollableTracksViewportHeight()) {
                    viewSize.height = vpSize.height;
                }
            }

            Point viewPosition = vp.getViewPosition();
            // The only substantive change is in this block, to change the minimum position x
            // from 0 (left alight) to a negative value that would center the viewport
            if (scrollableView != null && vp.getParent() != null && !vp.getParent()
                .getComponentOrientation().isLeftToRight()) {
                if (extentSize.width > viewSize.width) {
                    viewPosition.x = viewSize.width - extentSize.width;
                } else {
                    viewPosition.x = Math
                        .max(-(extentSize.width-viewSize.width) / 2,
                            Math.min(viewSize.width - extentSize.width,
                                viewPosition.x));
                }
            } else if (viewPosition.x + extentSize.width > viewSize.width) {
                viewPosition.x = Math
                    .max(-(extentSize.width-viewSize.width) / 2,
                        viewSize.width - extentSize.width);
            }

            if (viewPosition.y + extentSize.height > viewSize.height) {
                viewPosition.y = Math.max(0, viewSize.height - extentSize.height);
            }

            if (scrollableView == null) {
                if (viewPosition.x == 0 && vpSize.width > viewPrefSize.width) {
                    viewSize.width = vpSize.width;
                }

                if (viewPosition.y == 0 && vpSize.height > viewPrefSize.height) {
                    viewSize.height = vpSize.height;
                }
            }

            vp.setViewPosition(viewPosition);
            vp.setViewSize(viewSize);

            // Need to repaint to remove some ghost element
            vp.repaint();
        }
    }
}
