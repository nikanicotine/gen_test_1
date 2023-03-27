package javafiles;

public class TotalChoiceQuestion extends ChoiceQuestion {
    private boolean[] correctanswer;
    private boolean[] answer;

    public TotalChoiceQuestion(String question, String[] suggestedanswers, int[] correctanswer) {
        super(question, suggestedanswers);
        this.correctanswer = new boolean[suggestedanswers.length];
        clearAnswer();
        for (int j : correctanswer) this.correctanswer[j - 1] = true;
    }

    public String getCorrectAnswer() {
        StringBuffer correctanswer = new StringBuffer();
        for (int i = 0; i < this.correctanswer.length; i++)
            if (this.correctanswer[i]) correctanswer.append(i + 1).append(" & ");
        correctanswer.setLength(correctanswer.length() - 2);
        return correctanswer.toString();
    }

    public boolean[] getCorrectAnswers() {
        return correctanswer;
    }

    public void setCorrectAnswer(boolean[] correctanswer) {
        this.correctanswer = correctanswer;
    }

    public boolean[] getAnswer() {
        return answer;
    }

    public void setAnswer(boolean[] answer) {
        this.answer = answer;
    }

    public void clearAnswer() {
        answer = new boolean[suggestedanswers.length];
    }

    public boolean isCorrect() {
        boolean result = false;
        for (int i = 0; i < answer.length; i++) if (result |= answer[i] != correctanswer[i]) break;
        return !result;
    }
}