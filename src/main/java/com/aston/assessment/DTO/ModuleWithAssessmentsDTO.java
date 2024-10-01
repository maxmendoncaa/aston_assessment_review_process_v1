package com.aston.assessment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWithAssessmentsDTO {
    private String name;
    private String code;
    private int credits;
    private int level;
    private List<AssessmentDTO> assessments;
}
