package com.junichi11.netbeans.modules.textlint.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.openide.util.Exceptions;

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
/**
 *
 * @author junichi11
 */
public final class TextlintUtils {

    private static final String NBP_TEXTLINT = ".nbp/textlint"; // NOI18N

    private TextlintUtils() {
    }

    /**
     * Get Properties from USER_HOME/.nbp/textlint file.
     *
     * @return properties
     * @throws IOException
     */
    public static Properties getProperties() throws IOException {
        Path userHome = Paths.get(System.getProperty("user.home")); // NOI18N
        Path textlintPropertiesPath = userHome.resolve(NBP_TEXTLINT);
        File textlintPropertiesFile = textlintPropertiesPath.toFile();
        Properties properties = new Properties();
        if (!textlintPropertiesFile.exists()) {
            return properties;
        }

        try (FileInputStream inputStream = new FileInputStream(textlintPropertiesFile)) {
            properties.load(inputStream);
        }

        return properties;
    }

}
