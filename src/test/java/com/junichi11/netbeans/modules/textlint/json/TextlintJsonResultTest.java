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

    @Test
    public void testParseFixJsonResult() throws FileNotFoundException {
        String fileName = TextlintJsonResult.class
                .getClassLoader()
                .getResource("com/junichi11/netbeans/modules/textlint/json/textlintFixJsonResult.json")
                .getPath();
        File file = new File(fileName);
        TextlintJsonReader reader = new TextlintJsonReader(new FileReader(file));
        TextlintJsonResult[] results = TextlintJsonUtils.createTextlintResults(reader);
        assertEquals(1, results.length);
        TextlintJsonResult result = results[0];
        assertEquals("<text>", result.getFilePath());

        assertEquals("# README\n\nSay JavaScript!\n\nHTML Imports is bad word. HTML Imports is correct.\n", result.getOutput());

        // messages
        List<Message> messages = result.getMessages();
        assertEquals(2, messages.size());

        Message message1 = messages.get(0);
        assertEquals("lint", message1.getType());
        assertEquals("spellcheck-tech-word", message1.getRuleId());
        assertEquals("Javascript => JavaScript", message1.getMessage());
        assertEquals(14, message1.getIndex());
        assertEquals(3, message1.getLine());
        assertEquals(5, message1.getColumn());
        assertEquals(2, message1.getSeverity());

        Fix fix1 = message1.getFix();
        assertNotNull(fix1);
        assertEquals(14, fix1.getRange()[0]);
        assertEquals(24, fix1.getRange()[1]);
        assertEquals("JavaScript", fix1.getText());

        Message message2 = messages.get(1);
        assertEquals("lint", message2.getType());
        assertEquals("spellcheck-tech-word", message2.getRuleId());
        assertEquals("HTML Import => HTML Imports", message2.getMessage());
        assertEquals(27, message2.getIndex());
        assertEquals(5, message2.getLine());
        assertEquals(1, message2.getColumn());
        assertEquals(2, message2.getSeverity());

        Fix fix2 = message2.getFix();
        assertNotNull(fix2);
        assertEquals(27, fix2.getRange()[0]);
        assertEquals(38, fix2.getRange()[1]);
        assertEquals("HTML Imports", fix2.getText());

        // applyingMessages
        List<Message> applyingMessages = result.getApplyingMessages();
        assertEquals(2, applyingMessages.size());

        Message applyingMessage1 = applyingMessages.get(0);
        assertEquals("lint", applyingMessage1.getType());
        assertEquals("spellcheck-tech-word", applyingMessage1.getRuleId());
        assertEquals("Javascript => JavaScript", applyingMessage1.getMessage());
        assertEquals(14, applyingMessage1.getIndex());
        assertEquals(3, applyingMessage1.getLine());
        assertEquals(5, applyingMessage1.getColumn());
        assertEquals(2, applyingMessage1.getSeverity());

        Fix applyingFix1 = applyingMessage1.getFix();
        assertNotNull(applyingFix1);
        assertEquals(14, applyingFix1.getRange()[0]);
        assertEquals(24, applyingFix1.getRange()[1]);
        assertEquals("Javascript", applyingFix1.getText());

        Message applyingMessage2 = applyingMessages.get(1);
        assertEquals("lint", applyingMessage2.getType());
        assertEquals("spellcheck-tech-word", applyingMessage2.getRuleId());
        assertEquals("HTML Import => HTML Imports", applyingMessage2.getMessage());
        assertEquals(27, applyingMessage2.getIndex());
        assertEquals(5, applyingMessage2.getLine());
        assertEquals(1, applyingMessage2.getColumn());
        assertEquals(2, applyingMessage2.getSeverity());

        Fix applyingFix2 = applyingMessage2.getFix();
        assertNotNull(applyingFix2);
        assertEquals(27, applyingFix2.getRange()[0]);
        assertEquals(39, applyingFix2.getRange()[1]);
        assertEquals("HTML Import", applyingFix2.getText());

        // remainingMessages
        List<Message> remaininingMessages = result.getRemainingMessages();
        assertEquals(0, remaininingMessages.size());

    }

}
