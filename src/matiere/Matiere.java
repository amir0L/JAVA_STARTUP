package matiere;
import exercice.Exercice;
import java.util.ArrayList;
import java.util.Scanner;


public class Matiere {
  private int matiereId;
  private int bullteinId;
  private int anneeScolaire;
  private int trimestre;
  private String nameMatiere;
  private double noteExam;
  private double noteDevoir1;
  private double noteDevoir2;
  private ArrayList<Exercice> exercices;
  //for devoir
  public Matiere(){
    this.nameMatiere=null;
    this.noteExam=0;
    this.noteDevoir1=0;
    this.noteDevoir2=0;
  }
  public Matiere(double noteExam){
    this.nameMatiere=null;
    this.noteExam=noteExam;
    this.noteDevoir1=0;
    this.noteDevoir2=0;
  }
  public Matiere(String nameMatiere){
    this.nameMatiere=nameMatiere;
  }

  public int getMatiereId(){return matiereId;}
  public void setMatiereId(int matiereId){this.matiereId=matiereId;}

  public int getBullteinId(){return bullteinId;}
  public void setBullteinId(int bulltienId){this.bullteinId= bulltienId;}

  public int getAnneeScolaire(){return anneeScolaire;}
  public void setAnneeScolaire(int anneeScolaire){this.anneeScolaire=anneeScolaire;}

  public int getTrimestre(){return trimestre;}
  public void setTrimestre(int trimestre){this.trimestre=trimestre;}


  public void setNameMatiere(String nameMatiere){this.nameMatiere=nameMatiere;}
  public String getNameMatiere(){return nameMatiere;}

  public void setNoteExam(double note){this.noteExam=note;}
  public double getNoteExam(){return noteExam;}

  public void setNoteDevoir1(double noteDevoir1){this.noteDevoir1=noteDevoir1;}
  public double getNoteDevoir1(){return noteDevoir1;}

  public void setNoteDevoir2(double noteDevoir2){this.noteDevoir2=noteDevoir2;}
  public double getNoteDevoir2(){return noteDevoir2;}

  public void addExercice(Exercice exo){
    exercices.add(exo);
  }

  public void display(){
    System.out.println("-----les exercices de"+nameMatiere);
    for(Exercice exo :exercices){System.out.println(exo);}
  }
  //functions de saisirs
  public void enterNoteExam(Scanner input){
    System.out.println("please enter the note of"+nameMatiere);
    this.noteExam=input.nextDouble();
  }
  public void enterNoteDevoir1(Scanner input){
    System.out.println("please enter the note of"+nameMatiere);
    this.noteDevoir1=input.nextDouble();
  }
  public void enterNoteDevoir2(Scanner input){
    System.out.println("please enter the note of"+nameMatiere);
    this.noteDevoir2=input.nextDouble();
  }
  public void enterNameMatiere(Scanner input){
    System.out.println("please enter the name of the matiere");
    this.nameMatiere=input.nextLine();
  }



}

