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
import javax.swing.AbstractAction;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * Go to the error line.
 *
 * @author junichi11
 */
public class OpenAction extends AbstractAction {

    private static final long serialVersionUID = 5960169273364164482L;

    private final Line line;

    @NbBundle.Messages("OpenAction.displayName=Go to Error Line")
    public OpenAction(Line line) {
        super(Bundle.OpenAction_displayName());
        this.line = line;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (line != null) {
            line.show(Line.ShowOpenType.NONE, Line.ShowVisibilityType.FOCUS);
        }
    }

}
