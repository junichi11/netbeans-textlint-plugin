## NetBeans textlint Plugin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Build Status](https://travis-ci.org/junichi11/netbeans-textlint-plugin.svg?branch=master)](https://travis-ci.org/junichi11/netbeans-textlint-plugin)

This plugin provides support for textlint.

![annotations](screenshots/nb-textlint-annotations.png)

### Donation

- https://github.com/sponsors/junichi11

### What's the textlint?

See https://github.com/textlint/textlint

### Downloads

- https://github.com/junichi11/netbeans-textlint-plugin/releases
- http://plugins.netbeans.org/plugin/73031/textlint-support

### Usage

#### Install textlint and rules

Of course, it assumes that nodejs and npm are installed.

e.g.

```
$ mkdir txtlint
$ cd txtlint
$ npm init
$ npm install textlint --save-dev
$ npm install textlint-rule-max-ten textlint-rule-spellcheck-tech-word textlint-rule-no-mix-dearu-desumasu --save-dev
```

#### Create .textlintrc

```
$ touch .textlintrc
```

```json
{
  "rules": {
    "max-ten": {
      "max": 3
    },
    "spellcheck-tech-word": true,
    "no-mix-dearu-desumasu": true
  }
}
```

You can also set parameters to Options (see below).

#### Set textlint and .textlintrc paths

Set paths to the Options (see below).

e.g.

- textlint Path: /path/to/txtlint/node_modules/.bin/textlint (textlint.cmd in Windows)
- .textlintrc Path: /path/to/textlint/.textlintrc

#### Open Action Items window

- Click Window > Action Items.
- Click "Show action items for currently edited file only" icon.
- Open your markdown or text file.

![action items window](screenshots/nb-textlint-action-items-window.png)

### Options

Tools > Options > Editor > textlint

- textlint Path: Absolute path to textlint
- .textlintrc Path: Absolute path to .textlintrc
- Options : You can set options for the textlint command
- Enable in HTML files: To use the html plugin, you can check this
- Refresh on Save: To scan the document on save, you can check this (Checked by default)
- Show Annotations: To show annotations in the glyph gutter, you can check this (Checked by default)

### Actions

#### Fix

You have to save your file before you run this action.
If there is a fixable rule's error, you can fix it. Right-click an item > Click `Fix`.
To refresh items, your document is saved once.

#### Fix All

You have to save your file before you run this action.
If there are fixable rule's errors, you can fix them. Right-click an item > Click `Fix All`.
This action runs `textlint --fix`command.

#### Refresh

You can refresh results forcibly by the following action: Right-click your editor > Click `textlint Refresh`.
You can also set the shortcut key(Tools > Options > Keymap).

### Icons

- ![normal errors](screenshots/textlint_icon_16.png): Normal errors
- ![fixable errors](screenshots/textlint_fixable_icon_16.png): Fixable errors

### Color & Effect

See Tools > Options > Fonts & Colors > Annotations > com-junichi11-netbeans-modules-textlint-*

### NOTE

- The plugin scans only current file.
- The plugin does not refresh results automatically. Please save your file or run the refresh action.
- Use `UTF-8` as file encoding and `LF` as line endings.
- This plugin may not work properly in Windows. (Please try to check above.)
- If you cannot get expected results, just try to run the `textlint` commands once in your CLI.
