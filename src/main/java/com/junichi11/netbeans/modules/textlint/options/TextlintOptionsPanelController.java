/*
 * Copyright 2017 junichi11.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.junichi11.netbeans.modules.textlint.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@OptionsPanelController.SubRegistration(
        id = TextlintOptionsPanelController.OPTIONS_SUB_PATH,
        location = "Editor",
        displayName = "#AdvancedOption_DisplayName_Textlint",
        keywords = "#AdvancedOption_Keywords_Textlint",
        keywordsCategory = "Editor/Textlint"
)
@NbBundle.Messages({
    "AdvancedOption_DisplayName_Textlint=textlint",
    "AdvancedOption_Keywords_Textlint=textlint"
})
public final class TextlintOptionsPanelController extends OptionsPanelController implements ChangeListener {

    private TextlintOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public static final String OPTIONS_SUB_PATH = "Textlint"; // NOI18N
    public static final String OPTIONS_FULL_PATH = "Editor/" + OPTIONS_SUB_PATH; // NOI18N

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().store();
            changed = false;
        });
    }

    @Override
    public void cancel() {
        changed = false;
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private TextlintOptionsPanel getPanel() {
        if (panel == null) {
            panel = new TextlintOptionsPanel(this);
            panel.addChangeListener(this);
        }
        return panel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
