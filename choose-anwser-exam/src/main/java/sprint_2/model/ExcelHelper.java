package sprint_2.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sprint_2.service.SubjectService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelHelper {
    @Autowired
    private SubjectService subjectService;

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Sheet1";

    public boolean hasExcelFormat(MultipartFile file) {

        return TYPE.equals(file.getContentType());
    }

    public List<Question> excelQuestion(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Question> questions = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Question question = new Question();


                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
//                        case 0:
//                            question.setId((long) currentCell.getNumericCellValue());
//                            break;

                        case 0:
                           question.setQuestionContent(currentCell.getStringCellValue());
                            break;

                        case 1:
                            question.setTrueAnswer(currentCell.getStringCellValue());
                            break;

                        case 2:
                            Long idSubject = (long) currentCell.getNumericCellValue();
                            Subject a = subjectService.findById(idSubject);
                            question.setSubject(a);
                            break;

                        case 3:
                            question.setAnswerA(currentCell.getStringCellValue());
                            break;

                        case 4:
                            question.setAnswerB(currentCell.getStringCellValue());
                            break;

                        case 5:
                            question.setAnswerC(currentCell.getStringCellValue());
                            break;

                        case 6:
                            question.setAnswerD(currentCell.getStringCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                questions.add(question);
            }

            workbook.close();

            return questions;

        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}