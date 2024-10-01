package com.aston.assessment.DTO;

import com.aston.assessment.DTO.AssessmentParticipantDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {
    private String assessmentCategory;
    private String title;
    private int assessmentWeighting;
    private LocalDate plannedIssueDate;
    private LocalDate courseworkSubmissionDate;
    private List<AssessmentParticipantDTO> participants;
}