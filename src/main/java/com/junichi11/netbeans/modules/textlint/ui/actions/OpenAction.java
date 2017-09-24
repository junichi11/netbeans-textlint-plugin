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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * Go to the error line.
 *
 * @author junichi11
 */
public class OpenAction extends AbstractAction {

    private static final long serialVersionUID = 8371723464579911704L;

    private final int line;
    private final DataObject dataObject;
    private static final Logger LOGGER = Logger.getLogger(OpenAction.class.getName());

    @NbBundle.Messages("OpenAction.displayName=Go to Error Line")
    public OpenAction(int line, @NonNull DataObject dataObject) {
        super(Bundle.OpenAction_displayName());
        this.line = line;
        this.dataObject = dataObject;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (line < 1) {
            return;
        }
        LineCookie lineCookie = dataObject.getLookup().lookup(LineCookie.class);
        if (lineCookie == null) {
            return;
        }
        Line.Set lineSet = lineCookie.getLineSet();
        try {
            Line currentLine = lineSet.getOriginal(line - 1);
            if (currentLine == null) {
                currentLine = lineSet.getOriginal(0);
            }
            if (currentLine != null) {
                currentLine.show(Line.ShowOpenType.NONE, Line.ShowVisibilityType.FOCUS);
            }
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
        }
    }

}
