package sprint_2.controller;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sprint_2.model.Exam;
import sprint_2.model.Question;
import sprint_2.model.Subject;
import sprint_2.service.ExamService;
import sprint_2.service.QuestionService;
import sprint_2.service.SubjectService;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("exam")
public class ExamController {
    @Autowired
    ExamService examService;
    @Autowired
    private QuestionService questionService;

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Exam>> showAllExam() {
        List<Exam> list = examService.findAll();
        if (list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/deleteExam")
    public ResponseEntity<Void> deleteExam(@RequestParam("idExams") String idExams) {
        if (idExams.contains("-#-")) {
            String[] idExamList = idExams.split("-#-");
            Exam exam = null;
            for (int i = 0; i < idExamList.length; i++) {
                exam = examService.findById(Long.parseLong(idExamList[i]));
                if (exam == null) {
                    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (examService.findById(Long.parseLong(idExamList[i])) == null) {
                    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                examService.deleteById(Long.parseLong(idExamList[i]));
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/deleteQuestionInExam")
    public ResponseEntity<Void> deleteQuestionInExam(@RequestParam("idQuestions") String idQuestions) {
        if (idQuestions.contains("-#-")) {
            String[] idQuestionList = idQuestions.split("-#-");
            Exam exam = examService.findById(Long.parseLong(idQuestionList[0]));
            if (exam == null) {
                return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Set<Question> questions = exam.getQuestions();
            for (int i = 1; i < idQuestionList.length; i++) {
                if (questionService.findById(Long.parseLong(idQuestionList[i])) == null) {
                    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                questions.remove(questionService.findById(Long.parseLong(idQuestionList[i])));
            }
            exam.setQuestions(questions);
            examService.create(exam);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/addQuestionInExam")
    public ResponseEntity<Void> addQuestionInExam(@RequestParam("idQuestions") String idQuestions) {
        if (idQuestions.contains("-#-")) {
            String[] idQuestionList = idQuestions.split("-#-");
            Exam exam = examService.findById(Long.parseLong(idQuestionList[0]));
            if (exam == null) {
                return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Set<Question> questions = exam.getQuestions();
            for (int i = 1; i < idQuestionList.length; i++) {
                if (questionService.findById(Long.parseLong(idQuestionList[i])) == null) {
                    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                questions.add(questionService.findById(Long.parseLong(idQuestionList[i])));
            }
            exam.setQuestions(questions);
            examService.create(exam);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/create-exam")
    public ResponseEntity<Void> addExam(@RequestParam("examName") String examName, @RequestParam("subject") String subject) {
        if (!examName.equals("")) {
           if(subjectService.findById(Long.parseLong(subject)) == null){
               return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
           }
            Exam exam = new Exam();
            List<Question> questions = new ArrayList<>();
            List<Question> questionSubject = new ArrayList<>();
            for (int i = 0; i < questionService.findAll().size(); i++) {
                if (questionService.findAll().get(i).getSubject().getIdSubject().equals(Long.parseLong(subject))) {
                    questionSubject.add(questionService.findAll().get(i));
                }
            }
            for (int i = 0; i < 10; i++) {
                int randomIndex = (int) (Math.random() * (questionSubject.size()));
                questions.add(questionSubject.get(randomIndex));
                questionSubject.remove(randomIndex);
            }
            exam.setExamName(examName);
            Set<Question> questionSet = new HashSet<Question>(questions);
            exam.setQuestions(questionSet);
            exam.setExamDuration("10");
            examService.create(exam);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  
    @GetMapping("/findExam/{id}")
    public ResponseEntity<Exam> findExamById(@PathVariable long id) {
        Exam exam = examService.findById(id);
        if (exam == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(exam, HttpStatus.OK);
    }

    @GetMapping("/allQuestion")
    public ResponseEntity<List<Question>> findAllSubject(@RequestParam("idExam") String idExam) {
        List<Question> questions = questionService.findAll();
        Exam exam = examService.findById(Long.parseLong(idExam));
        if (exam == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Question> questionList = new ArrayList<Question>(exam.getQuestions());
        for (int i = 0; i < exam.getQuestions().size(); i++) {
            questions.remove(questionList.get(i));
        }
        if (questions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }
}
