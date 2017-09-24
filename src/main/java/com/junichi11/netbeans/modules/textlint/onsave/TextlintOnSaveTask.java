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
package com.junichi11.netbeans.modules.textlint.onsave;

import com.junichi11.netbeans.modules.textlint.TextlintPushTaskScanner;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptions;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.document.OnSaveTask;

/**
 *
 * @author junichi11
 */
public class TextlintOnSaveTask implements OnSaveTask {

    private final Context context;

    private TextlintOnSaveTask(Context context) {
        this.context = context;
    }

    @Override
    public void performTask() {
        if (TextlintOptions.getInstance().refreshOnSave()) {
            TextlintPushTaskScanner.refresh();
        }
    }

    @Override
    public void runLocked(Runnable r) {
        r.run();
    }

    @Override
    public boolean cancel() {
        return true;
    }

    @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 10000)
    public static class Factory implements OnSaveTask.Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new TextlintOnSaveTask(context);
        }

    }
}
