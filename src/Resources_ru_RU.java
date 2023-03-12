import java.util.ListResourceBundle;

public class Resources_ru_RU extends ListResourceBundle {
    public Object[][] getContents() {
        return contents;
    }

    static final Object[][] contents =
            {
                    {"label_name", "�������"},
                    {"label_group", "������"},
                    {"label_mode", "����� ������:"},
                    {"textfield_mode_learning", "��������"},
                    {"textfield_mode_testing", "����"},
                    {"textfield_mode_strong", "������� �����"},
                    {"label_question", "������"},
                    {"label_answer", "�����"},
                    {"label_answers", "�������� �������"},
                    {"label_correctanswer", "���������� �����:"},
                    {"label_questions", "��������:"},
                    {"label_correctanswers", "���������� �������:"},
                    {"label_note", "����������:"},
                    {"label_note_exactquestion", "��������� ��������� ����"},
                    {"label_note_properquestion", "�������� ���������� �������"},
                    {"label_note_totalquestion", "�������� ���(!) ���������� ��������"},
                    {"label_note_incdecaction", "��������/������� ������� ������ ->"},
                    {"button_next", "����� >"},
                    {"button_prev", "< �����"},
                    {"button_result", "���������"},
                    {"button_test", "����"},
                    {"button_add", "��������"},
                    {"button_delete", "�������"},
                    {"button_edit", "������"},
                    {"button_new", "����� ����"},
                    {"button_open", "�������"},
                    {"button_save", "���������"},
                    {"button_ok", "OK"},
                    {"button_cancel", "������"},
                    {"textarea_greeting_test", "1. ������� ������� � �������� ������" + "\n" +
                            "2. ������� ������ \"����\"" + "\n" +
                            "3. �������� ������ ������� ������" + "\n" +
                            "4. ��� ������ ������ \"< �����\" � \"����� >\"" + "\n" +
                            "    ��������� ������������ ����" + "\n" +
                            "5. ����� ����������� - ������ \"���������\""},
                    {"textarea_greeting_edit", "1. ������ \"�������\" - �������� ������������� �����" + "\n" +
                            "2. ������ \"���������\" - ���������� �����" + "\n" +
                            "3. ������ \"�����\" - ����� ����" + "\n" +
                            "4. ��� ������ ������ \"< �����\" � \"����� >\"" + "\n" +
                            "    ��������� ����" + "\n" +
                            "5. ������ \"��������\" - ���������� �������" + "\n" +
                            "6. ������ \"�������\" - �������� �������" + "\n" +
                            "7. ������ \"������\" - ������ �������"},
                    {"messagedialog_question", "�������� ���������� ������?"},
                    {"messagedialog_title", "���������� �������"},
                    {"messagedialog_note", "��� ������:"},
                    {"messagedialog_exactnote", " - ������"},
                    {"messagedialog_propernote", " - ����������"},
                    {"messagedialog_totalnote", " - ������� �����"}
            };
}