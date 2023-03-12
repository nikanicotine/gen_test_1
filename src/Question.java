public abstract class Question {
    protected String question;

    protected Question(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public abstract String getCorrectAnswer();

    public abstract void clearAnswer();

    public abstract boolean isCorrect();
}