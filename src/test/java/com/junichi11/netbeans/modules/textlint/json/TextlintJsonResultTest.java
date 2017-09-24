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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class TextlintJsonResultTest extends NbTestCase {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    public TextlintJsonResultTest(String name) {
        super(name);
    }

    @Before
    @Override
    public void setUp() {
    }

    @After
    @Override
    public void tearDown() {
    }

    @Test
    public void testParseJsonResult() throws FileNotFoundException {
        String fileName = TextlintJsonResult.class
                .getClassLoader()
                .getResource("com/junichi11/netbeans/modules/textlint/json/textlintJsonResult.json")
                .getPath();
        File file = new File(fileName);
        TextlintJsonReader reader = new TextlintJsonReader(new FileReader(file));
        TextlintJsonResult[] results = TextlintJsonUtils.createTextlintResults(reader);
        assertEquals(1, results.length);
        TextlintJsonResult result = results[0];
        assertEquals("/path/to/MyProject/README.md", result.getFilePath());

        List<Message> messages = result.getMessages();
        assertEquals(4, messages.size());

        Message messageDearuDesumasu = messages.get(0);
        assertEquals("lint", messageDearuDesumasu.getType());
        assertEquals("no-mix-dearu-desumasu", messageDearuDesumasu.getRuleId());
        assertEquals("本文: \"である\"調 と \"ですます\"調 が混在\n=> \"である。\" がである調\nTotal:\nである  : 2\nですます: 3\n", messageDearuDesumasu.getMessage());
        assertEquals(127, messageDearuDesumasu.getIndex());
        assertEquals(9, messageDearuDesumasu.getLine());
        assertEquals(4, messageDearuDesumasu.getColumn());
        assertEquals(2, messageDearuDesumasu.getSeverity());
        assertNull(messageDearuDesumasu.getFix());

        Message messageSpellcheck = messages.get(1);
        assertEquals("lint", messageSpellcheck.getType());
        assertEquals("spellcheck-tech-word", messageSpellcheck.getRuleId());
        assertEquals("JAVASCRIPT => JavaScript", messageSpellcheck.getMessage());
        assertEquals(133, messageSpellcheck.getIndex());
        assertEquals(11, messageSpellcheck.getLine());
        assertEquals(1, messageSpellcheck.getColumn());
        assertEquals(2, messageSpellcheck.getSeverity());

        Fix fixSpellcehck = messageSpellcheck.getFix();
        assertNotNull(fixSpellcehck);
        assertEquals(133, fixSpellcehck.getRange()[0]);
        assertEquals(143, fixSpellcehck.getRange()[1]);
        assertEquals("JavaScript", fixSpellcehck.getText());
    }

}
