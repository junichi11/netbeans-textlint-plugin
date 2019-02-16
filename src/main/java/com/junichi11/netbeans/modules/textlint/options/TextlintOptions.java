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

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class TextlintOptions {

    private static final String TEXTLINT = "textlint"; // NOI18N
    private static final String TEXTLINT_INITIALIZED = "textlint.initialized"; // NOI18N
    static final String TEXTLINT_PATH = "textlint.path"; // NOI18N
    static final String TEXTLINTRC_PATH = "textlintrc.path"; // NOI18N
    static final String TEXTLINT_OPTIONS = "textlint.options"; // NOI18N
    static final String TEXTLINT_HTML = "textlint.enable.html"; // NOI18N
    static final String TEXTLINT_REFRESH = "textlint.refresh.onsave"; // NOI18N
    static final String TEXTLINT_SHOW_ANNOTATION = "textlint.show.annotation"; // NOI18N
    private static final TextlintOptions INSTANCE = new TextlintOptions();

    private TextlintOptions() {
    }

    public static TextlintOptions getInstance() {
        return INSTANCE;
    }

    public String getTextlintPath() {
        return getPreferences().get(TEXTLINT_PATH, ""); // NOI18N
    }

    public void setTextlintPath(String path) {
        getPreferences().put(TEXTLINT_PATH, path);
    }

    public String getTextlintrcPath() {
        return getPreferences().get(TEXTLINTRC_PATH, ""); // NOI18N
    }

    public void setTextlintrcPath(String path) {
        getPreferences().put(TEXTLINTRC_PATH, path);
    }

    public String getTextlintOptions() {
        return getPreferences().get(TEXTLINT_OPTIONS, ""); // NOI18N
    }

    public void setTextlintOptions(String options) {
        getPreferences().put(TEXTLINT_OPTIONS, options);
    }

    public boolean isHtmlEnabled() {
        return getPreferences().getBoolean(TEXTLINT_HTML, false);
    }

    public void setHtmlEnabled(boolean isEnabled) {
        getPreferences().putBoolean(TEXTLINT_HTML, isEnabled);
    }

    public boolean refreshOnSave() {
        return getPreferences().getBoolean(TEXTLINT_REFRESH, true);
    }

    public void setRefreshOnSave(boolean refreshOnSave) {
        getPreferences().putBoolean(TEXTLINT_REFRESH, refreshOnSave);
    }

    public boolean showAnnotation() {
        return getPreferences().getBoolean(TEXTLINT_SHOW_ANNOTATION, true);
    }

    public void setShowAnnotation(boolean show) {
        getPreferences().putBoolean(TEXTLINT_SHOW_ANNOTATION, show);
    }

    public boolean initialized() {
        return getPreferences().getBoolean(TEXTLINT_INITIALIZED, false);
    }

    public void setInitialized(boolean initialized) {
        getPreferences().putBoolean(TEXTLINT_INITIALIZED, initialized);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(TextlintOptions.class).node(TEXTLINT);
    }

}
