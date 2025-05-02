import java.util.Scanner;

import student.Student;

public class Main{
  

  
  
  
  public static void main(String[] args){
    Scanner input = new Scanner(System.in);

  // enter info student
  System.out.println("__________ enter inforamtion Student ON _________ ");



    Student test = new Student();
    test.enterStudentAndBulltein(input); // inputs : you have the inforamtion about student and bulltein
    System.out.println(test.toString());
    test.getBulltein().identifyMatieresFaibles(10); //the table have the objects now 
    //system to propose the exercice the user need to see the question and me think about the functions

  
  
  System.out.println("_______ enter information Student OFF ________ ");
  

  System.out.println("_________enter information Bulltien On__________");
  


  }
}