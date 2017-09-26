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
package com.junichi11.netbeans.modules.textlint.ui.actions;

import com.junichi11.netbeans.modules.textlint.TextlintPushTaskScanner;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptions;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptionsPanelController;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@ActionID(category = "textlint", id = "com.junichi11.netbeans.modules.textlint.ui.actions.RefreshAction")
@ActionRegistration(displayName = "#RefreshAction.displayName", lazy = true)
@ActionReferences({
    @ActionReference(path = "Editors/text/plain/Popup", position = 10000),
    @ActionReference(path = "Editors/text/x-markdown/Popup", position = 10000),
    @ActionReference(path = "Editors/text/html/Popup", position = 10000),
})
@NbBundle.Messages("RefreshAction.displayName=textlint Refresh")
public class RefreshAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 9111205447558545742L;

    private DataObject context;

    public RefreshAction() {
        this(null);
    }

    public RefreshAction(DataObject context) {
        this.context = context;
        if (context == null) {
            putValue(Action.NAME, ""); // NOI18N
        } else {
            putValue(Action.NAME, Bundle.RefreshAction_displayName());
        }
        setEnabled(context != null);
    }

    @Override
    public Action createContextAwareInstance(Lookup lookup) {
        String textlintPath = TextlintOptions.getInstance().getTextlintPath();
        if (textlintPath == null || textlintPath.isEmpty()) {
            OptionsDisplayer.getDefault().open(TextlintOptionsPanelController.OPTIONS_FULL_PATH);
            return this;
        }
        DataObject dataObject = lookup.lookup(DataObject.class);
        if (dataObject == null) {
            return this;
        }
        FileObject fileObject = dataObject.getPrimaryFile();
        if (fileObject == null) {
            return this;
        }
        String ext = fileObject.getExt();
        if ("md".equals(ext) || "txt".endsWith(ext) // NOI18N
                || ("html".equals(ext) && TextlintOptions.getInstance().isHtmlEnabled())) { // NOI18N
            return new RefreshAction(dataObject);
        }
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent lastFocusedComponent = EditorRegistry.lastFocusedComponent();
        if (lastFocusedComponent != null) {
            TextlintPushTaskScanner.refresh();
        }
    }

}
