//import COM.PIM.JAVA.UTIL.*;

public class ExactQuestion extends Question {
    private String[] correctanswer;
    private String answer = new String();

    public ExactQuestion(String question, String[] correctanswer) {
        super(question);
        this.correctanswer = correctanswer;
    }

    public String getCorrectAnswer() {
        StringBuffer correctanswer = new StringBuffer();
        for (int i = 0; i < this.correctanswer.length; i++) correctanswer.append(this.correctanswer[i] + " | ");
        correctanswer.setLength(correctanswer.length() - 2);
        return correctanswer.toString();
    }

    public void setCorrectAnswer(String[] correctanswer) {
        this.correctanswer = correctanswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void clearAnswer() {
        answer = new String();
    }

    public boolean isCorrect() {
        boolean result = false;
        String answer = Utils.simplifyString(this.answer);
        for (int i = 0; i < correctanswer.length; i++) if (result |= answer.equals(correctanswer[i])) break;
        return result;
    }
}