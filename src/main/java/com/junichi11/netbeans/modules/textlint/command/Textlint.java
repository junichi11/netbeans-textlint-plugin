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
import com.junichi11.netbeans.modules.textlint.options.TextlintOptionsPanelController;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.ProcessBuilder;
import org.netbeans.api.extexecution.base.input.InputReaderTask;
import org.netbeans.api.extexecution.base.input.InputReaders;
import org.netbeans.api.options.OptionsDisplayer;
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
    private static final String STDIN_PARAM = "--stdin"; // NOI18N

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
            OptionsDisplayer.getDefault().open(TextlintOptionsPanelController.OPTIONS_FULL_PATH);
            throw new InvalidTextlintExecutableException("Invalid textlint path:" + path); // NOI18N
        }
        return new Textlint(path);
    }

    @CheckForNull
    public TextlintJsonReader textlintForStdin(String text) {
        List<String> allParams = getAllParamsForStdin();
        return runForStdin(allParams, text);
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

    @CheckForNull
    public TextlintJsonReader fixForStdin(String text) {
        List<String> allParams = getAllParamsForStdin();
        allParams.add(FIX_PARAM);
        return runForStdin(allParams, text);
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

    @CheckForNull
    private TextlintJsonReader runForStdin(List<String> allParams, String text) {
        // don't use org.netbeans.api.extexecution.base.ProcessBuilder
        java.lang.ProcessBuilder processBuilder = new java.lang.ProcessBuilder(allParams);

        // set working directory
        String textlintrcPath = TextlintOptions.getInstance().getTextlintrcPath();
        Path textlintrc = Paths.get(textlintrcPath);
        Path parent = textlintrc.getParent();
        if (parent != null) {
            processBuilder.directory(parent.toFile());
        }

        try {
            Process process = processBuilder.start();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8
            );

            InputReaderTask inputReaderTask = InputReaderTask.newTask(
                    InputReaders.forReader(inputStreamReader),
                    InputProcessors.copying(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))
            );
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(inputReaderTask);

            // prevent freezing: if buffer become full, netbeans freezes
            process.waitFor(1000, TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
            InputStream resultInputStream = process.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(resultInputStream, StandardCharsets.UTF_8));
            return new TextlintJsonReader(reader);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private List<String> getOptionsParams() {
        String options = TextlintOptions.getInstance().getTextlintOptions().trim();
        List<String> params = new ArrayList<>();
        if (!options.isEmpty()) {
            String[] splitOptions = options.split("\\s"); // NOI18N
            for (String option : splitOptions) {
                // prevent to add --fix prams
                if (!option.equals(FIX_PARAM)) {
                    params.add(option);
                }
            }
        }
        return params;
    }

    private List<String> getAllParams(List<String> params) {
        List<String> allParams = getOptionsParams();
        allParams.addAll(params);
        return allParams;
    }

    private List<String> getAllParamsForStdin() {
        List<String> allParams = new ArrayList<>();
        allParams.add(path);
        allParams.addAll(DEFAULT_PARAMS);
        allParams.addAll(getOptionsParams());
        allParams.add(STDIN_PARAM);
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

    //~ inner classes
    private static class JsonLineProcessor implements LineProcessor {

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
