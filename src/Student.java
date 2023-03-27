//package org.pim.psu.studenttest;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Student extends JApplet { //TODO JApplet or JFrame or ???
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
    private JTextField[] answerTextField;
    private Choice modeChoice = new Choice();
    private JButton prevButton, nextButton, resultButton, testButton, openButton;
    private JTextArea greetingTextArea, questionTextArea;
    private JPanel userPanel, modePanel, greetingPanel,
            answersPanel, notePanel, buttonsPanel;
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

    private void UpdateWindow() {
        this.repaint();
    }

    public static void main(String[] args) {
        int width = 400, height = 500;
        JDialog Student = new JDialog(new JFrame(), "Test");
        Student test = new Student();
        Student.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        Student.setMinimumSize(new Dimension(300, 400));
//        Student.setSize(400, 500);
//        Student.setMaximumSize(new Dimension(800, 600)); // TODO ?
        Student.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        Student.add(test, c);
        Student.pack();
//        test.initEditor();
        test.initTest();
        Insets di = Student.getInsets();
        Student.setSize(di.left + width + di.right, di.top + height + di.bottom);
        Dimension ds = Student.getToolkit().getScreenSize(), dd = Student.getSize();
        Student.setLocation((ds.width - dd.width) / 2, (ds.height - dd.height) / 2);
        Student.setVisible(true);
//        Student.show();
    }

    public void init() {
        initTest();
    }

    public void initTest() {
        mode = TEST;
        try {
            readParameters(new FileInputStream(propertiesfilename));
            readTestFile();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        initTestAWTComponents();
        newTest();
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
//        this.setBackground(Color.lightGray); // TODO а зачем это вообще надо?
        this.setBackground(Color.yellow);
        this.setFont(font = new Font(getFont().getName(), getFont().getStyle(), fontsize));
        this.setLayout(new GridBagLayout());
        ActionListener al = new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                while (true) {
                    if (actionCommand.equals("start test")) {
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
        testButton.setActionCommand("start test");
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
//        greetingTextArea.setLineWrap(true); // TODO дает странныый "эффект"
//        greetingTextArea.setWrapStyleWord(true);
//        greetingTextArea.setMinimumSize();
        answersPanel = new JPanel();
        answersPanel.setLayout(new GridBagLayout());
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 3, 2, 2));
        buttonsPanel.add(prevButton);
        buttonsPanel.add(nextButton);
        notePanel = new JPanel();
        notePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
//        c.insets = new Insets(0, 5, 0, 5); //2222
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        notePanel.add(noteLabel, c);

        questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setBackground(Color.white); // белый по умолчанию
//        questionTextArea.setBorder(new LineBorder(Color.lightGray)); // это не надо
        questionTextArea.setMargin(new Insets(10, 10, 10, 10));

        UpdateWindow();
    }

    private void initTestAWTComponents() {
        readResources();
        initCommonAWTComponents();
        GridBagConstraints c = new GridBagConstraints();
        //Components
        nameLabel = new JLabel(resourceBundle.getString("label_name"));
        groupLabel = new JLabel(resourceBundle.getString("label_group"));
        name.setBackground(Color.white);
        group.setBackground(Color.white);
        modeLabel = new JLabel(resourceBundle.getString("label_mode"));
        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_learning"), LEARNING);
        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_testing"), TESTING);
        modeChoice.insert(" " + resourceBundle.getString("textfield_mode_strong"), STRONG);
        greetingTextArea.setText(resourceBundle.getString("textarea_greeting_test"));
        answerTextField = new JTextField[1];
        //Panels
        userPanel = new JPanel();
        userPanel.setLayout(new GridBagLayout());
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 0, 10);
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.RELATIVE;
        userPanel.add(nameLabel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        userPanel.add(groupLabel, c);
        c.gridwidth = GridBagConstraints.RELATIVE;
        userPanel.add(name, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        userPanel.add(group, c);
        modePanel = new JPanel();
        modePanel.setLayout(new GridBagLayout());
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.insets = new Insets(0, 10, 10, 0);
        modePanel.add(modeLabel, c);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.insets = new Insets(2, 0, 5, 2);
        c.insets = new Insets(0, 10, 10, 10);
        modePanel.add(modeChoice, c);

        greetingPanel = new JPanel();
        greetingPanel.setLayout(new GridBagLayout());
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
//        c.insets = new Insets(5, 5, 0, 5);
        c.insets = new Insets(5, 10, 0, 10);
        c.fill = GridBagConstraints.BOTH;
        greetingPanel.add(greetingTextArea, c);
        c = new GridBagConstraints();
        c.ipadx = 10;
        c.insets = new Insets(5, 0, 5, 0);
//        c.insets = new Insets(15, 0, 15, 0);
        greetingPanel.add(testButton, c);
        //Window
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(userPanel, c);
        this.add(modePanel, c);

        UpdateWindow();
    }

    private void prevButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current == questionset.size() - 1) {
            if (mode == TEST) buttonsPanel.remove(resultButton);
            nextButton.setEnabled(true);
        }
        if (--current == 0) prevButton.setEnabled(false);
        buttonsPanel.validate();
        setTestQuestion();

        UpdateWindow();
    }

    private void nextButtonPushed() {
        if (mode == TEST) obtainCurrentAnswer();
        if (current++ == 0 && testmode != STRONG) prevButton.setEnabled(true);
        if (current == questionset.size() - 1) {
            nextButton.setEnabled(false);
            if (mode == TEST) buttonsPanel.add(resultButton);
        }
        buttonsPanel.validate();
        setTestQuestion();

        UpdateWindow();
    }

    private void formResult() { // TODO не работает
        obtainCurrentAnswer();
        int nCorrectAnswers = 0;
        for (int i = 0, n = questionset.size(); i < n; i++)
            if (((Question) questionset.elementAt(i)).isCorrect()) nCorrectAnswers++;
        StringBuffer message = new StringBuffer();
        message.append(resourceBundle.getString("label_name")).append(": ").append(name.getText()).append("\n");
        message.append(resourceBundle.getString("label_group")).append(": ").append(group.getText()).append("\n");
        message.append(resourceBundle.getString("label_questions")).append(" ").append(Integer.toString(questionset.size())).append("\n");
        message.append(resourceBundle.getString("label_correctanswers")).append(" ").append(Integer.toString(nCorrectAnswers));
        if (testmode == LEARNING)
            message.append("\n \n ").append(resourceBundle.getString("messagedialog_question")).append(" ");
        int actions = testmode == LEARNING ? MessageDialog.YES | MessageDialog.NO : MessageDialog.OK;
        MessageDialog md = new MessageDialog(new JFrame(), resourceBundle.getString("button_result"), new MultiLineLabel(message.toString(), MultiLineLabel.CENTER), actions);
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

    private void readTestFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Выберите файл теста");
        fc.setFileFilter(new FileNameExtensionFilter("Binary Files", "bin"));
        fc.showOpenDialog(null);
        File f = fc.getSelectedFile();

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
                        question.append("\r\n" + str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        UpdateWindow();
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

        UpdateWindow();
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
        UpdateWindow();
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        this.add(answersPanel, c);
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(buttonsPanel, c);
        setTestQuestion();
        this.validate();
    }

    private void setTestQuestion() {
        answersPanel.removeAll();
        Question currentquestion = (Question) questionset.elementAt(questionsorder[current]);
        GridBagConstraints c = new GridBagConstraints();
//        c.insets = new Insets(10, 10, 5, 10); //2222
        c.insets = new Insets(2, 2, 2, 2); //2222
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
            answerTextField[0] = new JTextField(((ExactQuestion) currentquestion).getAnswer());
            answerTextField[0].setBackground(Color.white);
            answersPanel.add(answerTextField[0], c);
            noteLabel.setText(resourceBundle.getString("label_note") + " " + resourceBundle.getString("label_note_exactquestion"));
        } else {
            answerLabel.setText(resourceBundle.getString("label_answers") + ":");
            String[] suggestedanswers = ((ChoiceQuestion) currentquestion).getSuggestedAnswers();
            checkboxGroup = new CheckboxGroup();
            checkboxes = new Checkbox[suggestedanswers.length];
            for (int i = 0; i < suggestedanswers.length; i++) {
//                c.insets = new Insets(0, 5, 0, 5); //2222
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
        }
        if (showcorrect) {
            c.weightx = 0.0;
            c.fill = GridBagConstraints.NONE;
            correctanswerLabel.setText(resourceBundle.getString("label_correctanswer"));
            answersPanel.add(correctanswerLabel, c);
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            answerTextField[0] = new JTextField(currentquestion.getCorrectAnswer());
            answerTextField[0].setEditable(false);
            answerTextField[0].setBackground(Color.white);
            answersPanel.add(answerTextField[0], c);
        }
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        answersPanel.add(notePanel, c);
        answersPanel.validate();
        answersPanel.repaint();

        UpdateWindow();
    }

}//class