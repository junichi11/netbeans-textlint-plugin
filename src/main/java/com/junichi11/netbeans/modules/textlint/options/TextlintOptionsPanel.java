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

import com.junichi11.netbeans.modules.textlint.utils.TextlintUtils;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

final class TextlintOptionsPanel extends javax.swing.JPanel {

    private static final String TEXTLINT_LAST_FOLDER_SUFFIX = ".textlint"; // NOI18N
    private static final String TEXTLINTRC_LAST_FOLDER_SUFFIX = ".textlintrc"; // NOI18N
    private static final long serialVersionUID = -7873059711550842158L;
    private static final Logger LOGGER = Logger.getLogger(TextlintOptionsPanel.class.getName());
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final TextlintOptionsPanelController controller;

    TextlintOptionsPanel(TextlintOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        setError(""); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N

        // add listeners
        DocumentListener documentListener = new DefaultDocumentListener();
        textlintPathTextField.getDocument().addDocumentListener(documentListener);
        textlintrcPathTextField.getDocument().addDocumentListener(documentListener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textlintPathLabel = new javax.swing.JLabel();
        textlintPathTextField = new javax.swing.JTextField();
        textlintrcPathLabel = new javax.swing.JLabel();
        textlintrcPathTextField = new javax.swing.JTextField();
        textlintOptionsLabel = new javax.swing.JLabel();
        textlintOptionsTextField = new javax.swing.JTextField();
        textlintHtmlCheckBox = new javax.swing.JCheckBox();
        textlintPathBrowseButton = new javax.swing.JButton();
        textlintrcPathBrowseButton = new javax.swing.JButton();
        textlintRefreshOnSaveCheckBox = new javax.swing.JCheckBox();
        textlintShowAnnotationsCheckBox = new javax.swing.JCheckBox();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(textlintPathLabel, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintPathLabel.text")); // NOI18N

        textlintPathTextField.setText(org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(textlintrcPathLabel, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintrcPathLabel.text")); // NOI18N

        textlintrcPathTextField.setText(org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintrcPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(textlintOptionsLabel, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintOptionsLabel.text")); // NOI18N

        textlintOptionsTextField.setText(org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintOptionsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(textlintHtmlCheckBox, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintHtmlCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(textlintPathBrowseButton, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintPathBrowseButton.text")); // NOI18N
        textlintPathBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textlintPathBrowseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(textlintrcPathBrowseButton, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintrcPathBrowseButton.text")); // NOI18N
        textlintrcPathBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textlintrcPathBrowseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(textlintRefreshOnSaveCheckBox, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintRefreshOnSaveCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(textlintShowAnnotationsCheckBox, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.textlintShowAnnotationsCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(TextlintOptionsPanel.class, "TextlintOptionsPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textlintrcPathLabel)
                    .addComponent(textlintPathLabel)
                    .addComponent(textlintOptionsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textlintrcPathTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .addComponent(textlintPathTextField, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textlintrcPathBrowseButton)
                            .addComponent(textlintPathBrowseButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(textlintOptionsTextField)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textlintHtmlCheckBox)
                    .addComponent(textlintRefreshOnSaveCheckBox)
                    .addComponent(textlintShowAnnotationsCheckBox)
                    .addComponent(errorLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textlintPathLabel)
                    .addComponent(textlintPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textlintPathBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textlintrcPathLabel)
                    .addComponent(textlintrcPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textlintrcPathBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textlintOptionsLabel)
                    .addComponent(textlintOptionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textlintHtmlCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textlintRefreshOnSaveCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textlintShowAnnotationsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("TextlintOptionsPanel.browse.textlint.title=Select textlint script")
    private void textlintPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textlintPathBrowseButtonActionPerformed
        File textlint = new FileChooserBuilder(TextlintOptionsPanel.class.getName() + TEXTLINT_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.TextlintOptionsPanel_browse_textlint_title())
                .setFilesOnly(true)
                .showOpenDialog();
        if (textlint != null) {
            textlint = FileUtil.normalizeFile(textlint);
            String textlintPath = textlint.getAbsolutePath();
            textlintPathTextField.setText(textlintPath);
        }
    }//GEN-LAST:event_textlintPathBrowseButtonActionPerformed

    @NbBundle.Messages("TextlintOptionsPanel.browse.textlintrc.title=Select .textlintrc")
    private void textlintrcPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textlintrcPathBrowseButtonActionPerformed
        File textlintrc = new FileChooserBuilder(TextlintOptionsPanel.class.getName() + TEXTLINTRC_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.TextlintOptionsPanel_browse_textlintrc_title())
                .setFilesOnly(true)
                .showOpenDialog();
        if (textlintrc != null) {
            textlintrc = FileUtil.normalizeFile(textlintrc);
            String textlintrcPath = textlintrc.getAbsolutePath();
            textlintrcPathTextField.setText(textlintrcPath);
        }
    }//GEN-LAST:event_textlintrcPathBrowseButtonActionPerformed

    void load() {
        TextlintOptions options = TextlintOptions.getInstance();
        textlintPathTextField.setText(options.getTextlintPath());
        textlintrcPathTextField.setText(options.getTextlintrcPath());
        textlintOptionsTextField.setText(options.getTextlintOptions());
        textlintHtmlCheckBox.setSelected(options.isHtmlEnabled());
        textlintRefreshOnSaveCheckBox.setSelected(options.refreshOnSave());
        textlintShowAnnotationsCheckBox.setSelected(options.showAnnotation());
        loadFromProperties(options);
    }

    void store() {
        TextlintOptions options = TextlintOptions.getInstance();
        String textlintPath = textlintPathTextField.getText().trim();
        options.setTextlintPath(textlintPath);
        options.setTextlintrcPath(textlintrcPathTextField.getText().trim());
        options.setTextlintOptions(textlintOptionsTextField.getText().trim());
        options.setHtmlEnabled(textlintHtmlCheckBox.isSelected());
        options.setRefreshOnSave(textlintRefreshOnSaveCheckBox.isSelected());
        options.setShowAnnotation(textlintShowAnnotationsCheckBox.isSelected());
        if (!textlintPath.isEmpty()) {
            options.setInitialized(true);
        }
    }

    private void loadFromProperties(TextlintOptions options) {
        if (options.initialized()) {
            return;
        }
        try {
            Properties properties = TextlintUtils.getProperties();
            String textlintPath = properties.getProperty(TextlintOptions.TEXTLINT_PATH, options.getTextlintPath());
            textlintPathTextField.setText(textlintPath);
            if (!StringUtils.isEmpty(textlintPath)) {
                textlintrcPathTextField.setText(properties.getProperty(TextlintOptions.TEXTLINTRC_PATH, options.getTextlintrcPath()));
                textlintOptionsTextField.setText(properties.getProperty(TextlintOptions.TEXTLINT_OPTIONS, options.getTextlintOptions()));
                String html = properties.getProperty(TextlintOptions.TEXTLINT_HTML);
                textlintHtmlCheckBox.setSelected(html != null ? Boolean.valueOf(html) : options.isHtmlEnabled());
                String refreshOnSave = properties.getProperty(TextlintOptions.TEXTLINT_REFRESH);
                textlintRefreshOnSaveCheckBox.setSelected(refreshOnSave != null ? Boolean.valueOf(refreshOnSave) : options.refreshOnSave());
                String showAnnotation = properties.getProperty(TextlintOptions.TEXTLINT_SHOW_ANNOTATION);
                textlintShowAnnotationsCheckBox.setSelected(showAnnotation != null ? Boolean.valueOf(showAnnotation) : options.showAnnotation());
                if (valid()) {
                    store();
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Please try to check your .nbp/textlint file. e.g. properties file format", ex); // NOI18N
        }
    }

    @NbBundle.Messages({
        "TextlintOptionsPanel.invalid.textlint.path=Existing file path must be set.",
        "TextlintOptionsPanel.invalid.textlintrc.path=Existing file path must be set.",
        "TextlintOptionsPanel.invalid.textlintrc.name=File name is not .textlintrc"
    })
    boolean valid() {
        // textlint path
        String textlintPathString = textlintPathTextField.getText().trim();
        File textlintFile = new File(textlintPathString);
        if (!textlintPathString.isEmpty() && !textlintFile.exists()) {
            setError(Bundle.TextlintOptionsPanel_invalid_textlint_path());
            return false;
        }

        // .textlintrc path
        String textlintrcPathString = textlintrcPathTextField.getText().trim();
        File textlintrcFile = new File(textlintrcPathString);
        if (!textlintrcPathString.isEmpty()) {
            if (!textlintrcFile.exists()) {
                setError(Bundle.TextlintOptionsPanel_invalid_textlintrc_path());
                return false;
            }
            if (!textlintrcPathString.endsWith(".textlintrc")) { // NOI18N
                setError(Bundle.TextlintOptionsPanel_invalid_textlintrc_name());
                return false;
            }
        }

        controller.cancel();
        setError(""); // NOI18N
        return true;
    }

    private void setError(String message) {
        if (message == null) {
            errorLabel.setText(""); // NOI18N
        } else {
            errorLabel.setText(message);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel errorLabel;
    private javax.swing.JCheckBox textlintHtmlCheckBox;
    private javax.swing.JLabel textlintOptionsLabel;
    private javax.swing.JTextField textlintOptionsTextField;
    private javax.swing.JButton textlintPathBrowseButton;
    private javax.swing.JLabel textlintPathLabel;
    private javax.swing.JTextField textlintPathTextField;
    private javax.swing.JCheckBox textlintRefreshOnSaveCheckBox;
    private javax.swing.JCheckBox textlintShowAnnotationsCheckBox;
    private javax.swing.JButton textlintrcPathBrowseButton;
    private javax.swing.JLabel textlintrcPathLabel;
    private javax.swing.JTextField textlintrcPathTextField;
    // End of variables declaration//GEN-END:variables

    //~ inner class
    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }
}
