import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import answer.Answer;
import answer.AnswerTF;
import exercice.Exercice;
import matiere.Matiere;
import question.Question;
import student.Student;
import bulltein.Bulltein;

public class QuizGenerator {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "4600";
    
    /**
     * Generate quizzes for subjects where a student is performing poorly
     * @param student The student for whom to generate quizzes
     * @param threshold The grade threshold below which a subject is considered weak
     * @return A list of quizzes organized by subject
     */
    public static List<SubjectQuiz> proposeQuizzesForWeakSubjects(Student student, double threshold) {
        List<SubjectQuiz> proposedQuizzes = new ArrayList<>();
        Connection conn = null;
        
        try {
            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Get student's bulletin
            Bulltein bulletin = student.getBulltein();
            
            // Identify weak subjects
            ArrayList<Matiere> weakSubjects = bulletin.identifyMatieresFaibles(threshold);
            
            if (weakSubjects.isEmpty()) {
                System.out.println("No weak subjects identified for the student.");
                return proposedQuizzes;
            }
            
            // For each weak subject, find appropriate exercises and questions
            for (Matiere weakSubject : weakSubjects) {
                SubjectQuiz subjectQuiz = new SubjectQuiz(weakSubject.getNameMatiere());
                
                // Find exercises for this subject
                List<Exercice> exercises = findExercisesForSubject(
                    conn, 
                    weakSubject.getNameMatiere(), 
                    student.getAnneeScolaire(), 
                    bulletin.getTrimestre()
                );
                
                // For each exercise, get questions and answers
                for (Exercice exercise : exercises) {
                    ExerciseQuiz exerciseQuiz = new ExerciseQuiz(exercise.getExerciceId(), exercise.getUnitNumber(), exercise.showLevlsExo());
                    
                    // Get questions for this exercise
                    List<Question> questions = getQuestionsForExercise(
                        conn, 
                        exercise.getExerciceId(),
                        exercise.getAnneeScolaire(),
                        exercise.getTrimestre(),
                        exercise.getUnitNumber()
                    );
                    
                    // For each question, get answers
                    for (Question question : questions) {
                        QuestionWithAnswers questionWithAnswers = new QuestionWithAnswers(
                            question.getQuestionId(),
                            question.getContentQuestion(),
                            question.getType()
                        );
                        
                        // Get appropriate answers based on question type
                        if (question.getType()) { // QCM type question
                            questionWithAnswers.setAnswers(getAnswersForQuestion(conn, question.getQuestionId()));
                        } else { // True/False type question
                            questionWithAnswers.setTfAnswer(getTFAnswerForQuestion(conn, question.getQuestionId()));
                        }
                        
                        exerciseQuiz.addQuestion(questionWithAnswers);
                    }
                    
                    subjectQuiz.addExercise(exerciseQuiz);
                }
                
                proposedQuizzes.add(subjectQuiz);
            }
            
        } catch (SQLException e) {
            System.out.println("Database error while generating quizzes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
        
        return proposedQuizzes;
    }
    
    /**
     * Find exercises for a specific subject based on school year and trimester
     */
    private static List<Exercice> findExercisesForSubject(Connection conn, String matiereName, int anneeScolaire, int trimestre) throws SQLException {
        List<Exercice> exercises = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT exerciceId, unitNumber, scoreExo, levlExo FROM Exercices " +
                        "WHERE matiereName = ? AND anneeScolaire = ? AND trimestre = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, matiereName);
            pstmt.setInt(2, anneeScolaire);
            pstmt.setInt(3, trimestre);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Exercice exercise = new Exercice();
                exercise.setExerciceId(rs.getInt("exerciceId"));
                exercise.setMatiereName(matiereName);
                exercise.setAnneeScolaire(anneeScolaire);
                exercise.setTrimestre(trimestre);
                exercise.setUnitNumber(rs.getInt("unitNumber"));
                exercise.setScoreExo(rs.getInt("scoreExo"));
                exercise.setLevlExo(rs.getInt("levlExo"));
                
                exercises.add(exercise);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return exercises;
    }
    
    /**
     * Get questions for a specific exercise
     */
    private static List<Question> getQuestionsForExercise(Connection conn, int exerciceId, int anneeScolaire, int trimestre, int unitNumber) throws SQLException {
        List<Question> questions = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT questionId, contentQuestion, type FROM Questions " +
                        "WHERE exerciceId = ? AND anneeScolaire = ? AND trimestre = ? AND unitNumber = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, exerciceId);
            pstmt.setInt(2, anneeScolaire);
            pstmt.setInt(3, trimestre);
            pstmt.setInt(4, unitNumber);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question();
                question.setQuestionId(rs.getInt("questionId"));
                question.setExerciceId(exerciceId);
                question.setContentQuestion(rs.getString("contentQuestion"));
                question.setType(rs.getBoolean("type"));
                question.setAnneeScolaire(anneeScolaire);
                question.setTrimestre(trimestre);
                question.setUnitNumber(unitNumber);
                
                questions.add(question);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return questions;
    }
    
    /**
     * Get multiple-choice answers for a question
     */
    private static List<Answer> getAnswersForQuestion(Connection conn, int questionId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT answerId, contentAnswer, isCorrect FROM Answers WHERE questionId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Answer answer = new Answer();
                answer.setAnswerId(rs.getInt("answerId"));
                answer.setQuestionId(questionId);
                answer.setContentAnswer(rs.getString("contentAnswer"));
                answer.setIsCorrect(rs.getBoolean("isCorrect"));
                
                answers.add(answer);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return answers;
    }
    
    /**
     * Get true/false answer for a question
     */
    private static AnswerTF getTFAnswerForQuestion(Connection conn, int questionId) throws SQLException {
        AnswerTF answer = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT answerTfId, isTrue FROM answerTF WHERE question_questionId = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                answer = new AnswerTF();
                answer.setAnswerTfId(rs.getInt("answerTfId"));
                answer.setQuestionId(questionId);
                answer.setIsTrue(rs.getBoolean("isTrue"));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return answer;
    }
    
    /**
     * Present a quiz to a student and record their answers/score
     */
    public static void presentQuiz(SubjectQuiz subjectQuiz, Scanner scanner) {
        System.out.println("\n==== QUIZ FOR " + subjectQuiz.getSubjectName() + " ====\n");
        int totalQuestions = 0;
        int correctAnswers = 0;
        
        for (ExerciseQuiz exerciseQuiz : subjectQuiz.getExercises()) {
            System.out.println("--- Exercise (Unit " + exerciseQuiz.getUnitNumber() + 
                              ", Level " + exerciseQuiz.getLevel() + ") ---");
            
            for (QuestionWithAnswers q : exerciseQuiz.getQuestions()) {
                totalQuestions++;
                System.out.println("\nQuestion " + totalQuestions + ": " + q.getQuestionText());
                
                boolean isCorrect = false;
                
                if (q.isMultipleChoice()) {
                    // Handle multiple choice question
                    List<Answer> answers = q.getAnswers();
                    for (int i = 0; i < answers.size(); i++) {
                        System.out.println((i + 1) + ". " + answers.get(i).getContentAnswer());
                    }
                    
                    System.out.print("Your answer (1-" + answers.size() + "): ");
                    int userAnswer = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    
                    if (userAnswer > 0 && userAnswer <= answers.size()) {
                        isCorrect = answers.get(userAnswer - 1).getIsCorrect();
                    }
                } else {
                    // Handle true/false question
                    System.out.println("1. True");
                    System.out.println("2. False");
                    
                    System.out.print("Your answer (1 for True, 2 for False): ");
                    int userAnswer = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    
                    boolean userBoolAnswer = (userAnswer == 1);
                    isCorrect = (userBoolAnswer == q.getTfAnswer().getIsTrue());
                }
                
                if (isCorrect) {
                    System.out.println("Correct!");
                    correctAnswers++;
                } else {
                    System.out.println("Incorrect.");
                }
            }
        }
        
        // Display quiz results
        double score = (double) correctAnswers / totalQuestions * 100;
        System.out.println("\n==== QUIZ RESULTS ====");
        System.out.println("Subject: " + subjectQuiz.getSubjectName());
        System.out.println("Correct answers: " + correctAnswers + " out of " + totalQuestions);
        System.out.println("Score: " + String.format("%.2f", score) + "%");
        
        if (score >= 80) {
            System.out.println("Excellent! You've mastered this subject.");
        } else if (score >= 60) {
            System.out.println("Good job! Keep practicing to improve further.");
        } else {
            System.out.println("You should continue studying this subject to improve your understanding.");
        }
    }
    
    /**
     * Main method to propose quizzes for a specific student's weak subjects
     */
    public static void proposeQuizzesForStudent(Student student, double threshold, Scanner scanner) {
        System.out.println("Analyzing student performance to identify weak subjects...");
        List<SubjectQuiz> quizzes = proposeQuizzesForWeakSubjects(student, threshold);
        
        if (quizzes.isEmpty()) {
            System.out.println("No quizzes available for weak subjects at this time.");
            return;
        }
        
        System.out.println("\nQuizzes available for the following weak subjects:");
        for (int i = 0; i < quizzes.size(); i++) {
            System.out.println((i + 1) + ". " + quizzes.get(i).getSubjectName());
        }
        
        System.out.print("\nSelect a subject quiz (1-" + quizzes.size() + ") or 0 to exit: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (choice > 0 && choice <= quizzes.size()) {
            presentQuiz(quizzes.get(choice - 1), scanner);
        }
    }
    
    /**
     * Specialized method to propose a quiz for a specific subject
     * @param nameMatiere The name of the subject
     * @param anneeScolaire The school year
     * @param trimestre The trimester
     * @return A quiz for the specified subject
     */
    public static SubjectQuiz proposeQuizForSpecificSubject(String nameMatiere, int anneeScolaire, int trimestre) {
        SubjectQuiz subjectQuiz = new SubjectQuiz(nameMatiere);
        Connection conn = null;
        
        try {
            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Find exercises for this subject
            List<Exercice> exercises = findExercisesForSubject(conn, nameMatiere, anneeScolaire, trimestre);
            
            if (exercises.isEmpty()) {
                System.out.println("No exercises found for " + nameMatiere + " in year " + 
                                  anneeScolaire + ", trimester " + trimestre);
                return subjectQuiz;
            }
            
            // For each exercise, get questions and answers
            for (Exercice exercise : exercises) {
                ExerciseQuiz exerciseQuiz = new ExerciseQuiz(
                    exercise.getExerciceId(), 
                    exercise.getUnitNumber(), 
                    exercise.showLevlsExo()
                );
                
                // Get questions for this exercise
                List<Question> questions = getQuestionsForExercise(
                    conn, 
                    exercise.getExerciceId(),
                    exercise.getAnneeScolaire(),
                    exercise.getTrimestre(),
                    exercise.getUnitNumber()
                );
                
                // For each question, get answers
                for (Question question : questions) {
                    QuestionWithAnswers questionWithAnswers = new QuestionWithAnswers(
                        question.getQuestionId(),
                        question.getContentQuestion(),
                        question.getType()
                    );
                    
                    // Get appropriate answers based on question type
                    if (question.getType()) { // QCM type question
                        questionWithAnswers.setAnswers(getAnswersForQuestion(conn, question.getQuestionId()));
                    } else { // True/False type question
                        questionWithAnswers.setTfAnswer(getTFAnswerForQuestion(conn, question.getQuestionId()));
                    }
                    
                    exerciseQuiz.addQuestion(questionWithAnswers);
                }
                
                subjectQuiz.addExercise(exerciseQuiz);
            }
            
        } catch (SQLException e) {
            System.out.println("Database error while generating quiz: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
        
        return subjectQuiz;
    }
    
    /**
     * Presents a quiz for a specific subject to the student
     */
    public static void presentQuizForSpecificSubject(String nameMatiere, int anneeScolaire, int trimestre, Scanner scanner) {
        System.out.println("Generating quiz for " + nameMatiere + " (Year " + anneeScolaire + ", Trimester " + trimestre + ")...");
        SubjectQuiz quiz = proposeQuizForSpecificSubject(nameMatiere, anneeScolaire, trimestre);
        
        if (quiz.getExercises().isEmpty()) {
            System.out.println("No quiz available for this subject at this time.");
            return;
        }
        
        presentQuiz(quiz, scanner);
    }
}

/**
 * Helper class to organize quiz data by subject
 */
class SubjectQuiz {
    private String subjectName;
    private List<ExerciseQuiz> exercises;
    
    public SubjectQuiz(String subjectName) {
        this.subjectName = subjectName;
        this.exercises = new ArrayList<>();
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public List<ExerciseQuiz> getExercises() {
        return exercises;
    }
    
    public void addExercise(ExerciseQuiz exercise) {
        exercises.add(exercise);
    }
}

/**
 * Helper class to organize quiz data by exercise
 */
class ExerciseQuiz {
    private int exerciseId;
    private int unitNumber;
    private int level;
    private List<QuestionWithAnswers> questions;
    
    public ExerciseQuiz(int exerciseId, int unitNumber, int level) {
        this.exerciseId = exerciseId;
        this.unitNumber = unitNumber;
        this.level = level;
        this.questions = new ArrayList<>();
    }
    
    public int getExerciseId() {
        return exerciseId;
    }
    
    public int getUnitNumber() {
        return unitNumber;
    }
    
    public int getLevel() {
        return level;
    }
    
    public List<QuestionWithAnswers> getQuestions() {
        return questions;
    }
    
    public void addQuestion(QuestionWithAnswers question) {
        questions.add(question);
    }
}

/**
 * Helper class to organize question data with its answers
 */
class QuestionWithAnswers {
    private int questionId;
    private String questionText;
    private boolean isMultipleChoice; // true for QCM, false for True/False
    private List<Answer> answers; // Used for QCM questions
    private AnswerTF tfAnswer; // Used for True/False questions
    
    public QuestionWithAnswers(int questionId, String questionText, boolean isMultipleChoice) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.isMultipleChoice = isMultipleChoice;
        
        if (isMultipleChoice) {
            this.answers = new ArrayList<>();
        }
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }
    
    public List<Answer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    
    public AnswerTF getTfAnswer() {
        return tfAnswer;
    }
    
    public void setTfAnswer(AnswerTF tfAnswer) {
        this.tfAnswer = tfAnswer;
    }
}