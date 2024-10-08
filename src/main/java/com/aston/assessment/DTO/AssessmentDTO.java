package com.aston.assessment.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String skills;
    private int assessmentWeighting;
    private LocalDate plannedIssueDate;
    private LocalDate courseworkSubmissionDate;
    private Set<String> userRoles;

    // Module Assessment Lead fields
    private String moduleAssessmentLeadSignature;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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

    public Set<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<String> userRoles) {
        this.userRoles = userRoles;
    }


}