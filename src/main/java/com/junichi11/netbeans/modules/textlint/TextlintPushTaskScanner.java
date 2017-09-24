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
package com.junichi11.netbeans.modules.textlint;

import com.junichi11.netbeans.modules.textlint.annotations.TextlintAnnotation;
import com.junichi11.netbeans.modules.textlint.annotations.TextlintFixableAnnotation;
import com.junichi11.netbeans.modules.textlint.command.InvalidTextlintExecutableException;
import com.junichi11.netbeans.modules.textlint.command.Textlint;
import com.junichi11.netbeans.modules.textlint.json.Fix;
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonReader;
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonUtils;
import com.junichi11.netbeans.modules.textlint.json.TextlintJsonResult;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptions;
import com.junichi11.netbeans.modules.textlint.options.TextlintOptionsPanelController;
import com.junichi11.netbeans.modules.textlint.ui.actions.FixAction;
import com.junichi11.netbeans.modules.textlint.ui.actions.FixAllAction;
import com.junichi11.netbeans.modules.textlint.ui.actions.OpenAction;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = PushTaskScanner.class, path = "TaskList/Scanners")
public class TextlintPushTaskScanner extends PushTaskScanner {

    private TaskScanningScope scope;
    private Callback callback;

    private static final String TEXTLINT_GROUP_NAME = "nb-tasklist-textlint"; // NOI18N
    private static final String TEXTLINT_FIXABLE_GROUP_NAME = "nb-tasklist-textlint-fixable"; // NOI18N
    private static final RequestProcessor RP = new RequestProcessor(TextlintPushTaskScanner.class);
    private static final Logger LOGGER = Logger.getLogger(TextlintPushTaskScanner.class.getName());
    private static TextlintPushTaskScanner INSTANCE;
    private static final Map<FileObject, List<? extends Annotation>> ANNOTATIONS = new HashMap<>();
    private static RequestProcessor.Task currentTask;
    private static boolean refreshing;

    @NbBundle.Messages({
        "TextlintPushTaskScanner.displayName=textlint",
        "TextlintPushTaskScanner.description=Check rules using textlint"
    })
    public TextlintPushTaskScanner() {
        super(Bundle.TextlintPushTaskScanner_displayName(),
                Bundle.TextlintPushTaskScanner_description(),
                TextlintOptionsPanelController.OPTIONS_FULL_PATH);
        INSTANCE = this;
    }

    public static void refresh() {
        if (INSTANCE != null) {
            refreshing = true;
            INSTANCE.setScope(INSTANCE.scope, INSTANCE.callback);
            refreshing = false;
        }
    }

    @NbBundle.Messages("TextlintPushTaskScanner.error.message.modified.file=File was modified or not found. Please save it for scanning.")
    @Override
    public void setScope(TaskScanningScope scope, Callback callback) {
        if (scope == null || callback == null) {
            removeAllAnnotations();
            return;
        }

        // cancel
        if (currentTask != null && !currentTask.isFinished()) {
            currentTask.cancel();
        }

        this.scope = scope;
        this.callback = callback;
        Collection<? extends FileObject> fileObjects = scope.getLookup().lookupAll(FileObject.class);
        for (FileObject fileObject : fileObjects) {
            String ext = fileObject.getExt();
            boolean canBeRun = canBeRun(fileObject);
            if (!canBeRun) {
                StatusDisplayer.getDefault().setStatusText(Bundle.TextlintPushTaskScanner_error_message_modified_file());
            }
            if (isAvailableExt(ext) && canBeRun) {
                currentTask = RP.post(() -> {
                    callback.started();
                    callback.clearAllTasks();
                    File file = FileUtil.toFile(fileObject);
                    LOGGER.log(Level.FINE, "textlint scans the file:{0}", file.getAbsolutePath()); // NOI18N
                    TextlintJsonReader reader;
                    try {
                        reader = Textlint.getDefault().textlint(file.getAbsolutePath());
                        TextlintJsonResult[] results = TextlintJsonUtils.createTextlintResults(reader);
                        // annotations
                        if (TextlintOptions.getInstance().showAnnotation()) {
                            updateAnnotations(fileObject, AnnotationCreator.create(results, fileObject));
                        } else {
                            if (!ANNOTATIONS.isEmpty()) {
                                removeAllAnnotations();
                            }
                        }
                        // tasks
                        callback.setTasks(fileObject, TaskCreator.create(results, fileObject));
                    } catch (InvalidTextlintExecutableException ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage());
                    } finally {
                        callback.finished();
                    }
                });
                return;
            }
        }
    }

    private static boolean canBeRun(FileObject fileObject) {
        try {
            DataObject dataObject = DataObject.find(fileObject);
            return refreshing || !dataObject.isModified();
        } catch (DataObjectNotFoundException ex) {
            // don't run if DataObject doesn't exist
        }
        return false;
    }

    private static boolean isAvailableExt(String ext) {
        return "md".equals(ext) // NOI18N
                || "txt".equals(ext) // NOI18N
                || ("html".equals(ext) && TextlintOptions.getInstance().isHtmlEnabled()); // NOI18N
    }

    private static synchronized void updateAnnotations(FileObject fileObject, List<? extends Annotation> annotations) {
        removeAnnotations(fileObject);
        addAnnotations(fileObject, annotations);
    }

    private static synchronized void addAnnotations(FileObject fileObject, List<? extends Annotation> annotations) {
        ANNOTATIONS.put(fileObject, annotations);
    }

    private static synchronized void removeAnnotations(FileObject fileObject) {
        List<? extends Annotation> removedAnnotations = ANNOTATIONS.remove(fileObject);
        if (removedAnnotations != null) {
            removedAnnotations.forEach(annotation -> annotation.detach());
        }
    }

    private static synchronized void removeAllAnnotations() {
        ANNOTATIONS.forEach((fileObject, annotations) -> {
            annotations.forEach(annotation -> annotation.detach());
        });
        ANNOTATIONS.clear();
    }

    //~ inner classes
    private static class AnnotationCreator {

        private static final String MESSAGE_FORMAT = "[%s]\n%s"; // NOI18N

        public static List<? extends Annotation> create(TextlintJsonResult[] results, FileObject fileObject) {
            List<TextlintAnnotation> annotations = new ArrayList<>();
            DataObject dObject = null;
            try {
                dObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            final DataObject dataObject = dObject;
            if (results != null && dataObject != null) {
                LineCookie lineCookie = dataObject.getLookup().lookup(LineCookie.class);
                final Line.Set lineSet = lineCookie != null ? lineCookie.getLineSet() : null;
                for (TextlintJsonResult result : results) {
                    result.getMessages().forEach((message) -> {
                        // attach annotations
                        int lineNumber = message.getLine();
                        if (lineSet != null && lineNumber > 0) {
                            Line line = lineSet.getOriginal(lineNumber - 1);
                            if (line != null) {
                                TextlintAnnotation annotation;
                                if (message.getFix() != null) {
                                    annotation = new TextlintFixableAnnotation(String.format(MESSAGE_FORMAT, message.getRuleId(), message.getMessage()));
                                } else {
                                    annotation = new TextlintAnnotation(String.format(MESSAGE_FORMAT, message.getRuleId(), message.getMessage()));
                                }
                                annotation.attach(line);
                                annotations.add(annotation);
                            }
                        }
                    });
                }

                fileObject.addFileChangeListener(new FileChangeAdapter() {
                    @Override
                    public void fileChanged(FileEvent fe) {
                        fileObject.removeFileChangeListener(this);
                        annotations.forEach(annotation -> annotation.detach());
                        removeAnnotations(fileObject);
                    }

                    @Override
                    public void fileDeleted(FileEvent fe) {
                        fileObject.removeFileChangeListener(this);
                        annotations.forEach(annotation -> annotation.detach());
                        removeAnnotations(fileObject);
                    }
                });
            }
            return annotations;
        }
    }

    private static class TaskCreator {

        private static final String MESSAGE_FORMAT = "[%s] %s (line: %s offset: %s)"; // NOI18N

        public static List<Task> create(TextlintJsonResult[] results, FileObject fileObject) {
            List<Task> tasks = new ArrayList<>();
            DataObject dObject;
            try {
                dObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                dObject = null;
            }
            final DataObject dataObject = dObject;
            if (results != null && dataObject != null) {
                for (TextlintJsonResult result : results) {
                    result.getMessages().forEach((message) -> {
                        OpenAction defaultAction = new OpenAction(message.getLine(), dataObject);
                        Action[] popupActions = createPopupActions(dataObject, fileObject, message.getFix());
                        String description = String.format(MESSAGE_FORMAT,
                                message.getRuleId(),
                                message.getMessage(),
                                message.getLine(),
                                message.getIndex());
                        String groupName = message.getFix() != null ? TEXTLINT_FIXABLE_GROUP_NAME : TEXTLINT_GROUP_NAME;
                        tasks.add(Task.create(fileObject.toURL(), groupName, description, defaultAction, popupActions));
                    });
                }
            }
            return tasks;
        }

        private static Action[] createPopupActions(DataObject dataObject, FileObject fileObject, Fix fix) {
            if (fix == null) {
                return new Action[0];
            } else {
                return new Action[]{
                    new FixAction(dataObject, fix),
                    new FixAllAction(dataObject, FileUtil.toFile(fileObject).getAbsolutePath())
                };
            }
        }
    }

}
