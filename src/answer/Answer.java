package answer;
public class Answer {
  private int answerId;
  private int questionId;
  private String contentAnswer;
  private boolean isCorrect;
  


  public int getAnswerId(){return answerId;}
  public void setAnswerId(int answerId){this.answerId=answerId;}


  public int getQuestionId(){return questionId;}
  public void setQuestionId(int questionId){ this.questionId=questionId;}


  public String getContentAnswer(){return contentAnswer;}
  public void setContentAnswer(String contentAnswer){this.contentAnswer=contentAnswer;}


  public boolean getIsCorrect(){return isCorrect;}
  public void setIsCorrect(boolean isCorrect){this.isCorrect=isCorrect;}

  


}
