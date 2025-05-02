package student;
import matiere.Matiere;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import bulltein.Bulltein;


public class Student {
  private int studentId;
  private String password;
  private int anneeScolaire =0;
  private int score;
  private String name;
  private String prenom;
  private String email;
  
  private Bulltein bulltein = new Bulltein();
  ArrayList <Matiere> matiereFaibles ;


  public int getStudentId(){return studentId;}
  public void setStudentId(int studentId){this.studentId = studentId;}

  public String getName() {return name;}
  public void setName(String name) {this.name = name;}

  public String getPrenom() {return prenom;}
  public void setPrenom(String prenom) {this.prenom = prenom;}

  public String getEmail(){return email;}
  public void setEmail(String email){this.email = email;}

  public String getPassword(){return password;}
  public void setPassword(String password){this.password = password;}

  public int getAnneeScolaire() {return anneeScolaire;}
  public void setAnneeScolaire(int anneeScolaire) {this.anneeScolaire = anneeScolaire;}


  public int getScore() {return score;}
  public void setScore(int score) {this.score = score;}

  public Bulltein getBulltein(){return this.bulltein;}
  

  /*public void enterStudentAndBulltein(Scanner input){
    enterInformationStudent(input);
    bulltein.setAnneeScolaire(this.anneeScolaire);
    bulltein.setStudentId(this.studentId);
    bulltein.enterInfoBulltain(input);
  }*/


  // Database connection parameters
  private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
  private static final String DB_USER = "root";
  private static final String DB_PASSWORD = "4600"; // Change this to your actual MySQL password




  public void enterStudentAndBulltein(Scanner input) {
    if (authenticate(input)) {
      bulltein.setAnneeScolaire(this.anneeScolaire);
      bulltein.setStudentId(this.studentId);
      bulltein.enterInfoBulltain(input);
    } else {
      System.out.println("Authentication failed. Cannot proceed with entering bulletin information.");
    }
  }





  
  public boolean enterInformationStudent(Scanner input){
      enterNameStudent(input);
      enterPrenomStudent(input);
      enterAnneeScolaire(input);
      enterEmailStudent(input);
      enterPasswordStudent(input);
      signup();
      enterStudentAndBulltein(input);
    return true;
  }
  
  



  private void enterNameStudent(Scanner input){
    do{
    System.out.print("-> enter the name of Student\n --> ");
      this.setName(input.nextLine());
    }while(getName() =="");
  }


  private void enterPrenomStudent(Scanner input){
    do{
      System.out.print("-> enter the prenom \n --> ");
      this.setPrenom(input.nextLine());
    }while(getPrenom() == "");
  }


  private void enterAnneeScolaire(Scanner input){
    System.out.println("-> enter the Annee Scolaire 1 2 3 4");
    this.setAnneeScolaire(input.nextInt());
    switch (anneeScolaire) {
      case 1:
        setAnneeScolaire(1);
        break;
      case 2:
        setAnneeScolaire(2);
        break;
      case 3:
        setAnneeScolaire(3);
        break;
      case 4: 
        setAnneeScolaire(4);
        break;
      default: 
        enterAnneeScolaire(input);
        break;  }
      }


  private void enterEmailStudent(Scanner input){
    String email;
    do {
      System.out.println("-> enter your email");
      email=input.nextLine();
      this.email =email;
    } while (this.email=="");
  }


  private void enterPasswordStudent(Scanner input){
    do {
      System.out.println("->enter the password of email");
      this.setPassword(input.nextLine());
    } while (this.getPassword() =="");
  }





  
  // Existing getters and setters...

  /**
   * Registers a new student in the database
   * First inserts into Students table with auto-increment ID
   * Then inserts into Infos table using the same ID
   * @return true if registration is successful, false otherwise
   */
  public boolean signup() {
    Connection conn = null;
    PreparedStatement pstmtStudent = null;
    PreparedStatement pstmtInfo = null;
    ResultSet rs = null;
    
    try {
      // Establish database connection
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      conn.setAutoCommit(false); // Start transaction
      
      // First insert into Students table - studentId is auto-increment
      String studentSql = "INSERT INTO Students (studentPassword, anneeScolaire, studentScore) VALUES (?, ?, ?)";
      pstmtStudent = conn.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS);
      pstmtStudent.setString(1, this.password);
      pstmtStudent.setInt(2, this.anneeScolaire);
      pstmtStudent.setInt(3, 0); // Initialize score to 0
      
      int studentRowsAffected = pstmtStudent.executeUpdate();
      if (studentRowsAffected == 0) {
        conn.rollback();
        System.out.println("Failed to create student record");
        return false;
      }
      
      // Get the auto-generated studentId
      rs = pstmtStudent.getGeneratedKeys();
      if (rs.next()) {
        this.studentId = rs.getInt(1);
      } else {
        conn.rollback();
        System.out.println("Failed to get generated student ID");
        return false;
      }
      
      // Now insert into Infos table using the studentId as infoId
      String infoSql = "INSERT INTO Infos (infoId, name, prenom, email) VALUES (?, ?, ?, ?)";
      pstmtInfo = conn.prepareStatement(infoSql);
      pstmtInfo.setInt(1, this.studentId); // Use the same ID for both tables
      pstmtInfo.setString(2, this.name);
      pstmtInfo.setString(3, this.prenom);
      pstmtInfo.setString(4, this.email);
      
      int infoRowsAffected = pstmtInfo.executeUpdate();
      if (infoRowsAffected == 0) {
        conn.rollback();
        System.out.println("Failed to create info record");
        return false;
      }
      
      // Update the Students record to set the Infos_infoId reference
      PreparedStatement pstmtUpdate = conn.prepareStatement(
        "UPDATE Students SET Infos_infoId = ? WHERE studentId = ?");
      pstmtUpdate.setInt(1, this.studentId); // Same ID used for both tables
      pstmtUpdate.setInt(2, this.studentId);
      
      int updateRowsAffected = pstmtUpdate.executeUpdate();
      if (updateRowsAffected == 0) {
        conn.rollback();
        System.out.println("Failed to update student with info reference");
        return false;
      }
      
      // Commit transaction
      conn.commit();
      System.out.println("Student registered successfully with ID: " + this.studentId);
      return true;
      
    } catch (SQLException e) {
      try {
        if (conn != null) conn.rollback();
      } catch (SQLException ex) {
        System.out.println("Rollback failed: " + ex.getMessage());
      }
      System.out.println("Database error during signup: " + e.getMessage());
      return false;
    } finally {
      try {
        if (rs != null) rs.close();
        if (pstmtStudent != null) pstmtStudent.close();
        if (pstmtInfo != null) pstmtInfo.close();
        if (conn != null) conn.close();
      } catch (SQLException e) {
        System.out.println("Error closing resources: " + e.getMessage());
      }
    }
  }




  /**
   * Authenticates a student with studentId and password
   * @param studentId The student's ID
   * @param password The student's password
   * @return true if login is successful, false otherwise
   */
  public boolean login(int studentId, String password) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
      // Establish database connection
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      
      // Query to check if student exists with given id and password
      String sql = "SELECT s.*, i.name, i.prenom, i.email FROM Students s " +
                "JOIN Infos i ON s.Infos_infoId = i.infoId " +
                "WHERE s.studentId = ? AND s.studentPassword = ?";
      
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, studentId);
      pstmt.setString(2, password);
      
      rs = pstmt.executeQuery();
      
      if (rs.next()) {
        // Login successful, populate student object with data from database
        this.studentId = rs.getInt("studentId");
        this.password = rs.getString("studentPassword");
        this.anneeScolaire = rs.getInt("anneeScolaire");
        this.score = rs.getInt("studentScore");
        this.name = rs.getString("name");
        this.prenom = rs.getString("prenom");
        this.email = rs.getString("email");
        
        System.out.println("Login successful! Welcome " + this.prenom + " " + this.name);
        return true;
      } else {
        System.out.println("Invalid student ID or password");
        return false;
      }
      
    } catch (SQLException e) {
      System.out.println("Database error during login: " + e.getMessage());
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
   * Method to handle user authentication - either login or signup based on user choice
   * @param input Scanner for reading user input
   * @return true if authentication is successful, false otherwise
   */
  public boolean authenticate(Scanner input) {
    System.out.println("1. Login");
    System.out.println("2. Signup");
    System.out.print("Enter your choice: ");
    
    int choice;
    try {
      choice = Integer.parseInt(input.nextLine());
    } catch (NumberFormatException e) {
      System.out.println("Invalid choice. Please enter 1 or 2.");
      return authenticate(input);
    }
    
    switch (choice) {
      case 1:
        System.out.print("Enter your student ID: ");
        int id;
        try {
          id = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
          System.out.println("Invalid student ID format.");
          return false;
        }
        
        System.out.print("Enter your password: ");
        String pwd = input.nextLine();
        
        return login(id, pwd);
        
      case 2:
        enterInformationStudent(input);
        return signup();
        
      default:
        System.out.println("Invalid choice. Please enter 1 or 2.");
        return authenticate(input);
    }
  }


  




















      @Override
  public String toString() {
      return"StudentId : "+getStudentId()+"\n"+"Name : "+getName()+"\n"+"Pernom : "+getPrenom()+"\n"+"Email : "+getEmail()+"\n"
      +"Password : "+getPassword()+"\n"+"Annee Scolaire : "+getAnneeScolaire()+"\n"+"Score : "+getScore();
  }

  }

