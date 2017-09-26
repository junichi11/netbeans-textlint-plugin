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
package com.junichi11.netbeans.modules.textlint.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author junichi11
 */
public final class TextlintJsonUtils {

    private static final Logger LOGGER = Logger.getLogger(TextlintJsonUtils.class.getName());

    private TextlintJsonUtils() {
    }

    public static TextlintJsonResult[] createTextlintResults(TextlintJsonReader reader) {
        Gson gson = new Gson();
        TextlintJsonResult[] results;
        if (reader == null) {
            return new TextlintJsonResult[0];
        }
        try {
            results = gson.fromJson(reader.getReader(), TextlintJsonResult[].class);
        } catch (JsonSyntaxException ex) {
            LOGGER.log(Level.WARNING, "Cannot get results as Json format. Did you Set rules?"); // NOI18N
            results = new TextlintJsonResult[0];
        }
        return results;
    }

}
