package com.quizmaster.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class QuestionImportDTO {
    private Integer rowNum;

    @ExcelProperty(index = 1)
    private String title;

    @ExcelProperty(index = 2)
    private String optionA;

    @ExcelProperty(index = 3)
    private String optionB;

    @ExcelProperty(index = 4)
    private String optionC;

    @ExcelProperty(index = 5)
    private String optionD;

    @ExcelProperty(index = 6)
    private String answer;

    @ExcelProperty(index = 7)
    private String analysis;
}
