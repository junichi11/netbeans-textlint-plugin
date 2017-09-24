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

/**
 *
 * @author junichi11
 */
public final class TextlintJsonUtils {

    private TextlintJsonUtils() {
    }

    public static TextlintJsonResult[] createTextlintResults(TextlintJsonReader reader) {
        Gson gson = new Gson();
        TextlintJsonResult[] results = gson.fromJson(reader.getReader(), TextlintJsonResult[].class);
        return results;
    }

}
