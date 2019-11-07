package nightgames.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalProgressBarUI;

public class MeterBarUI extends MetalProgressBarUI {
    // Almost all of this code was stolen in some part from BasicProgressBarUI.paintDeterminate
    // and then modified to add in the animations.  It calls paintDeterminate because
    // paintIndeterminate doesn't paint any meter at all and instead does a side to side
    // moving bar animation that we don't want as a base.
    //
    // If it breaks, go check BasicProgressBarUI to see if something there changed.
    @Override
    public void paintIndeterminate(Graphics g, JComponent c) {
        paintDeterminate(g, c);
        if (g instanceof Graphics2D) {
            Insets b = this.progressBar.getInsets();
            int barRectWidth = this.progressBar.getWidth() - (b.right + b.left);
            int barRectHeight = this.progressBar.getHeight() - (b.top + b.bottom);
            if (barRectWidth > 0 && barRectHeight > 0) {
                int cellLength = this.getCellLength();
                int cellSpacing = this.getCellSpacing();
                int amountFull = this.getAmountFull(b, barRectWidth, barRectHeight);
                Graphics2D g2 = (Graphics2D) g;
                if (this.progressBar instanceof GUICoreStatBar) {
                    var fgColor = this.progressBar.getForeground();
                    var middleFrame = this.getFrameCount() / 2;
                    float alpha;
                    var animationIndex = getAnimationIndex();
                    if (animationIndex <= middleFrame) {
                        alpha = ((float) animationIndex) / ((float) middleFrame);
                    } else {
                        alpha =
                            1.0f - (((float) animationIndex - middleFrame) / ((float) middleFrame));
                    }
                    float currentAlpha = fgColor.getAlpha();
                    int newAlpha = Math.round(currentAlpha * alpha);
                    var fadeColor = new Color(fgColor.getRed(),
                        fgColor.getGreen(),
                        fgColor.getBlue(),
                        newAlpha);
                    g2.setColor(fadeColor);
                    int uncertainAmountFull = getUncertainAmountFull(b, barRectWidth, barRectHeight);
                    if (uncertainAmountFull != 0) {
                        if (this.progressBar.getOrientation() == 0) {
                            if (cellSpacing == 0 && amountFull > 0) {
                                g2.setStroke(new BasicStroke((float) barRectHeight,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL));
                            } else {
                                g2.setStroke(new BasicStroke((float) barRectHeight,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL,
                                    0.0F,
                                    new float[]{(float) cellLength, (float) cellSpacing},
                                    0.0F));
                            }

                            if (c.getComponentOrientation().isLeftToRight()) {
                                g2.drawLine(b.left + amountFull,
                                    barRectHeight / 2 + b.top,
                                    uncertainAmountFull + amountFull + b.left,
                                    barRectHeight / 2 + b.top);
                            } else {
                                g2.drawLine(barRectWidth - amountFull + b.left,
                                    barRectHeight / 2 + b.top,
                                    barRectWidth + b.left - amountFull - uncertainAmountFull,
                                    barRectHeight / 2 + b.top);
                            }
                        } else {
                            if (cellSpacing == 0 && amountFull > 0) {
                                g2.setStroke(new BasicStroke((float) barRectWidth,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL));
                            } else {
                                g2.setStroke(new BasicStroke((float) barRectWidth,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL,
                                    0.0F,
                                    new float[]{(float) cellLength, (float) cellSpacing},
                                    0.0F));
                            }

                            g2.drawLine(barRectWidth / 2 + b.left,
                                b.top + barRectHeight - amountFull,
                                barRectWidth / 2 + b.left,
                                b.top + barRectHeight - amountFull - uncertainAmountFull);
                        }
                    }
                }
            }
        }
    }

    private int getUncertainAmountFull(Insets b, int width, int height) {
        assert this.progressBar instanceof GUICoreStatBar;
        var gmb = (GUICoreStatBar) this.progressBar;
        int amountFull = 0;
        BoundedRangeModel model = this.progressBar.getModel();
        if (model.getMaximum() - model.getMinimum() != 0) {
            if (this.progressBar.getOrientation() == 0) {
                amountFull = (int)Math.round((double)width * gmb.getUncertainPercentComplete());
            } else {
                amountFull = (int)Math.round((double)height * gmb.getUncertainPercentComplete());
            }
        }

        return amountFull;
    }

    // This decides the part of the progress bar that needs to repainted (and also affects the text
    // color a bit, I think.)  Keep it in line with any changes above to which regions
    // need to be updated.
    @Override
    protected Rectangle getBox(Rectangle r) {
        if (r == null) {
            r = new Rectangle();
        }
        if (this.progressBar instanceof GUICoreStatBar) {
            Insets b = this.progressBar.getInsets();
            var amountFull = getAmountFull(b, this.progressBar.getWidth(),
                this.progressBar.getHeight());
            var uncertainAmountFull = getUncertainAmountFull(b, this.progressBar.getWidth(),
                this.progressBar.getHeight());
            // I think this makes it slightly larger than it needs to be, but I kept getting bars
            // a few pixels wide on the edges of the region that needed to be repainted
            // that didn't get repainted.
            if (this.progressBar.getOrientation() == 0) {
                r.x = amountFull;
                r.width = uncertainAmountFull + b.left;
                r.y = 0;
                r.height = this.progressBar.getHeight();
            } else {
                r.x = 0;
                r.width = this.progressBar.getWidth();
                r.y = amountFull;
                r.height = uncertainAmountFull + b.top;
            }
        } else {
            super.getBox(r);
        }
        return r;
    }

}
