import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

import answer.Answer;
import answer.AnswerTF;
import exercice.Exercice;
import matiere.Matiere;
import question.Question;

public class quiz {
    public static void quiz(Matiere matiere,Connection conn)throws SQLException{
        
        
        PreparedStatement pstmtExo ;    
        String exoSql = "SELECT (exerciceId,unitNumber,levlExo) FROM Exercices WHERE matiereName =matiere.getName() anneeScolaire = matiere.getName() trimestre=matiere.getTrimestre";
        pstmtExo =conn.prepareStatement(exoSql);
        ResultSet rsExo =pstmtExo.executeQuery();
        
        ArrayList <Exercice> exos = new ArrayList<>(); //we can change to take the ids onlyy??
        
        Exercice tmpExo =new Exercice();
        tmpExo.setMatiereName(matiere.getNameMatiere());
        tmpExo.setAnneeScolaire(matiere.getAnneeScolaire());
        tmpExo.setTrimestre(matiere.getTrimestre());
        
        while (rsExo.next()) {
            tmpExo.setExerciceId(rsExo.getInt("exerciceId"));
            tmpExo.setUnitNumber(rsExo.getInt("unitNumber"));
            tmpExo.setLevlExo(rsExo.getInt("levlExo"));
            exos.add(tmpExo);
        }
        //now we have the exos of the matiere with the unit
        //2_ get the qouestion from db using where atts of exos to the exactly question
        //loop to get the question of all the exercice of matiere 
        
        
        
    
    }   
    
    public static void getQuestionsOfExercice(Connection conn , Exercice exo)throws SQLException{
        
        PreparedStatement pstmtQuestion;
        String quesSql ="SELECT (questionId,contentQuestion,type)FROM Questions WHERE exerciceId= ,anneeScolaire=,trimestre=,unitNumber= " ;
        pstmtQuestion = conn.prepareStatement(quesSql);
        ResultSet rsQues = pstmtQuestion.executeQuery();
        
        ArrayList <Question> questions = new ArrayList<>();
        
        Question tmpQues =new Question();
        tmpQues.setExerciceId(exo.getExerciceId());
        tmpQues.setAnneeScolaire(exo.getAnneeScolaire());
        tmpQues.setTrimestre(exo.getTrimestre());
        tmpQues.setUnitNumber(exo.getUnitNumber());

        while(rsQues.next()){
            tmpQues.setQuestionId(rsQues.getInt("questionId"));
            tmpQues.setContentQuestion(rsQues.getString("contentQuestion"));
            tmpQues.setType(rsQues.getBoolean("type"));
            questions.add(tmpQues);
            
        }
        //now we have the question of the specific exercice 
        //_3 get the specific anwers of the question
        //loop to get the ansewers of the question
    }

    
    
    public static void getAnswersOfQuestion(Connection conn,Question ques) {
        
        try {
            
            PreparedStatement pstmtAnswer = null;
            String AnsSql = null;
            
            if (ques.getType()) { 
                AnsSql = "SELECT (answerId,contentAnswer,isCorrect)FROM Answers WHERE question_questionId = "+ques.getQuestionId();
                pstmtAnswer=conn.prepareStatement(AnsSql);
                
                ArrayList <Answer> anss =new ArrayList<>();
                Answer ansTmp = new Answer();
                ansTmp.setQuestionId(ques.getQuestionId());
                
                ResultSet rsAns = pstmtAnswer.executeQuery();
                
                while(rsAns.next()){
                    ansTmp.setAnswerId(rsAns.getInt("answerId"));
                    ansTmp.setContentAnswer(rsAns.getString("contentAnswer"));
                    ansTmp.setIsCorrect(rsAns.getBoolean("isCorrect"));
                    anss.add(ansTmp);
                }
                //now you have the answers of specific question
            }else{
                AnsSql = "SELECT (answerTfId,isTrue) FROM AnswersTF WHERE question = "+ques.getQuestionId();
                pstmtAnswer=conn.prepareStatement(AnsSql);
                
                ArrayList <AnswerTF> ansTFs =new ArrayList<>();
                AnswerTF ansTmp = new AnswerTF();
                ansTmp.setQuestionId(ques.getQuestionId());
                
                ResultSet rsAns = pstmtAnswer.executeQuery();
                
                while (rsAns.next()) {
                    ansTmp.setAnswerTfId(rsAns.getInt("answerTfId"));
                    ansTmp.setIsTrue(rsAns.getBoolean("isTrue"));
                    ansTFs.add(ansTmp);
                }
                // now you have the reponse tf of the specific question
            }
            
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }




    
}
