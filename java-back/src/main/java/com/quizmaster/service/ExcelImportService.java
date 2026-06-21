package com.quizmaster.service;

import com.quizmaster.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {
    ImportResultDTO importQuestions(MultipartFile file);
}
