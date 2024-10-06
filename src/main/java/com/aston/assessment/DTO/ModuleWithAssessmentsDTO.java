package com.aston.assessment.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWithAssessmentsDTO {
    private String moduleName;
    private String moduleLeader;
    private String moduleCode;
    private int credits;
    private int level;
    private List<AssessmentDTO> assessments;
    private String moduleOutcomes;
    
}
