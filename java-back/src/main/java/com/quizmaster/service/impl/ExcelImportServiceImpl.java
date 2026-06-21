package com.quizmaster.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.quizmaster.dto.ImportResultDTO;
import com.quizmaster.dto.QuestionImportDTO;
import com.quizmaster.entity.Question;
import com.quizmaster.entity.QuestionOption;
import com.quizmaster.service.ExcelImportService;
import com.quizmaster.service.QuestionOptionService;
import com.quizmaster.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final int BATCH_SIZE = 500;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionOptionService questionOptionService;

    @Override
    public ImportResultDTO importQuestions(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        List<String> errors = new ArrayList<>();

        QuestionExcelListener listener = new QuestionExcelListener(errors);

        try {
            EasyExcel.read(file.getInputStream(), QuestionImportDTO.class, listener)
                    .sheet()
                    .headRowNumber(1)
                    .doRead();
        } catch (IOException e) {
            errors.add("文件读取失败：" + e.getMessage());
        }

        result.setTotalRows(listener.getTotalRows());
        result.setSuccessCount(listener.getSuccessCount());
        result.setFailCount(listener.getFailCount());
        result.setErrorMessages(errors);
        return result;
    }

    private class QuestionExcelListener implements ReadListener<QuestionImportDTO> {

        private final List<String> errors;
        private int totalRows = 0;
        private int successCount = 0;
        private int failCount = 0;
        private int currentRowIndex = 1;

        private final List<Question> questionBatch = new ArrayList<>();
        private final List<QuestionOption> optionBatch = new ArrayList<>();

        public QuestionExcelListener(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void invoke(QuestionImportDTO dto, AnalysisContext context) {
            currentRowIndex++;
            totalRows++;

            if (dto == null) {
                failCount++;
                errors.add("第" + currentRowIndex + "行：数据为空或格式错误");
                return;
            }

            if (!validateQuestion(dto)) {
                failCount++;
                errors.add("第" + currentRowIndex + "行：数据验证失败，题目或选项为空");
                return;
            }

            try {
                Question question = convertToQuestion(dto);
                questionBatch.add(question);

                List<QuestionOption> options = convertToOptions(dto);
                optionBatch.addAll(options);

                if (questionBatch.size() >= BATCH_SIZE) {
                    batchInsert(questionBatch, optionBatch);
                    successCount += questionBatch.size();
                    questionBatch.clear();
                    optionBatch.clear();
                }
            } catch (Exception e) {
                failCount++;
                errors.add("第" + currentRowIndex + "行：转换失败 - " + e.getMessage());
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (!questionBatch.isEmpty()) {
                batchInsert(questionBatch, optionBatch);
                successCount += questionBatch.size();
                questionBatch.clear();
                optionBatch.clear();
            }
        }

        public int getTotalRows() {
            return totalRows;
        }

        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailCount() {
            return failCount;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<Question> questions, List<QuestionOption> options) {
        questionService.saveBatch(questions);

        for (int i = 0; i < questions.size(); i++) {
            Long questionId = questions.get(i).getId();
            int optionStartIndex = i * 4;
            for (int j = 0; j < 4 && optionStartIndex + j < options.size(); j++) {
                options.get(optionStartIndex + j).setQuestionId(questionId);
            }
        }

        questionOptionService.saveBatch(options);
    }

    private boolean validateQuestion(QuestionImportDTO dto) {
        return dto.getTitle() != null && !dto.getTitle().isEmpty()
                && dto.getOptionA() != null && !dto.getOptionA().isEmpty()
                && dto.getOptionB() != null && !dto.getOptionB().isEmpty()
                && dto.getOptionC() != null && !dto.getOptionC().isEmpty()
                && dto.getOptionD() != null && !dto.getOptionD().isEmpty()
                && dto.getAnswer() != null && !dto.getAnswer().isEmpty();
    }

    private Question convertToQuestion(QuestionImportDTO dto) {
        Question question = new Question();
        question.setContent(dto.getTitle());
        question.setCorrectAnswer(dto.getAnswer().toUpperCase());
        question.setExplanation(dto.getAnalysis());
        return question;
    }

    private List<QuestionOption> convertToOptions(QuestionImportDTO dto) {
        List<QuestionOption> options = new ArrayList<>();

        QuestionOption optionA = new QuestionOption();
        optionA.setOptionId("A");
        optionA.setContent(dto.getOptionA());
        options.add(optionA);

        QuestionOption optionB = new QuestionOption();
        optionB.setOptionId("B");
        optionB.setContent(dto.getOptionB());
        options.add(optionB);

        QuestionOption optionC = new QuestionOption();
        optionC.setOptionId("C");
        optionC.setContent(dto.getOptionC());
        options.add(optionC);

        QuestionOption optionD = new QuestionOption();
        optionD.setOptionId("D");
        optionD.setContent(dto.getOptionD());
        options.add(optionD);

        return options;
    }
}
