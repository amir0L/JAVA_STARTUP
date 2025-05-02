package bulltein;
import matiere.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Bulltein {


  private int bullteinId;
  private int anneeScolaire;
  private int trimestre;
  private double noteBulltein;
  private int studentId;
  private  ArrayList<Matiere> matiers = new ArrayList<>();
  private ArrayList<Matiere> matiersFaibles = new ArrayList<>();

    
  
  public int getBullteinId(){return bullteinId;}
  public void setBullteinId(int bullteinId){this.bullteinId = bullteinId;}

  public int getAnneeScolaire(){return anneeScolaire;}
  public void setAnneeScolaire(int anneeScolaire){this.anneeScolaire=anneeScolaire;}


  public int getTrimestre(){return trimestre;}
  public void setTrimestre(int trimestre) {this.trimestre=trimestre;}


  public double getNoteBulltein(){return this.noteBulltein;}
  public void setNoteBulltein(double noteBulltein){this.noteBulltein=noteBulltein;}
  

  public int getStudentId(){return studentId;}
  public void setStudentId(int studentId){this.studentId = studentId;}


  public ArrayList<Matiere> getMatieres(){return matiers;}
  public void addMatiere(Matiere matiere){this.matiers.add(matiere);}

  

  private void InitMatierestable(){

    Matiere MATHEMATICS = new Matiere();
    MATHEMATICS.setNameMatiere("Mathematics");

    Matiere ARABIC = new Matiere();
    ARABIC.setNameMatiere("Arabic");

    Matiere FRENCH = new Matiere();
    FRENCH.setNameMatiere("French");


    Matiere ENGLISH = new Matiere();
    ENGLISH.setNameMatiere("English");


    Matiere HISTORY_GEOGRAPHY = new Matiere();
    HISTORY_GEOGRAPHY.setNameMatiere("History And Geography");


    Matiere PHYSICS = new Matiere();
    PHYSICS.setNameMatiere("Physics");

    Matiere NATURELLES_SCIENCES = new Matiere();
    NATURELLES_SCIENCES.setNameMatiere("Naturells Sciences");


    Matiere ISLAMIC_EDUCATION = new Matiere();
    ISLAMIC_EDUCATION.setNameMatiere("Islamic Education");
    
    Matiere CIVIVC_EDUCATION = new Matiere();
    CIVIVC_EDUCATION.setNameMatiere("Civic Education");

    Matiere TAMAZIGHT = new Matiere();
    TAMAZIGHT.setNameMatiere("Tamazight");
    
    Matiere INFORMATICS = new Matiere();
    INFORMATICS.setNameMatiere("Informatics");

    Matiere DRAWING = new Matiere();
    DRAWING.setNameMatiere("Drawing");


    Matiere MUSIC = new Matiere();
    MUSIC.setNameMatiere("Music");

        

    this.matiers.addAll(Arrays.asList(MATHEMATICS,ARABIC,FRENCH,ENGLISH,HISTORY_GEOGRAPHY,PHYSICS,NATURELLES_SCIENCES,ISLAMIC_EDUCATION,CIVIVC_EDUCATION,TAMAZIGHT,INFORMATICS,DRAWING,MUSIC));
}




  //function de saisir
  public void enterInfoBulltain(Scanner input) {
    InitMatierestable();
    System.out.println("enter information of bulltein");
    enterTrimestre(input);
    enterNoteBulltein(input);
    enterMatiersNotes(input);
    initTableMatieres();
    
    // Insert bulletin into database
    if (insertBullteinToDatabase()) {
        System.out.println("Bulletin saved successfully!");
    } else {
        System.out.println("Failed to save bulletin to database.");
    }
}
  //________
  public void initTableMatieres(){
    for (Matiere matiere : matiers) {
      matiere.setAnneeScolaire(anneeScolaire);
      matiere.setTrimestre(trimestre);
    }
  }




//functions to use with method "enterInfoBulltein"

  private void enterTrimestre(Scanner input){
    System.out.print("whiche trimestre are \n 1 ou 2 ou 3 \n _>");
    switch (input.nextInt()) {
      case 1:
        this.trimestre = 1;
        break;
      case 2: 
        this.trimestre=2;
        break;
      case 3 :
        this.trimestre=3;
        break;
      default:
      System.err.println("please enter again the correct value");
      enterTrimestre(input);
        break;
    }
  }

  private void enterNoteBulltein(Scanner input){
    do{
      System.out.print("please enter la note of bulltein \n _>");
      this.noteBulltein = input.nextDouble();
    }while( this.noteBulltein<=0 || this.noteBulltein>20);
    System.out.println(" the Note Bulltein  entred Success");  
  }

  public void enterMatiersNotes(Scanner input) {
    boolean notNulls ;
    
    System.out.println("please enter the Exams of  matiers of your "+trimestre+" trimestre");
    
    for(Matiere index : this.matiers){
      System.out.println("Matiere : "+index.getNameMatiere());

      double Devoir1;
      do {
        System.out.print("Note devoir1 : \n ->");
        Devoir1= input.nextDouble();
      } while (Devoir1<0 || Devoir1>20);
      index.setNoteDevoir1(Devoir1);

      double Devoir2;
      do {
        System.out.print("Note devoir2 : \n ->");
        Devoir2= input.nextDouble();
      } while (Devoir2 <0 || Devoir2>20);
      index.setNoteDevoir2(Devoir2);

      notNulls= Devoir1!= 0 && Devoir2 != 0;
        if (notNulls) {
          double Exam;
          do {
            System.out.print("enter Note Exam : \n ->");
            Exam = input.nextDouble();
          } while (Exam <0 || Exam >20);
            
            index.setNoteExam(Exam);
            
        }
    }
    
}







  public boolean insertBullteinToDatabase() {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        // Establish database connection
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false); // Start transaction
        
        // Insert into Bullteins table - bullteinId is auto-increment
        String sql = "INSERT INTO Bullteins (anneeScolaire, trimestre, noteBulltein, student_studentId) " +
                     "VALUES (?, ?, ?, ?)";
        pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, this.anneeScolaire);
        pstmt.setInt(2, this.trimestre);
        pstmt.setDouble(3, this.noteBulltein);
        pstmt.setInt(4, this.studentId);
        
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected == 0) {
            conn.rollback();
            System.out.println("Failed to create bulletin record");
            return false;
        }
        
        // Get the auto-generated bullteinId
        rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            this.bullteinId = rs.getInt(1);
        } else {
            conn.rollback();
            System.out.println("Failed to get generated bulletin ID");
            return false;
        }
        
        // Now insert all associated matieres
        boolean allMatieresInserted = insertMatieres(conn);
        if (!allMatieresInserted) {
            conn.rollback();
            System.out.println("Failed to insert matiere records");
            return false;
        }
        
        // Commit transaction
        conn.commit();
        System.out.println("Bulletin added successfully with ID: " + this.bullteinId);
        return true;
        
    } catch (SQLException e) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            System.out.println("Rollback failed: " + ex.getMessage());
        }
        System.out.println("Database error during bulletin insertion: " + e.getMessage());
        return false;
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}





/**
 * Helper method to insert matieres associated with this bulletin
 * @param conn Active database connection (should be in transaction mode)
 * @return true if all matieres were inserted successfully, false otherwise
 */
private boolean insertMatieres(Connection conn) throws SQLException {
  PreparedStatement pstmt = null;
  
  try {
      String sql = "INSERT INTO Matieres (bullteinId, anneeScolaire, trimestre, name, noteExam, noteDevoir1, noteDevoir2) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?)";
      pstmt = conn.prepareStatement(sql);
      
      for (Matiere matiere : matiers) {
          pstmt.setInt(1, this.bullteinId);
          pstmt.setInt(2, this.anneeScolaire);
          pstmt.setInt(3, this.trimestre);
          pstmt.setString(4, matiere.getNameMatiere());
          pstmt.setDouble(5, matiere.getNoteExam());
          pstmt.setDouble(6, matiere.getNoteDevoir1());
          pstmt.setDouble(7, matiere.getNoteDevoir2());
          
          int rowsAffected = pstmt.executeUpdate();
          if (rowsAffected == 0) {
              return false; // Failed to insert this matiere
          }
      }
      
      return true;
  } finally {
      if (pstmt != null) pstmt.close();
  }
}

// Database connection parameters - you need to add these to your Bulltein.java file
private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "4600"; // Change this to your actual MySQL password




public ArrayList<Matiere> identifyMatieresFaibles(double threshold) {
  // Clear existing weak subjects list to avoid duplicates
  this.matiersFaibles.clear();
  
  for (Matiere matiere : this.matiers) {
      double averageGrade = 0.0;
      
      // Check if the subject has valid grades to calculate average
      boolean hasValidGrades = matiere.getNoteDevoir1() != 0 || matiere.getNoteDevoir2() != 0 || matiere.getNoteExam() != 0;
      
      if (hasValidGrades) {
          // If both homework grades exist, use the standard formula
          if (matiere.getNoteDevoir1() != 0 && matiere.getNoteDevoir2() != 0 && matiere.getNoteExam() != 0) {
              averageGrade = (matiere.getNoteDevoir1() + matiere.getNoteDevoir2() + 2 * matiere.getNoteExam()) / 4;
          }
          
          // If only homework grades exist
          else if (matiere.getNoteDevoir1() != 0 && matiere.getNoteDevoir2() != 0 && matiere.getNoteExam() ==0) {
              averageGrade = (matiere.getNoteDevoir1() + matiere.getNoteDevoir2()) / 2;
          }
          else if (matiere.getNoteDevoir1() != 0) {
              averageGrade = matiere.getNoteDevoir1();
          }
          else if (matiere.getNoteDevoir2() != 0) {
              averageGrade = matiere.getNoteDevoir2();
          }
          
          // Add to weak subjects if below threshold
          if (averageGrade < threshold) {
              this.matiersFaibles.add(matiere);
          }
      }
  }
  
  return this.matiersFaibles;
}































@Override 
public String toString() {
    return "BullteinId : "+bullteinId+"\nAnnee Scolaire : "+anneeScolaire+"\nTrimestre : "+trimestre+"\nNote Bulltein : "+noteBulltein+"\nStudentId : "+studentId+"\nmatiers de bulltein :"+matiers;
}


}

