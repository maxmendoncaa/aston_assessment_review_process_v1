package com.aston.assessment.DTO;

import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String questionText;
    private boolean yesNoAnswer;
    private String comment;
}