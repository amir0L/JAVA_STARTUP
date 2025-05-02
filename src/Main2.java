import java.util.Scanner;
import student.Student;

public class Main2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Example 1: Propose quizzes for a student's weak subjects
        System.out.println("===== Student Quiz Generation =====");
        Student student = new Student();
        
        // First authenticate the student
        if (student.authenticate(scanner)) {
            // Set threshold for weak subjects (e.g., subjects with grade below 10 out of 20)
            double threshold = 10.0;
            
            // Propose quizzes for weak subjects
            QuizGenerator.proposeQuizzesForStudent(student, threshold, scanner);
        }
        
        // Example 2: Generate a quiz for a specific subject
        System.out.println("\n===== Specific Subject Quiz =====");
        System.out.print("Enter subject name: ");
        String subjectName = scanner.nextLine();
        
        System.out.print("Enter school year (1-4): ");
        int schoolYear = scanner.nextInt();
        
        System.out.print("Enter trimester (1-3): ");
        int trimester = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        // Present quiz for the specific subject
        QuizGenerator.presentQuizForSpecificSubject(subjectName, schoolYear, trimester, scanner);
        
        scanner.close();
    }
}