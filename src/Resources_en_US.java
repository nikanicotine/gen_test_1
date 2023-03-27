import java.util.ListResourceBundle;

public class Resources_en_US extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    static final Object[][] contents =
            {
                    {"label_name", "Name"},
                    {"label_group", "Group"},
                    {"label_mode", "Test mode:"},
                    {"textfield_mode_learning", "learning"},
                    {"textfield_mode_testing", "testing"},
                    {"textfield_mode_strong", "strong testing"},
                    {"label_question", "Question"},
                    {"label_answer", "Answer"},
                    {"label_answers", "Answers"},
                    {"label_correctanswer", "Correct answer:"},
                    {"label_questions", "Questions:"},
                    {"label_correctanswers", "Correct answers:"},
                    {"label_note", "Note:"},
                    {"label_note_exactquestion", "input correct answer"},
                    {"label_note_properquestion", "choose correct answer"},
                    {"label_note_totalquestion", "choose all(!) correct answers"},
                    {"label_note_incdecaction", "Add/delete answer ->"},
                    {"button_next", "Next >"},
                    {"button_prev", "< Prev"},
                    {"button_result", "Result"},
                    {"button_test", "Start test"},
                    {"button_add", "Add"},
                    {"button_delete", "Delete"},
                    {"button_edit", "Edit"},
                    {"button_new", "New test"},
                    {"button_open", "Open"},
                    {"button_save", "Save"},
                    {"button_ok", "OK"},
                    {"button_cancel", "Cancel"},
                    {"textarea_greeting_test", "1. Enter your name and group number" + "\n" +
                            "2. Push \"Test\" button" + "\n" +
                            "3. Choose correct answer" + "\n" +
                            "4. Using \"< Prev\" and \"Next >\" buttons pass the test entirely" + "\n" +
                            "5. \"Result\" button lets watch out your results"},
                    {"textarea_greeting_edit", "1. Button \"Open\" - open existed test" + "\n" +
                            "2. Button \"Save\" - save test" + "\n" +
                            "3. Button \"New\" - new test" + "\n" +
                            "4. Using \"< Prev\" and \"Next >\" buttons pass the test entirely" + "\n" +
                            "5. Button \"Add\" - add question" + "\n" +
                            "6. Button \"Delete\" - delete question" + "\n" +
                            "7. Button \"Edit\" - edit question"},
                    {"messagedialog_question", "Would you like to see correct answers?"},
                    {"messagedialog_title", "Add question"},
                    {"messagedialog_note", "Answer type:"},
                    {"messagedialog_exactnote", " - detailed"},
                    {"messagedialog_propernote", " - choice"},
                    {"messagedialog_totalnote", " - complex choice"}
            };
}
