package question;
import java.util.ArrayList;

import answer.Answer;

public class Question {
  private int questionId;
  private int exerciceId;
  private String contentQuestion;
  private boolean type; //qcm = 1 ,V/F =0
  private int anneeScolaire;
  private int trimestre;
  private int unitNumber;
  private ArrayList<Answer> answers;
  


  public int getQuestionId(){return questionId;}
  public void setQuestionId(int questionId){this.questionId=questionId;}

  public int getExerciceId(){return exerciceId;}
  public void setExerciceId(int exerciceId){this.exerciceId=exerciceId;}

  public void  setContentQuestion(String contentQuestion){this.contentQuestion=contentQuestion;}
  public String getContentQuestion(){return contentQuestion;}

  public boolean getType(){return type;}
  public void setType(boolean type){this.type=type;}

  public int getAnneeScolaire(){return anneeScolaire;}
  public void setAnneeScolaire(int anneeScolaire){this.anneeScolaire=anneeScolaire;}

  public int getTrimestre(){return trimestre;}
  public void setTrimestre(int trimestre){this.trimestre=trimestre;}

  public int getUnitNumber(){return unitNumber;}
  public void setUnitNumber(int unitNumber){this.unitNumber=unitNumber;}

  
}
