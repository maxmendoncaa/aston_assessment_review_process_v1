package com.aston.assessment.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssessmentUpdateDTO {
    // Fields for Module Assessment Lead
    private String title;
    private String moduleCode;
    private String assessmentCategory;
    private Integer assessmentWeighting;
    private LocalDate plannedIssueDate;
    private LocalDate courseworkSubmissionDate;

    // Fields for Internal Moderator
    private String internalModeratorComments;

    // Fields for External Examiner
    private String externalExaminerComments;
    private String externalExaminerApproval;

    // Fields for Programme Director
    private String programmeDirectorApproval;
}