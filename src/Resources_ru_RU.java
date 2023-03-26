import java.util.ListResourceBundle;

public class Resources_ru_RU extends ListResourceBundle
{
    public Object[][] getContents(){return contents;}
    static final Object[][] contents=
            {
                    {"label_name","Фамилия"},
                    {"label_group","Группа"},
                    {"label_mode","Форма опроса:"},
                    {"textfield_mode_learning","обучение"},
                    {"textfield_mode_testing","тест"},
                    {"textfield_mode_strong","строгий опрос"},
                    {"label_question","Вопрос"},
                    {"label_answer","Ответ"},
                    {"label_answers","Варианты ответов"},
                    {"label_correctanswer","Правильный ответ:"},
                    {"label_questions","Вопросов:"},
                    {"label_correctanswers","Правильных ответов:"},
                    {"label_note","Примечание:"},
                    {"label_note_exactquestion","заполните текстовое поле"},
                    {"label_note_properquestion","выберите правильный вариант"},
                    {"label_note_totalquestion","выберите все(!) правильные варианты"},
                    {"label_note_incdecaction","Добавить/удалить вариант ответа ->"},
                    {"button_next","Далее >"},
                    {"button_prev","< Назад"},
                    {"button_result","Результат"},
                    {"button_test","Открыть тест"},
                    {"button_add","Добавить"},
                    {"button_delete","Удалить"},
                    {"button_edit","Правка"},
                    {"button_new","Новый тест"},
                    {"button_open","Открыть"},
                    {"button_save","Сохранить"},
                    {"button_ok","OK"},
                    {"button_cancel","Отмена"},
                    {"textarea_greeting_test","1. Введите фамилию и название группы"+"\n"+
                            "2. Нажмите кнопку \"Тест\""+"\n"+
                            "3. Выберите нужный вариант ответа"+"\n"+
                            "4. При помощи кнопок \"< Назад\" и \"Далее >\""+"\n"+
                            "    заполните предлагаемый тест"+"\n"+
                            "5. Вывод результатов - кнопка \"Результат\""},
                    {"textarea_greeting_edit","1. Кнопка \"Открыть\" - открытие существующего теста"+"\n"+
                            "2. Кнопка \"Сохранить\" - сохранение теста"+"\n"+
                            "3. Кнопка \"Новый\" - новый тест"+"\n"+
                            "4. При помощи кнопок \"< Назад\" и \"Далее >\""+"\n"+
                            "    заполните тест"+"\n"+
                            "5. Кнопка \"Добавить\" - добавление вопроса"+"\n"+
                            "6. Кнопка \"Удалить\" - удаление вопроса"+"\n"+
                            "7. Кнопка \"Правка\" - правка вопроса"},
                    {"messagedialog_question","Показать правильные ответы?"},
                    {"messagedialog_title","Добавление вопроса"},
                    {"messagedialog_note","Тип ответа:"},
                    {"messagedialog_exactnote"," - полный"},
                    {"messagedialog_propernote"," - выборочный"},
                    {"messagedialog_totalnote"," - сложный выбор"}
            };
}