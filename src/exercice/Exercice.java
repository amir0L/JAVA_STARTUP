package exercice;
import java.util.ArrayList;

import question.Question;

public class Exercice {
  private int exerciceId;
  private String matiereName;
  private int anneeScolaire;
  private int trimestre;
  private int unitNumber;  //chaptier =1 on pose 4 exercice pour le
  private int scoreExo;
  private int levlExo;
  private ArrayList <Question> questions;
  
  
  
  public int getExerciceId(){return exerciceId;}
  public void setExerciceId(int exerciceId){this.exerciceId= exerciceId;}

  public String getMatiereName(){return matiereName;}
  public void setMatiereName(String matiereName){this.matiereName= matiereName;}

  public int getAnneeScolaire(){return anneeScolaire;}
  public void setAnneeScolaire(int anneeScolaire){this.anneeScolaire=anneeScolaire;}

  public int getTrimestre(){return trimestre;}
  public void setTrimestre(int trimestre){this.trimestre=trimestre;}


  public int getUnitNumber(){return unitNumber;}
  public void setUnitNumber(int unitNumber){this.unitNumber=unitNumber;}

  public int getScoreExo(){return scoreExo;}
  public void setScoreExo(int scoreExo){this.scoreExo=scoreExo;}
  
  public void setLevlExo (int levlExo){
    switch (levlExo) {
      case 1:
        this.levlExo=1;
        break;
      case 2:
      this.levlExo=2;
        break;
      case 3:
      this.levlExo=3;
        break;
      default:
      this.levlExo=0;
        break;
    }
  }
  public int showLevlsExo(){
    switch (levlExo) {
      case 1:
        System.out.println("beginner");
        break;
      case 2:
        System.out.println("medium");
        break;
      case 3:
        System.out.println("hard");
        break;
      default:
      System.out.println("not exist levl");
        break;
    }
    return this.levlExo;
  }



  

}
