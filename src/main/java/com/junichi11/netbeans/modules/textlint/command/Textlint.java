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
package com.junichi11.netbeans.modules.textlint.command;

import com.junichi11.netbeans.modules.textlint.json.TextlintJsonReader;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptions;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.ProcessBuilder;
import org.openide.windows.InputOutput;

/**
 *
 * @author junichi11
 */
public final class Textlint {

    private final String path;

    // params
    private static final String FORMAT_PARAM = "--format"; // NOI18N
    private static final String FIX_PARAM = "--fix"; // NOI18N
    private static final String NO_COLOR_PARAM = "--no-color"; // NOI18N

    // format
    private static final String JSON_FORMAT = "json"; // NOI18N
    private static final List<String> DEFAULT_PARAMS = Arrays.asList(
            FORMAT_PARAM,
            JSON_FORMAT,
            NO_COLOR_PARAM
    );
    private static final Logger LOGGER = Logger.getLogger(Textlint.class.getName());

    private static final ExecutionDescriptor DEFAULT_SILENT_DESCRIPTOR = new ExecutionDescriptor()
            .charset(StandardCharsets.UTF_8)
            .inputOutput(InputOutput.NULL);

    private Textlint(String path) {
        this.path = path;
    }

    public static Textlint getDefault() throws InvalidTextlintExecutableException {
        String path = TextlintOptions.getInstance().getTextlintPath();
        if (path == null || path.isEmpty()) {
            throw new InvalidTextlintExecutableException("Invalid textlint path:" + path); // NOI18N
        }
        return new Textlint(path);
    }

    public TextlintJsonReader textlint(String filePath) {
        List<String> params = new ArrayList<>(DEFAULT_PARAMS);
        params.add(filePath);
        return textlint(params);
    }

    public TextlintJsonReader textlint(List<String> params) {
        JsonLineProcessor lineProcessor = new JsonLineProcessor();
        Future<Integer> future = runCommand(params, "textlint", lineProcessor); // NOI18N
        try {
            future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return new TextlintJsonReader(lineProcessor.getReader());
    }

    public void fix(String filePath) {
        List<String> params = new ArrayList<>();
        params.add(FIX_PARAM);
        params.add(filePath);
        Future<Integer> future = runCommand(params, "textlint-fix"); // NOI18N
        try {
            future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    private Future<Integer> runCommand(List<String> params, String title) {
        ExecutionDescriptor descriptor = DEFAULT_SILENT_DESCRIPTOR;
        return run(descriptor, params, title);
    }

    private Future<Integer> runCommand(List<String> params, String title, LineProcessor lineProcessor) {
        ExecutionDescriptor descriptor = new ExecutionDescriptor().inputOutput(InputOutput.NULL)
                .charset(StandardCharsets.UTF_8)
                .outProcessorFactory(getOutputProcessorFactory(lineProcessor));
        return run(descriptor, params, title);
    }

    private Future<Integer> run(ExecutionDescriptor executionDescriptor, List<String> params, String title) {
        ProcessBuilder processBuilder = createProcessBuilder(getAllParams(params));
        return ExecutionService.newService(processBuilder, executionDescriptor, title).run();
    }

    private List<String> getAllParams(List<String> params) {
        String options = TextlintOptions.getInstance().getTextlintOptions().trim();
        List<String> allParams = new ArrayList<>();
        if (!options.isEmpty()) {
            String[] splitOptions = options.split("\\s"); // NOI18N
            for (String option : splitOptions) {
                // prevent to add --fix prams
                if (!option.equals(FIX_PARAM)) {
                    allParams.add(option);
                }
            }
        }
        allParams.addAll(params);
        return allParams;
    }

    private ProcessBuilder createProcessBuilder(List<String> params) {
        ProcessBuilder processBuilder = ProcessBuilder.getLocal();
        processBuilder.setExecutable(path);
        processBuilder.setArguments(params);
        String textlintrcPath = TextlintOptions.getInstance().getTextlintrcPath();
        Path textlintrc = Paths.get(textlintrcPath);
        Path parent = textlintrc.getParent();
        if (parent != null) {
            processBuilder.setWorkingDirectory(parent.toFile().getAbsolutePath());
        }
        return processBuilder;
    }

    private ExecutionDescriptor.InputProcessorFactory2 getOutputProcessorFactory(final LineProcessor lineProcessor) {
        return inputProcessor -> {
            return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
        };
    }

    private class JsonLineProcessor implements LineProcessor {

        StringBuilder sb = new StringBuilder();

        public JsonLineProcessor() {
        }

        @Override
        public void processLine(String string) {
            sb.append(string);
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public String getJsonString() {
            return sb.toString();
        }

        public Reader getReader() {
            return new StringReader(sb.toString());
        }
    }

}
