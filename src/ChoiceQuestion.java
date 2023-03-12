public abstract class ChoiceQuestion extends Question {
    protected String[] suggestedanswers;

    protected ChoiceQuestion(String question, String[] suggestedanswers) {
        super(question);
        this.suggestedanswers = suggestedanswers;
    }

    public String[] getSuggestedAnswers() {
        return suggestedanswers;
    }
}