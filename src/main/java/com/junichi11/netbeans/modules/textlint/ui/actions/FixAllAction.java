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
import com.junichi11.netbeans.modules.textlint.command.InvalidTextlintExecutableException;
import com.junichi11.netbeans.modules.textlint.command.Textlint;
import com.junichi11.netbeans.modules.textlint.ui.UiUtils;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 * Run textlint --fix command.
 *
 * @author junichi11
 */
public class FixAllAction extends AbstractAction {

    private static final long serialVersionUID = 7774711324226267487L;
    private static final Logger LOGGER = Logger.getLogger(FixAllAction.class.getName());

    private final String filePath;
    private final DataObject dataObject;

    @NbBundle.Messages("FixAllAction.displayName=Fix All")
    public FixAllAction(@NonNull DataObject dataObject, @NonNull String filePath) {
        super(Bundle.FixAllAction_displayName());
        this.filePath = filePath;
        this.dataObject = dataObject;
    }

    @NbBundle.Messages("FixAllAction.modified.file.error.message=Please save the file once.")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (dataObject.isModified()) {
            UiUtils.showErrorMessage(Bundle.FixAllAction_modified_file_error_message());
            return;
        }
        try {
            Textlint.getDefault().fix(filePath);

            // prevent that annotations are detached
            JTextComponent lastFocusedComponent = EditorRegistry.lastFocusedComponent();
            if (lastFocusedComponent != null) {
                lastFocusedComponent.requestFocusInWindow();
            }
            TextlintPushTaskScanner.refresh();
        } catch (InvalidTextlintExecutableException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

}
