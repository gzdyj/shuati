package com.quizmaster.service.impl;

import com.quizmaster.dto.ImportResultDTO;
import com.quizmaster.dto.QuestionImportDTO;
import com.quizmaster.entity.Question;
import com.quizmaster.entity.QuestionOption;
import com.quizmaster.mapper.QuestionMapper;
import com.quizmaster.mapper.QuestionOptionMapper;
import com.quizmaster.service.ExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionOptionMapper questionOptionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importQuestions(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            result.setTotalRows(totalRows);

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                QuestionImportDTO dto = parseRow(row, i + 1);
                
                if (dto == null) {
                    failCount++;
                    errors.add("第" + (i + 1) + "行：数据为空或格式错误");
                    continue;
                }

                if (!validateQuestion(dto)) {
                    failCount++;
                    errors.add("第" + (i + 1) + "行：数据验证失败，题目或选项为空");
                    continue;
                }

                try {
                    saveQuestion(dto);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("第" + (i + 1) + "行：保存失败 - " + e.getMessage());
                }
            }

        } catch (Exception e) {
            errors.add("文件解析失败：" + e.getMessage());
            failCount++;
        }

        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setErrorMessages(errors);
        return result;
    }

    private QuestionImportDTO parseRow(Row row, int rowNum) {
        QuestionImportDTO dto = new QuestionImportDTO();
        dto.setRowNum(rowNum);

        try {
            Cell titleCell = row.getCell(1);
            Cell optionACell = row.getCell(2);
            Cell optionBCell = row.getCell(3);
            Cell optionCCell = row.getCell(4);
            Cell optionDCell = row.getCell(5);
            Cell answerCell = row.getCell(6);
            Cell analysisCell = row.getCell(7);

            dto.setTitle(getCellValue(titleCell));
            dto.setOptionA(getCellValue(optionACell));
            dto.setOptionB(getCellValue(optionBCell));
            dto.setOptionC(getCellValue(optionCCell));
            dto.setOptionD(getCellValue(optionDCell));
            dto.setAnswer(getCellValue(answerCell));
            dto.setAnalysis(getCellValue(analysisCell));

            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private boolean validateQuestion(QuestionImportDTO dto) {
        return dto.getTitle() != null && !dto.getTitle().isEmpty()
                && dto.getOptionA() != null && !dto.getOptionA().isEmpty()
                && dto.getOptionB() != null && !dto.getOptionB().isEmpty()
                && dto.getOptionC() != null && !dto.getOptionC().isEmpty()
                && dto.getOptionD() != null && !dto.getOptionD().isEmpty()
                && dto.getAnswer() != null && !dto.getAnswer().isEmpty();
    }

    private void saveQuestion(QuestionImportDTO dto) {
        Question question = new Question();
        question.setContent(dto.getTitle());
        question.setCorrectAnswer(dto.getAnswer().toUpperCase());
        question.setExplanation(dto.getAnalysis());
        
        questionMapper.insert(question);
        
        Long questionId = question.getId();

        QuestionOption optionA = new QuestionOption();
        optionA.setQuestionId(questionId);
        optionA.setOptionId("A");
        optionA.setContent(dto.getOptionA());
        questionOptionMapper.insert(optionA);

        QuestionOption optionB = new QuestionOption();
        optionB.setQuestionId(questionId);
        optionB.setOptionId("B");
        optionB.setContent(dto.getOptionB());
        questionOptionMapper.insert(optionB);

        QuestionOption optionC = new QuestionOption();
        optionC.setQuestionId(questionId);
        optionC.setOptionId("C");
        optionC.setContent(dto.getOptionC());
        questionOptionMapper.insert(optionC);

        QuestionOption optionD = new QuestionOption();
        optionD.setQuestionId(questionId);
        optionD.setOptionId("D");
        optionD.setContent(dto.getOptionD());
        questionOptionMapper.insert(optionD);
    }
}