package com.quizmaster.controller;

import com.quizmaster.common.Result;
import com.quizmaster.dto.ImportResultDTO;
import com.quizmaster.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @PostMapping("/questions")
    public Result<ImportResultDTO> importQuestions(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".xlsx")) {
            return Result.error("仅支持.xlsx格式的Excel文件");
        }

        ImportResultDTO result = excelImportService.importQuestions(file);
        return Result.success(result);
    }
}
