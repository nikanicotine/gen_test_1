package javafiles;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Professor extends JDialog {
    //applet parameters
    private String testfilename, testfileencoding, propertiesfilename = "tests/Properties";
    private int testmode, language, fontsize, mode;
    private boolean questionsmixer, choicemode;

    private final int RUSSIAN = 1, ENGLISH = 2;
    private final int LEARNING = 0, TESTING = 1, STRONG = 2;
    private final int OTHER = 0, EXACT = 1, PROPER = 2, TOTAL = 3;
    private final int TEST = 0, EDITOR = 1;

    //SWING elements
    private JLabel questionLabel,
            answerLabel = new JLabel(),
            noteLabel = new JLabel(),
            correctanswerLabel = new JLabel();
    private JTextArea[] answerTextField;
    private Choice modeChoice = new Choice();
    private JButton prevButton, nextButton,
            addButton, deleteButton, editButton,
            newButton, openButton, saveButton,
            okButton, cancelButton, incButton, decButton;
    private JTextArea greetingTextArea, questionTextArea;
    private JPanel filePanel, answersPanel, notePanel, actionsPanel, buttonsPanel,
            incdecPanel;
    private JScrollPane scrollPane;
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

    private void ShowMsg(String s) {
        this.setVisible(true);
        JOptionPane.showMessageDialog(null, s);
        this.setVisible(true);
    }

    private void UpdateWindow() {
        this.repaint();
    }

    public static void main(String[] args) {
        int width = 434, height = 543;
//        JDialog professor = new JDialog(new JFrame(), "Question redactor");
        Professor professor = new Professor();
        professor.setTitle("Question redactor");
        professor.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        professor.setMinimumSize(new Dimension(300, 400));
        professor.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
//        professor.add(professor, c);
        professor.pack();
        professor.initEditor();
        Insets di = professor.getInsets();
        professor.setSize(di.left + width + di.right, di.top + height + di.bottom);
        Dimension ds = professor.getToolkit().getScreenSize(), dd = professor.getSize();
        professor.setLocation((ds.width - dd.width) / 2, (ds.height - dd.height) / 2);
        professor.setVisible(true);
    }

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
                resourceBundle = ResourceBundle.getBundle("javafiles.Resources", new Locale("ru", "RU"));
                break;
            case ENGLISH:
                resourceBundle = ResourceBundle.getBundle("javafiles.Resources", new Locale("en", "US"));
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
//        this.setBackground(Color.lightGray); // лучше всего без цвета, бозовый серый
        this.setFont(font = new Font(getFont().getName(), getFont().getStyle(), fontsize));
        this.setLayout(new GridBagLayout());
        ActionListener al = new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                while (true) {
                    if (actionCommand.equals("prev")) {
                        prevButtonPushed();
                        break;
                    }
                    if (actionCommand.equals("next")) {
                        nextButtonPushed();
                        break;
                    }
                    break;
                }
            }
        };
        prevButton = new JButton(resourceBundle.getString("button_prev"));
        prevButton.setActionCommand("prev");
        prevButton.addActionListener(al);

        nextButton = new JButton(resourceBundle.getString("button_next"));
        nextButton.setActionCommand("next");
        nextButton.addActionListener(al);

        greetingTextArea = new JTextArea();
        greetingTextArea.setEditable(false);
        greetingTextArea.setMargin(new Insets(10, 10, 10, 10));
        greetingTextArea.setLineWrap(true);
        greetingTextArea.setWrapStyleWord(true);

        answersPanel = new JPanel();
        answersPanel.setLayout(new GridBagLayout());

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 2, 2)); // TODO
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
        questionTextArea.setMargin(new Insets(10, 10, 10, 10));

        UpdateWindow();
    }

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
        c.insets = new Insets(10, 10, 5, 10);//2222
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

        UpdateWindow();
    }

    private void prevButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current == questionset.size() - 1) nextButton.setEnabled(true);
        if (--current == 0) prevButton.setEnabled(false);
        buttonsPanel.validate();
        setQuestion();

        UpdateWindow();
    }

    private void nextButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current++ == 0 && testmode != STRONG) prevButton.setEnabled(true);
        if (current == questionset.size() - 1) nextButton.setEnabled(false);
        buttonsPanel.validate();
        setQuestion();

        UpdateWindow();
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


    private void newEditor() {
        questionset = new Vector();
        current = 0;
        if (this.isAncestorOf(buttonsPanel)) this.remove(buttonsPanel);
        answersPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 10, 0); //TODO нижняя норм при 0 10 10 10
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        answersPanel.add(greetingTextArea, c);
        c.insets = new Insets(0, 10, 10, 10);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(buttonsPanel, c);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        UpdateWindow();
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
        newEditor();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("../tests/Properties"));
        fc.setFileFilter(new FileNameExtensionFilter("Binary Files", "bin"));
        fc.showOpenDialog(null);
        File f = fc.getSelectedFile();
        if (f == null) {
            ShowMsg("Вы не выбрали тест!");
            return;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            ArrayList<String> arr = (ArrayList<String>) ois.readObject();
            ois.close();

            StringBuffer question = new StringBuffer();
            Vector suggestedanswers = new Vector();
            char delimiter;
            questionset = new Vector();

            for (String str : arr) {
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
                        question.append("\r").append(str);
                }
            }
            startEditor();
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
            ShowMsg("Ошибка при загрузке");
        }
        UpdateWindow();
    }

    private void saveTest() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("../tests/Properties"));
        fc.setDialogTitle("Сохранение теста");
        fc.setFileFilter(new FileNameExtensionFilter("Binary Files", "bin"));
        fc.showSaveDialog(null);
        File f = fc.getSelectedFile();
        if (f == null) {
            ShowMsg("Вы не сохранили тест!");
            return;
        }
        ArrayList<String> arr = new ArrayList<String>();

        Question currentquestion;
        String[] suggestedAnswers;

        for (int i = 0, n = questionset.size(); i < n; i++) {
            currentquestion = (Question) questionset.elementAt(i);
            arr.add("?" + currentquestion.getQuestion() + "\r");
            if (currentquestion instanceof ChoiceQuestion) {
                suggestedAnswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
                for (String suggestedAnswer : suggestedAnswers) arr.add("#" + suggestedAnswer + "\r");
            }
            arr.add("!" + currentquestion.getCorrectAnswer() + "\r");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
            oos.writeObject(arr);
            oos.close();
            ShowMsg("Сохранение прошло успешно");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            ShowMsg("Ошибка при сохранении");
        }
        UpdateWindow();
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
        UpdateWindow();
    }

    private void setTestQuestion() {
        answersPanel.removeAll();
        Question currentquestion = (Question) questionset.elementAt(questionsorder[current]);
        GridBagConstraints c = new GridBagConstraints();
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
        if (getQuestionType(currentquestion) == EXACT) {
            answerLabel.setText(resourceBundle.getString("label_answer") + ":");
            answerTextField[0] = new JTextArea(((ExactQuestion) currentquestion).getAnswer());
            answerTextField[0].setBackground(Color.white);
            answersPanel.add(answerTextField[0], c);
            noteLabel.setText(resourceBundle.getString("label_note") + " " + resourceBundle.getString("label_note_exactquestion"));
        } else {
            answerLabel.setText(resourceBundle.getString("label_answers") + ":");
            String[] suggestedanswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
            checkboxGroup = new CheckboxGroup();
            checkboxes = new Checkbox[suggestedanswers.length];
            for (int i = 0; i < suggestedanswers.length; i++) {
                c.weightx = 1.0;
                c.gridwidth = GridBagConstraints.RELATIVE;
                JTextField textField = new JTextField(suggestedanswers[i]);
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
        }

        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        answersPanel.add(notePanel, c);
        answersPanel.validate();
        answersPanel.repaint();

        UpdateWindow();
    }

    private void addQuestion() {
        String[] suggestedanswers = {new String()};
        int[] correctanswers = {1};
        CheckboxGroup cbg = new CheckboxGroup();
        Checkbox[] cbs = new Checkbox[3];
        MessageDialog md = new MessageDialog(new JFrame(), resourceBundle.getString("messagedialog_title"), new MultiLineLabel(resourceBundle.getString("messagedialog_note"), MultiLineLabel.CENTER), MessageDialog.OK);
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

        UpdateWindow();
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

        UpdateWindow();
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

        UpdateWindow();
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
        setEditorQuestion(false);

        UpdateWindow();
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
                for (Checkbox checkbox : checkboxes) if (checkbox.getState()) n++;
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
        c.insets = new Insets(2, 0, 0, 0); //2222
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        questionLabel = new JLabel(resourceBundle.getString("label_question") + ": " + Integer.toString(current + 1));
        answersPanel.add(questionLabel, c);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        questionTextArea.setText(currentquestion.getQuestion());
        questionTextArea.setEditable(editable);
        scrollPane = new JScrollPane(questionTextArea);
        answersPanel.add(scrollPane, c);
        c.weighty = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        answersPanel.add(answerLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        String[] suggestedanswers;
        if (getQuestionType(currentquestion) == EXACT) {
            answerLabel.setText(resourceBundle.getString("label_answer") + ":");
            suggestedanswers = Utils.stringTokenizer(currentquestion.getCorrectAnswer(), "|");
            decButton.setEnabled(suggestedanswers.length != 1);
            answerTextField = new JTextArea[suggestedanswers.length];
            for (int i = 0; i < suggestedanswers.length; i++) {
                answerTextField[i] = new JTextArea(suggestedanswers[i].trim());
                answerTextField[i].setEditable(editable);
                answerTextField[i].setBackground(Color.white);
                answerTextField[i].setLineWrap(true);
                answerTextField[i].setWrapStyleWord(true);
                answerTextField[i].setMargin(new Insets(5, 10, 5, 10));
                answersPanel.add(answerTextField[i], c);
            }
        } else {
            answerLabel.setText(resourceBundle.getString("label_answers") + ":");
            suggestedanswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
            decButton.setEnabled(suggestedanswers.length != 1);
            answerTextField = new JTextArea[suggestedanswers.length];
            checkboxGroup = new CheckboxGroup();
            checkboxes = new Checkbox[suggestedanswers.length];
            for (int i = 0; i < suggestedanswers.length; i++) {
                c.weightx = 1.0;
                c.gridwidth = GridBagConstraints.RELATIVE;
                answerTextField[i] = new JTextArea(suggestedanswers[i].trim());
                answerTextField[i].setEditable(editable);
                answerTextField[i].setBackground(Color.white);
                answerTextField[i].setLineWrap(true);
                answerTextField[i].setWrapStyleWord(true);
                answerTextField[i].setMargin(new Insets(5, 10, 5, 10));
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
        }
        answersPanel.validate();
        UpdateWindow();
    }

    private void addAnswer() {
        if (answerTextField.length == 1) decButton.setEnabled(true);
        Question currentquestion = (Question) questionset.elementAt(current);
        JTextArea[] newanswerTextField = new JTextArea[answerTextField.length + 1];
        System.arraycopy(answerTextField, 0, newanswerTextField, 0, answerTextField.length);
        answerTextField = newanswerTextField;
        answerTextField[answerTextField.length - 1] = new JTextArea();
        answerTextField[answerTextField.length - 1].setEditable(true);
        answerTextField[answerTextField.length - 1].setBackground(Color.white);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 0, 0, 0); // 2222
        c.fill = GridBagConstraints.HORIZONTAL;
        if (getQuestionType(currentquestion) == EXACT) {
            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;
            answersPanel.add(answerTextField[answerTextField.length - 1], c, answersPanel.getComponentCount() - 1);
        } else {
            Checkbox[] newcheckboxes = new Checkbox[checkboxes.length + 1];
            System.arraycopy(checkboxes, 0, newcheckboxes, 0, checkboxes.length);
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
        }
        answersPanel.validate();
        UpdateWindow();
    }

    private void deleteAnswer() {
        if (answerTextField.length == 2) decButton.setEnabled(false);
        Question currentquestion = (Question) questionset.elementAt(current);
        JTextArea[] newanswerTextField = new JTextArea[answerTextField.length - 1];
        System.arraycopy(answerTextField, 0, newanswerTextField, 0, answerTextField.length - 1);
        if (getQuestionType(currentquestion) == EXACT) {
            answersPanel.remove(answerTextField[answerTextField.length - 1]);
        } else {
            Checkbox[] newcheckboxes = new Checkbox[checkboxes.length - 1];
            System.arraycopy(checkboxes, 0, newcheckboxes, 0, checkboxes.length - 1);
            if (getQuestionType(currentquestion) == PROPER)
                if (checkboxes[checkboxes.length - 1].getState())
                    newcheckboxes[newcheckboxes.length - 1].setState(true);
            answersPanel.remove(answerTextField[answerTextField.length - 1]);
            answersPanel.remove(checkboxes[checkboxes.length - 1]);
            checkboxes = newcheckboxes;
        }
        answerTextField = newanswerTextField;
        answersPanel.validate();
        UpdateWindow();
    }
}//class