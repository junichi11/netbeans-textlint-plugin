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
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonReader;
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonResult;
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonUtils;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.progress.BaseProgressUtils;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
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
        // XXX get results using stdin
        // but if something occurs as problems, just use the file path parameter
        JTextComponent lastFocusedComponent = EditorRegistry.lastFocusedComponent();
        if (lastFocusedComponent != null) {
            TextlintJsonReader[] reader = new TextlintJsonReader[1];
            BaseProgressUtils.runOffEventDispatchThread(() -> {
                try {
                    reader[0] = Textlint.getDefault().fixForStdin(lastFocusedComponent.getText());
                } catch (InvalidTextlintExecutableException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage());
                }
            }, "textlint Fixing...", new AtomicBoolean(), true); // NOI18N

            if (reader[0] != null) {
                assert EventQueue.isDispatchThread();
                TextlintJsonResult[] results = TextlintJsonUtils.createTextlintResults(reader[0]);
                for (TextlintJsonResult result : results) {
                    String output = result.getOutput();
                    if (output != null && !output.isEmpty()) {
                        Document document = lastFocusedComponent.getDocument();
                        if (document instanceof StyledDocument) {
                            int caretPosition = lastFocusedComponent.getCaretPosition();
                            NbDocument.runAtomic((StyledDocument) document, () -> {
                                lastFocusedComponent.setText(output);
                            });
                            lastFocusedComponent.requestFocusInWindow();
                            setCaretPosition(caretPosition, output.length(), lastFocusedComponent);
                            TextlintPushTaskScanner.refresh();
                        }
                    }
                }
            }
        }
    }

    private void setCaretPosition(int caretPosition, int outputLength, JTextComponent lastFocusedComponent) {
        if (0 <= caretPosition && caretPosition <= outputLength) {
            lastFocusedComponent.setCaretPosition(caretPosition);
        } else {
            lastFocusedComponent.setCaretPosition(0);
        }
    }

}
