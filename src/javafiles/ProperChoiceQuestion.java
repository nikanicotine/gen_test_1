package javafiles;

public class ProperChoiceQuestion extends ChoiceQuestion {
    private int correctanswer;
    private int answer;

    public ProperChoiceQuestion(String question, String[] suggestedanswers, int correctanswer) {
        super(question, suggestedanswers);
        this.correctanswer = correctanswer;
    }

    public String getCorrectAnswer() {
        return new String("" + correctanswer);
    }

    public void setCorrectAnswer(int correctanswer) {
        this.correctanswer = correctanswer;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void clearAnswer() {
        answer = 0;
    }

    public boolean isCorrect() {
        return answer == correctanswer;
    }

}