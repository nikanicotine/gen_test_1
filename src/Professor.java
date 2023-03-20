//package org.pim.psu.studenttest;

import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Professor extends Applet { //TODO JApplet or JFrame or ???
    //applet parameters
    private String testfilename, testfileencoding, propertiesfilename = "Properties";
    private int testmode, language, fontsize, mode;
    private boolean questionsmixer, choicemode, showcorrect;

    private final int RUSSIAN = 1, ENGLISH = 2;
    private final int LEARNING = 0, TESTING = 1, STRONG = 2;
    private final int OTHER = 0, EXACT = 1, PROPER = 2, TOTAL = 3;
    private final int TEST = 0, EDITOR = 1;

    //AWT elements
    private JLabel nameLabel, groupLabel, modeLabel, questionLabel,
            answerLabel = new JLabel(),
            noteLabel = new JLabel(),
            correctanswerLabel = new JLabel();
    private JTextField name = new JTextField();
    private JTextField group = new JTextField();
    private TextField[] answerTextField;
    private Choice modeChoice = new Choice();
    private JButton prevButton, nextButton, resultButton, testButton,
            addButton, deleteButton, editButton,
            newButton, openButton, saveButton,
            okButton, cancelButton, incButton, decButton;
    private JTextArea greetingTextArea, questionTextArea;
    private JPanel userPanel, modePanel, filePanel, greetingPanel,
            answersPanel, notePanel, actionsPanel, buttonsPanel,
            incdecPanel;
    private CheckboxGroup checkboxGroup;
    private Checkbox[] checkboxes;
    private Font font;

    //test data
    private Vector questionset;
    private int[] questionsorder;
    private int current = 0;

    //resources
    private ResourceBundle resourceBundle;
    private PropertyResourceBundle propertyResourceBundle;

    public static void main(String[] args) {
        int width = 400, height = 500;
        Dialog professor = new Dialog(new Frame(), "Question redactor");
        Professor test = new Professor();
        professor.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        professor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        professor.add(test, c);
        professor.pack();
        test.initEditor();
        Insets di = professor.getInsets();
        professor.setSize(di.left + width + di.right, di.top + height + di.bottom);
        Dimension ds = professor.getToolkit().getScreenSize(), dd = professor.getSize();
        professor.setLocation((ds.width - dd.width) / 2, (ds.height - dd.height) / 2);
        professor.show();
    }

//    public void init() {
//        initTest();
//    }
//
//    public void initTest() {
//        mode = TEST;
//        try {
//            readParameters((new URL(getCodeBase(), propertiesfilename)).openStream());
//            readTestFile((new URL(getCodeBase(), testfilename)).openStream());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        initTestAWTComponents();
//        newTest();
//    }

    public void initEditor() {
        mode = EDITOR;
        try {
            readParameters(new FileInputStream(propertiesfilename));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        initEditorAWTComponents();
        newEditor();
    }

    private void readResources() {
        switch (language) {
            case RUSSIAN:
                resourceBundle = ResourceBundle.getBundle("Resources", new Locale("ru", "RU"));
                break;
            case ENGLISH:
                resourceBundle = ResourceBundle.getBundle("Resources", new Locale("en", "US"));
                break;
        }
    }

    private String readParameter(String parameter) {
        return propertyResourceBundle.getString(parameter);
    }

    private void readParameters(InputStream is) {
        try {
            propertyResourceBundle = new PropertyResourceBundle(is);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String parameter;
        parameter = readParameter("TESTFILENAME");
        testfilename = parameter != null ? parameter : "TestFile.txt";
        parameter = readParameter("TESTFILEENCODING");
        testfileencoding = parameter != null ? parameter : "Cp1251";
        parameter = readParameter("TESTMODE");
        choicemode = false;
        testmode = LEARNING;
        if (parameter != null) if (parameter.equals("forchoice")) choicemode = true;
        else if (parameter.equals("testing")) testmode = TESTING;
        else if (parameter.equals("strong")) testmode = STRONG;
        parameter = readParameter("QUESTIONSMIXER");
        questionsmixer = parameter != null ? Boolean.valueOf(parameter).booleanValue() : false;
        parameter = readParameter("LANGUAGE");
        language = ENGLISH;
        if (parameter != null) if (parameter.equals("ru")) language = RUSSIAN;
        parameter = readParameter("FONTSIZE");
        fontsize = parameter != null ? Integer.parseInt(parameter) : getFont().getSize();
    }

    private void initCommonAWTComponents() {
        this.setBackground(Color.lightGray);
        this.setFont(font = new Font(getFont().getName(), getFont().getStyle(), fontsize));
        this.setLayout(new GridBagLayout());
        ActionListener al = new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                while (true) {
                    if (actionCommand.equals("test")) {
                        startTest();
                        break;
                    }
                    if (actionCommand.equals("prev")) {
                        prevButtonPushed();
                        break;
                    }
                    if (actionCommand.equals("next")) {
                        nextButtonPushed();
                        break;
                    }
                    if (actionCommand.equals("result")) {
                        formResult();
                        break;
                    }
                    break;
                }
            }
        };
        testButton = new JButton(resourceBundle.getString("button_test"));
        testButton.setActionCommand("test");
        testButton.addActionListener(al);
        prevButton = new JButton(resourceBundle.getString("button_prev"));
        prevButton.setActionCommand("prev");
        prevButton.addActionListener(al);
        nextButton = new JButton(resourceBundle.getString("button_next"));
        nextButton.setActionCommand("next");
        nextButton.addActionListener(al);
        resultButton = new JButton(resourceBundle.getString("button_result"));
        resultButton.setActionCommand("result");
        resultButton.addActionListener(al);
        greetingTextArea = new JTextArea();
        greetingTextArea.setEditable(false);
        answersPanel = new JPanel();
        answersPanel.setLayout(new GridBagLayout());
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 2, 2));
        buttonsPanel.add(prevButton);
        buttonsPanel.add(nextButton);
        notePanel = new JPanel();
        notePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        notePanel.add(noteLabel, c);

        questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        questionTextArea.setBackground(Color.white);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
    }

//    private void initTestAWTComponents() {
//        readResources();
//        initCommonAWTComponents();
//        GridBagConstraints c = new GridBagConstraints();
//        //Components
//        nameLabel = new JLabel(resourceBundle.getString("label_name"));
//        groupLabel = new JLabel(resourceBundle.getString("label_group"));
//        name.setBackground(Color.white);
//        group.setBackground(Color.white);
//        modeLabel = new JLabel(resourceBundle.getString("label_mode"));
//        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_learning"), LEARNING);
//        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_testing"), TESTING);
//        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_strong"), STRONG);
//        greetingTextArea.setText(resourceBundle.getString("textarea_greeting_test"));
//        answerTextField = new TextField[1];
//        //Panels
//        userPanel = new JPanel();
//        userPanel.setLayout(new GridBagLayout());
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(2, 2, 2, 2);
//        c.anchor = GridBagConstraints.WEST;
//        c.gridwidth = GridBagConstraints.RELATIVE;
//        userPanel.add(nameLabel, c);
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        userPanel.add(groupLabel, c);
//        c.gridwidth = GridBagConstraints.RELATIVE;
//        userPanel.add(name, c);
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        userPanel.add(group, c);
//        modePanel = new JPanel();
//        modePanel.setLayout(new GridBagLayout());
//        c.weightx = 0.0;
//        c.fill = GridBagConstraints.NONE;
//        c.gridwidth = GridBagConstraints.RELATIVE;
//        c.insets = new Insets(2, 2, 5, 0);
//        modePanel.add(modeLabel, c);
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.insets = new Insets(2, 0, 5, 2);
//        modePanel.add(modeChoice, c);
//        greetingPanel = new JPanel();
//        greetingPanel.setLayout(new GridBagLayout());
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.weightx = 1.0;
//        c.weighty = 1.0;
//        c.insets = new Insets(5, 5, 0, 5);
//        c.fill = GridBagConstraints.BOTH;
//        greetingPanel.add(greetingTextArea, c);
//        c = new GridBagConstraints();
//        c.ipadx = 10;
//        c.insets = new Insets(5, 0, 5, 0);
//        greetingPanel.add(testButton, c);
//        //Window
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.weightx = 1.0;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        this.add(userPanel, c);
//        this.add(modePanel, c);
//    }

    private void initEditorAWTComponents() {
        readResources();
        initCommonAWTComponents();
        ActionListener al = new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                while (true) {
                    if (actionCommand.equals("open")) {
                        openTest();
                        break;
                    }
                    if (actionCommand.equals("save")) {
                        saveTest();
                        break;
                    }
                    if (actionCommand.equals("new")) {
                        newEditor();
                        break;
                    }
                    if (actionCommand.equals("add")) {
                        addQuestion();
                        break;
                    }
                    if (actionCommand.equals("delete")) {
                        deleteQuestion();
                        break;
                    }
                    if (actionCommand.equals("edit")) {
                        editQuestion();
                        break;
                    }
                    if (actionCommand.equals("ok")) {
                        updateQuestion(true);
                        break;
                    }
                    if (actionCommand.equals("cancel")) {
                        updateQuestion(false);
                        break;
                    }
                    if (actionCommand.equals("increase")) {
                        addAnswer();
                        break;
                    }
                    if (actionCommand.equals("decrease")) {
                        deleteAnswer();
                        break;
                    }
                    break;
                }
            }
        };
        greetingTextArea.setText(resourceBundle.getString("textarea_greeting_edit"));
        //Buttons
        newButton = new JButton(resourceBundle.getString("button_new"));
        newButton.setActionCommand("new");
        newButton.addActionListener(al);
        openButton = new JButton(resourceBundle.getString("button_open"));
        openButton.setActionCommand("open");
        openButton.addActionListener(al);
        saveButton = new JButton(resourceBundle.getString("button_save"));
        saveButton.setActionCommand("save");
        saveButton.addActionListener(al);
        addButton = new JButton(resourceBundle.getString("button_add"));
        addButton.setActionCommand("add");
        addButton.addActionListener(al);
        deleteButton = new JButton(resourceBundle.getString("button_delete"));
        deleteButton.setActionCommand("delete");
        deleteButton.addActionListener(al);
        editButton = new JButton(resourceBundle.getString("button_edit"));
        editButton.setActionCommand("edit");
        editButton.addActionListener(al);
        okButton = new JButton(resourceBundle.getString("button_ok"));
        okButton.setActionCommand("ok");
        okButton.addActionListener(al);
        cancelButton = new JButton(resourceBundle.getString("button_cancel"));
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(al);
        incButton = new JButton(" + ");
        incButton.setActionCommand("increase");
        incButton.addActionListener(al);
        decButton = new JButton(" - ");
        decButton.setActionCommand("decrease");
        decButton.addActionListener(al);
        //Panels
        filePanel = new JPanel();
        filePanel.setLayout(new GridLayout(1, 3, 2, 2));
        filePanel.add(newButton);
        filePanel.add(openButton);
        filePanel.add(saveButton);
        actionsPanel = new JPanel();
        actionsPanel.setLayout(new GridLayout(1, 3, 2, 2));
        actionsPanel.add(addButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(editButton);
        incdecPanel = new JPanel();
        incdecPanel.setLayout(new GridLayout(1, 2, 2, 2));
        incdecPanel.add(incButton);
        incdecPanel.add(decButton);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(filePanel, c);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(answersPanel, c);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(actionsPanel, c);
        noteLabel.setText(resourceBundle.getString("label_note_incdecaction"));
        c.gridwidth = GridBagConstraints.RELATIVE;
        notePanel.add(noteLabel, c);
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        notePanel.add(incdecPanel, c);
    }

    private void prevButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current == questionset.size() - 1) {
            if (mode == TEST) buttonsPanel.remove(resultButton);
            nextButton.setEnabled(true);
        }
        if (--current == 0) prevButton.setEnabled(false);
        buttonsPanel.validate();
        setQuestion();
    }

    private void nextButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current++ == 0 && testmode != STRONG) prevButton.setEnabled(true);
        if (current == questionset.size() - 1) {
            nextButton.setEnabled(false);
            if (mode == TEST) buttonsPanel.add(resultButton);
        }
        buttonsPanel.validate();
        setQuestion();
    }

    private void formResult() { // TODO не работает
        obtainCurrentAnswer();
        int nCorrectAnswers = 0;
        for (int i = 0, n = questionset.size(); i < n; i++)
            if (((Question) questionset.elementAt(i)).isCorrect()) nCorrectAnswers++;
        StringBuffer message = new StringBuffer();
        message.append(resourceBundle.getString("label_name") + ": " + name.getText() + "\n");
        message.append(resourceBundle.getString("label_group") + ": " + group.getText() + "\n");
        message.append(resourceBundle.getString("label_questions") + " " + Integer.toString(questionset.size()) + "\n");
        message.append(resourceBundle.getString("label_correctanswers") + " " + Integer.toString(nCorrectAnswers));
        if (testmode == LEARNING) message.append("\n \n " + resourceBundle.getString("messagedialog_question") + " ");
        int actions = testmode == LEARNING ? MessageDialog.YES | MessageDialog.NO : MessageDialog.OK;
        MessageDialog md = new MessageDialog((Frame) getParent(), resourceBundle.getString("button_result"), new MultiLineLabel(message.toString(), MultiLineLabel.CENTER), actions);
        ActionListener al = new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                while (true) {
                    if (actionCommand.equals("ok")) {
                        newTest();
                        break;
                    }
                    if (actionCommand.equals("yes")) {
                        showcorrect = true;
                        startTest();
                        break;
                    }
                    if (actionCommand.equals("no")) {
                        newTest();
                        break;
                    }
                    break;
                }
            }
        };
        md.addActionListener(al);
        showMessageDialog(md);
    }

    private void showMessageDialog(MessageDialog dialog) {
        setEnabled(false);
        dialog.setFont(font);
        dialog.showCentred();
        setEnabled(true);
        dialog.dispose();
    }

    private int getQuestionType(Question question) {
        return question instanceof ExactQuestion ? EXACT :
                question instanceof ProperChoiceQuestion ? PROPER :
                        question instanceof TotalChoiceQuestion ? TOTAL : OTHER;
    }

    private void obtainCurrentAnswer() {
        Question currentquestion = (Question) questionset.elementAt(questionsorder[current]);
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                ((ExactQuestion) currentquestion).setAnswer(answerTextField[0].getText());
                break;
            case PROPER:
                for (int i = 0; i < checkboxes.length; i++)
                    if (checkboxes[i].getState()) {
                        ((ProperChoiceQuestion) currentquestion).setAnswer(i + 1);
                        break;
                    }
                break;
            case TOTAL:
                boolean[] answer = new boolean[checkboxes.length];
                for (int i = 0; i < checkboxes.length; i++) answer[i] = checkboxes[i].getState();
                ((TotalChoiceQuestion) currentquestion).setAnswer(answer);
                break;
        }
    }

    private void readTestFile(InputStream is) {
        try {
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, testfileencoding));
            StringBuffer question = new StringBuffer();
            Vector suggestedanswers = new Vector();
            String str;
            char delimiter;
            questionset = new Vector();
            while (lnr.ready()) {
                str = lnr.readLine().trim();
                delimiter = str.charAt(0);
                switch (delimiter) {
                    case '?':
                        question = new StringBuffer(str.substring(1));
                        break;
                    case '#':
                        suggestedanswers.addElement(str.substring(1));
                        break;
                    case '!':
                        if (suggestedanswers.size() == 0) {
                            String[] correctanswers = Utils.stringTokenizer(str.substring(1), "|");
                            for (int i = 0; i < correctanswers.length; i++)
                                correctanswers[i] = Utils.simplifyString(correctanswers[i]);
                            questionset.addElement(new ExactQuestion(question.toString(), correctanswers));
                        } else {
                            String[] correctanswers = Utils.stringTokenizer(str.substring(1), "&");
                            int[] nanswers = new int[correctanswers.length];
                            String[] sanswers = new String[suggestedanswers.size()];
                            for (int i = 0; i < suggestedanswers.size(); i++)
                                sanswers[i] = (String) suggestedanswers.elementAt(i);
                            for (int i = 0; i < correctanswers.length; i++)
                                nanswers[i] = Integer.parseInt(Utils.simplifyString(correctanswers[i]));
                            if (correctanswers.length == 1)
                                questionset.addElement(new ProperChoiceQuestion(question.toString(), sanswers, nanswers[0]));
                            else
                                questionset.addElement(new TotalChoiceQuestion(question.toString(), sanswers, nanswers));
                            suggestedanswers.removeAllElements();
                        }
                        break;
                    default:
                        question.append("\r\n" + str);
                }
            }
            lnr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void writeTestFile(FileOutputStream os) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, testfileencoding));
            Question currentquestion;
            String[] suggestedAnswers;
            for (int i = 0, n = questionset.size(); i < n; i++) {
                currentquestion = (Question) questionset.elementAt(i);
                bw.write("?" + currentquestion.getQuestion() + "\r\n");
                if (currentquestion instanceof ChoiceQuestion) {
                    suggestedAnswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
                    for (int j = 0; j < suggestedAnswers.length; j++) bw.write("#" + suggestedAnswers[j] + "\r\n");
                }
                bw.write("!" + currentquestion.getCorrectAnswer() + "\r\n");
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void newTest() {
        if (questionsmixer) questionsorder = Utils.randomize(questionset.size());
        else {
            questionsorder = new int[questionset.size()];
            for (int i = 0; i < questionset.size(); i++) questionsorder[i] = i;
        }
        for (int i = 0; i < questionset.size(); i++) ((Question) questionset.elementAt(i)).clearAnswer();
        name.setText("");
        name.setEditable(true);
        group.setText("");
        group.setEditable(true);
        modeChoice.select(testmode);
        modeChoice.setEnabled(choicemode);
        showcorrect = false;
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        if (this.isAncestorOf(answersPanel)) this.remove(answersPanel);
        if (this.isAncestorOf(buttonsPanel)) this.remove(buttonsPanel);
        if (buttonsPanel.isAncestorOf(resultButton)) buttonsPanel.remove(resultButton);
        this.add(greetingPanel, c);
    }

    private void startTest() {
        if (name.getText().equals("") | group.getText().equals("")) return;
        current = 0;
        name.setEditable(false);
        group.setEditable(false);
        modeChoice.setEnabled(false);
        prevButton.setEnabled(false);
        nextButton.setEnabled(true);
        if (choicemode) testmode = modeChoice.getSelectedIndex();
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        this.remove(greetingPanel);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        this.add(answersPanel, c);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(buttonsPanel, c);
        setQuestion();
        this.validate();
    }

    private void newEditor() {
        questionset = new Vector();
        current = 0;
        if (this.isAncestorOf(buttonsPanel)) this.remove(buttonsPanel);
        answersPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        answersPanel.add(greetingTextArea, c);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(buttonsPanel, c);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
    }

    private void startEditor() {
        saveButton.setEnabled(true);
        deleteButton.setEnabled(true);
        editButton.setEnabled(true);
        prevButton.setEnabled(false);
        nextButton.setEnabled(true);
        setQuestion();
        this.validate();
    }

    private void openTest() {
        FileDialog openFileDialog = new FileDialog((Frame) ((Window) this.getParent()).getOwner());
        openFileDialog.show();
        openFileDialog.dispose();
        String directory = openFileDialog.getDirectory(), file = openFileDialog.getFile();
        if (directory != null && file != null)
            try {
                readTestFile(new FileInputStream(directory + file));
                startEditor();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }

    private void saveTest() {
        FileDialog saveFileDialog = new FileDialog((Frame) ((Window) this.getParent()).getOwner());
        saveFileDialog.setMode(FileDialog.SAVE);
        saveFileDialog.show();
        saveFileDialog.dispose();
        String directory = saveFileDialog.getDirectory(), file = saveFileDialog.getFile();
        if (directory != null && file != null)
            try {
                writeTestFile(new FileOutputStream(directory + file));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }

    private void setQuestion() {
        switch (mode) {
            case TEST:
                setTestQuestion();
                break;
            case EDITOR:
                setEditorQuestion(false);
                break;
        }
    }

    private void setTestQuestion() {
        answersPanel.removeAll();
        Question currentquestion = (Question) questionset.elementAt(questionsorder[current]);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        questionLabel = new JLabel(resourceBundle.getString("label_question") + ": " + Integer.toString(current + 1));
        answersPanel.add(questionLabel, c);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        questionTextArea.setText(currentquestion.getQuestion());
        answersPanel.add(questionTextArea, c);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        answersPanel.add(answerLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                answerLabel.setText(resourceBundle.getString("label_answer") + ":");
                answerTextField[0] = new TextField(((ExactQuestion) currentquestion).getAnswer());
                answerTextField[0].setBackground(Color.white);
                answersPanel.add(answerTextField[0], c);
                noteLabel.setText(resourceBundle.getString("label_note") + " " + resourceBundle.getString("label_note_exactquestion"));
                break;
            default:
                answerLabel.setText(resourceBundle.getString("label_answers") + ":");
                String[] suggestedanswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
                checkboxGroup = new CheckboxGroup();
                checkboxes = new Checkbox[suggestedanswers.length];
                for (int i = 0; i < suggestedanswers.length; i++) {
                    c.weightx = 1.0;
                    c.gridwidth = GridBagConstraints.RELATIVE;
                    TextField textField = new TextField(suggestedanswers[i]);
                    textField.setEditable(false);
                    textField.setBackground(Color.white);
                    answersPanel.add(textField, c);
                    c.weightx = 0.0;
                    c.gridwidth = GridBagConstraints.REMAINDER;
                    switch (getQuestionType(currentquestion)) {
                        case PROPER:
                            checkboxes[i] = new Checkbox(Integer.toString(i + 1), false, checkboxGroup);
                            break;
                        case TOTAL:
                            checkboxes[i] = new Checkbox(Integer.toString(i + 1), ((TotalChoiceQuestion) currentquestion).getAnswer()[i]);
                            break;
                    }
                    answersPanel.add(checkboxes[i], c);
                }
                switch (getQuestionType(currentquestion)) {
                    case PROPER:
                        noteLabel.setText(resourceBundle.getString("label_note") + " " + resourceBundle.getString("label_note_properquestion"));
                        if (((ProperChoiceQuestion) currentquestion).getAnswer() != 0)
                            checkboxes[((ProperChoiceQuestion) currentquestion).getAnswer() - 1].setState(true);
                        break;
                    case TOTAL:
                        noteLabel.setText(resourceBundle.getString("label_note") + " " + resourceBundle.getString("label_note_totalquestion"));
                        break;
                }
                break;
        }
        if (showcorrect) {
            c.weightx = 0.0;
            c.fill = GridBagConstraints.NONE;
            correctanswerLabel.setText(resourceBundle.getString("label_correctanswer"));
            answersPanel.add(correctanswerLabel, c);
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            answerTextField[0] = new TextField(currentquestion.getCorrectAnswer());
            answerTextField[0].setEditable(false);
            answerTextField[0].setBackground(Color.white);
            answersPanel.add(answerTextField[0], c);
        }
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        answersPanel.add(notePanel, c);
        answersPanel.validate();
        answersPanel.repaint();
    }

    private void addQuestion() {
        String[] suggestedanswers = {new String()};
        int[] correctanswers = {1};
        CheckboxGroup cbg = new CheckboxGroup();
        Checkbox[] cbs = new Checkbox[3];
        MessageDialog md = new MessageDialog((Frame) getParent().getParent(), resourceBundle.getString("messagedialog_title"), new MultiLineLabel(resourceBundle.getString("messagedialog_note"), MultiLineLabel.CENTER), MessageDialog.OK);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        md.addExt(cbs[0] = new Checkbox(resourceBundle.getString("messagedialog_exactnote"), true, cbg), c);
        md.addExt(cbs[1] = new Checkbox(resourceBundle.getString("messagedialog_propernote"), false, cbg), c);
        md.addExt(cbs[2] = new Checkbox(resourceBundle.getString("messagedialog_totalnote"), false, cbg), c);
        showMessageDialog(md);
        int choice = 0;
        while (choice < 3) if (cbs[choice++].getState()) break;
        switch (choice) {
            case EXACT:
                questionset.insertElementAt(new ExactQuestion("", suggestedanswers), current);
                break;
            case PROPER:
                questionset.insertElementAt(new ProperChoiceQuestion("", suggestedanswers, correctanswers[0]), current);
                break;
            case TOTAL:
                questionset.insertElementAt(new TotalChoiceQuestion("", suggestedanswers, correctanswers), current);
                break;
        }
        editQuestion();
        if (questionset.size() - 2 == current) nextButton.setEnabled(true);
    }

    private void deleteQuestion() {
        questionset.removeElementAt(current);
        int nquestions = questionset.size();
        if (nquestions == 0) {
            newEditor();
            return;
        }
        if (current == nquestions) current--;
        setQuestion();
        this.validate();
    }

    private void editQuestion() {
        newButton.setEnabled(false);
        openButton.setEnabled(false);
        saveButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        buttonsPanel.remove(prevButton);
        buttonsPanel.remove(nextButton);
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.validate();
        setEditorQuestion(true);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        answersPanel.add(notePanel, c);
        answersPanel.validate();
    }

    private void updateQuestion(boolean update) {
        newButton.setEnabled(true);
        openButton.setEnabled(true);
        saveButton.setEnabled(true);
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
        editButton.setEnabled(true);
        buttonsPanel.remove(okButton);
        buttonsPanel.remove(cancelButton);
        buttonsPanel.add(prevButton);
        buttonsPanel.add(nextButton);
        buttonsPanel.validate();
        if (update) questionset.setElementAt(obtainCurrentQuestion(), current);
//  answersPanel.remove(notePanel);
        setEditorQuestion(false);
//  answersPanel.validate();
    }

    private Question obtainCurrentQuestion() {
        Question currentquestion = (Question) questionset.elementAt(current);
        String[] suggestedanswers = new String[answerTextField.length];
        for (int i = 0; i < suggestedanswers.length; i++) suggestedanswers[i] = answerTextField[i].getText();
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                currentquestion = new ExactQuestion(questionTextArea.getText(), suggestedanswers);
                break;
            case PROPER:
                int correctanswer = 0;
                for (int i = 0; i < checkboxes.length; i++)
                    if (checkboxes[i].getState()) {
                        correctanswer = i + 1;
                        break;
                    }
                currentquestion = new ProperChoiceQuestion(questionTextArea.getText(), suggestedanswers, correctanswer);
                break;
            case TOTAL:
                int n = 0;
                for (int i = 0; i < checkboxes.length; i++) if (checkboxes[i].getState()) n++;
                int[] correctanswers = new int[n];
                n = 0;
                for (int i = 0; i < checkboxes.length; i++) if (checkboxes[i].getState()) correctanswers[n++] = i + 1;
                currentquestion = new TotalChoiceQuestion(questionTextArea.getText(), suggestedanswers, correctanswers);
                break;
        }
        return currentquestion;
    }

    private void setEditorQuestion(boolean editable) {
        answersPanel.removeAll();
        Question currentquestion = (Question) questionset.elementAt(current);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        questionLabel = new JLabel(resourceBundle.getString("label_question") + ": " + Integer.toString(current + 1));
        answersPanel.add(questionLabel, c);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        questionTextArea.setText(currentquestion.getQuestion());
        questionTextArea.setEditable(editable);
        answersPanel.add(questionTextArea, c);
        c.weighty = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        answersPanel.add(answerLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        String[] suggestedanswers;
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                answerLabel.setText(resourceBundle.getString("label_answer") + ":");
                suggestedanswers = Utils.stringTokenizer(((ExactQuestion) currentquestion).getCorrectAnswer(), "|");
                decButton.setEnabled(suggestedanswers.length == 1 ? false : true);
                answerTextField = new TextField[suggestedanswers.length];
                for (int i = 0; i < suggestedanswers.length; i++) {
                    answerTextField[i] = new TextField(suggestedanswers[i].trim());
                    answerTextField[i].setEditable(editable);
                    answerTextField[i].setBackground(Color.white);
                    answersPanel.add(answerTextField[i], c);
                }
                break;
            default:
                answerLabel.setText(resourceBundle.getString("label_answers") + ":");
                suggestedanswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
                decButton.setEnabled(suggestedanswers.length == 1 ? false : true);
                answerTextField = new TextField[suggestedanswers.length];
                checkboxGroup = new CheckboxGroup();
                checkboxes = new Checkbox[suggestedanswers.length];
                for (int i = 0; i < suggestedanswers.length; i++) {
                    c.weightx = 1.0;
                    c.gridwidth = GridBagConstraints.RELATIVE;
                    answerTextField[i] = new TextField(suggestedanswers[i].trim());
                    answerTextField[i].setEditable(editable);
                    answerTextField[i].setBackground(Color.white);
                    answersPanel.add(answerTextField[i], c);
                    c.weightx = 0.0;
                    c.gridwidth = GridBagConstraints.REMAINDER;
                    switch (getQuestionType(currentquestion)) {
                        case PROPER:
                            checkboxes[i] = new Checkbox(Integer.toString(i + 1), (i + 1 == Integer.parseInt(currentquestion.getCorrectAnswer())), checkboxGroup);
                            break;
                        case TOTAL:
                            checkboxes[i] = new Checkbox(Integer.toString(i + 1), ((TotalChoiceQuestion) currentquestion).getCorrectAnswers()[i]);
                            break;
                    }
                    checkboxes[i].setEnabled(editable);
                    answersPanel.add(checkboxes[i], c);
                }
                if (getQuestionType(currentquestion) == PROPER)
                    if (((ProperChoiceQuestion) currentquestion).getAnswer() != 0)
                        checkboxes[((ProperChoiceQuestion) currentquestion).getAnswer() - 1].setState(true);
                break;
        }
        answersPanel.validate();
    }

    private void addAnswer() {
        if (answerTextField.length == 1) decButton.setEnabled(true);
        Question currentquestion = (Question) questionset.elementAt(current);
        TextField[] newanswerTextField = new TextField[answerTextField.length + 1];
        for (int i = 0; i < answerTextField.length; i++) newanswerTextField[i] = answerTextField[i];
        answerTextField = newanswerTextField;
        answerTextField[answerTextField.length - 1] = new TextField();
        answerTextField[answerTextField.length - 1].setEditable(true);
        answerTextField[answerTextField.length - 1].setBackground(Color.white);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                c.weightx = 1.0;
                c.gridwidth = GridBagConstraints.REMAINDER;
                answersPanel.add(answerTextField[answerTextField.length - 1], c, answersPanel.getComponentCount() - 1);
                break;
            default:
                Checkbox[] newcheckboxes = new Checkbox[checkboxes.length + 1];
                for (int i = 0; i < checkboxes.length; i++) newcheckboxes[i] = checkboxes[i];
                checkboxes = newcheckboxes;
                switch (getQuestionType(currentquestion)) {
                    case PROPER:
                        checkboxes[checkboxes.length - 1] = new Checkbox(Integer.toString(checkboxes.length), false, checkboxGroup);
                        break;
                    case TOTAL:
                        checkboxes[checkboxes.length - 1] = new Checkbox(Integer.toString(checkboxes.length), false);
                        break;
                }
                checkboxes[checkboxes.length - 1].setEnabled(true);
                c.weightx = 1.0;
                c.gridwidth = GridBagConstraints.RELATIVE;
                answersPanel.add(answerTextField[answerTextField.length - 1], c, answersPanel.getComponentCount() - 1);
                c.weightx = 0.0;
                c.gridwidth = GridBagConstraints.REMAINDER;
                answersPanel.add(checkboxes[checkboxes.length - 1], c, answersPanel.getComponentCount() - 1);
                break;
        }
        answersPanel.validate();
    }

    private void deleteAnswer() {
        if (answerTextField.length == 2) decButton.setEnabled(false);
        Question currentquestion = (Question) questionset.elementAt(current);
        TextField[] newanswerTextField = new TextField[answerTextField.length - 1];
        for (int i = 0; i < answerTextField.length - 1; i++) newanswerTextField[i] = answerTextField[i];
        switch (getQuestionType(currentquestion)) {
            case EXACT:
                answersPanel.remove(answerTextField[answerTextField.length - 1]);
                break;
            default:
                Checkbox[] newcheckboxes = new Checkbox[checkboxes.length - 1];
                for (int i = 0; i < checkboxes.length - 1; i++) newcheckboxes[i] = checkboxes[i];
                if (getQuestionType(currentquestion) == PROPER)
                    if (checkboxes[checkboxes.length - 1].getState())
                        newcheckboxes[newcheckboxes.length - 1].setState(true);
                answersPanel.remove(answerTextField[answerTextField.length - 1]);
                answersPanel.remove(checkboxes[checkboxes.length - 1]);
                checkboxes = newcheckboxes;
                break;
        }
        answerTextField = newanswerTextField;
        answersPanel.validate();
    }

}//class