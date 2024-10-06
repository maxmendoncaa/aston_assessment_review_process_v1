package com.aston.assessment.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class AssessmentDTO {
    private Long id;
    private String title;
    private String moduleCode;
    private String moduleLeader;
    private String assessmentCategory;
    private int assessmentWeighting;
    private LocalDate plannedIssueDate;
    private LocalDate courseworkSubmissionDate;
    private Set<String> userRoles;

    // Module Assessment Lead fields
    private String moduleAssessmentLeadSignature;
    private LocalDateTime moduleAssessmentLeadSignatureDate;

    // Internal Moderator fields
    private String internalModeratorComments;
    private String internalModeratorSignature;
    private LocalDate internalModeratorSignatureDate;

    // External Examiner fields
    private String externalExaminerComments;
    private String externalExaminerApproval;
    private LocalDate externalExaminerSignatureDate;

    // Programme Director fields
    private String programmeDirectorApproval;
    private LocalDate programmeDirectorSignatureDate;
    // Add this field for participants
    private List<AssessmentParticipantDTO> participants;


}