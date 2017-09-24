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

import com.junichi11.netbeans.modules.textlint.json.Fix;
import com.junichi11.netbeans.modules.textlint.ui.UiUtils;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class FixAction extends AbstractAction {

    private static final long serialVersionUID = 3728010110802060908L;

    private final DataObject dataObject;
    private final Fix fix;
    private static final Logger LOGGER = Logger.getLogger(FixAction.class.getName());

    @NbBundle.Messages("FixAction.displayName=Fix")
    public FixAction(DataObject dataObject, Fix fix) {
        super(Bundle.FixAction_displayName());
        this.dataObject = dataObject;
        this.fix = fix;
    }

    @NbBundle.Messages("FixAction.modified.file.error.message=Please save the file once.")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (fix == null) {
            return;
        }

        if (dataObject.isModified()) {
            UiUtils.showErrorMessage(Bundle.FixAction_modified_file_error_message());
            return;
        }

        EditorCookie editorCookie = dataObject.getLookup().lookup(EditorCookie.class);
        StyledDocument doc = editorCookie.getDocument();
        if (doc == null) {
            return;
        }

        fix(doc, editorCookie);
    }

    private void fix(StyledDocument doc, final EditorCookie editorCookie) {
        NbDocument.runAtomic(doc, () -> {
            try {
                doc.remove(fix.getRange()[0], fix.getRange()[1] - fix.getRange()[0]);
                doc.insertString(fix.getRange()[0], fix.getText(), null);
            } catch (BadLocationException ex) {
                LOGGER.log(Level.WARNING, "Incorrect offset:{0}", ex.offsetRequested()); // NOI18N
            }
        });
        try {
            if (editorCookie != null) {
                editorCookie.saveDocument();
            } else {
                LOGGER.log(Level.WARNING, "Cannot save: EditorCookie is null"); // NOI18N
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

}
