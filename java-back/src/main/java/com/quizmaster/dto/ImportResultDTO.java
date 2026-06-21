package com.quizmaster.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultDTO {
    private int totalRows;
    private int successCount;
    private int failCount;
    private List<String> errorMessages = new ArrayList<>();
}