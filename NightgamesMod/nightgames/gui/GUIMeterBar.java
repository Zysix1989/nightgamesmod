package nightgames.gui;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

public class GUIMeterBar extends JProgressBar {
    public GUIMeterBar() {
        setUI(new MeterBarUI());
    }

    public void setModel(BoundedRangeModel newModel) {
        BoundedRangeModel oldModel = this.getModel();
        if (newModel != oldModel) {
            if (oldModel != null) {
                oldModel.removeChangeListener(this.changeListener);
                this.changeListener = null;
            }

            this.model = newModel;
            if (newModel != null) {
                this.changeListener = this.createChangeListener();
                newModel.addChangeListener(this.changeListener);
            }

            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleValue", oldModel == null ? null : oldModel.getValue(), newModel == null ? null : newModel.getValue());
            }

            // Removed a clause here setting the extent to zero.
            this.repaint();
        }
    }

    public double getUncertainPercentComplete() {
        long span = this.model.getMaximum() - this.model.getMinimum();
        double extentValue = this.model.getExtent();
        return (extentValue) / (double)span;
    }

    public void setExtent(int extent) {
        this.model.setExtent(extent);
    }
}
