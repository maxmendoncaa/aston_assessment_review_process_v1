package com.aston.assessment.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedIssueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseworkSubmissionDate;
    private Set<String> userRoles;

    // Module Assessment Lead fields
    private String moduleAssessmentLeadSignature;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime moduleAssessmentLeadSignatureDateTime;
    private String responseToInternalModerator;
    private LocalDateTime responseToInternalModeratorDateTime;
    private String responseToExternalExaminer;
    private LocalDateTime responseToExternalExaminerDateTime;
    private String stage2_assessmentLeadComments;

    // Internal Moderator fields
    private String internalModeratorComments;
    private String internalModeratorSignature;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime internalModeratorSignatureDateTime;
    private String stage2_moderatorComments;

    // External Examiner fields
    private String externalExaminerComments;
    private String externalExaminerApproval;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime externalExaminerSignatureDateTime;
    private  String externalExaminer_signature;

    // Programme Director fields
    private String programmeDirectorApproval;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime programmeDirectorSignatureDateTime;
    private String programmeDirectorConfirmation_signature="Pending";
    private String programmeDirectorConfirmation_signature_stage2;


    //stage2fields
    private LocalDate assessmentDeadline;
    private Integer totalSubmissions;
    private Integer failedSubmissions;
    private Integer moderatedSubmissions;
    private String teachingImpactDetails;



    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate markingCompletedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moderationCompletedDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime moderatorSignatureDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime programmeDirectorConfirmation_signatureDateTime_stage2;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime stage2ModuleAssessmentLeadSignatureDateTime;

    //Triggers for flow of assessment review process - Stage1

    private String assessmentDetailsTrigger="Not Completed";
    private String internalModeratorDetailsTrigger="Not Completed";
    private String externalExaminerDetailsTrigger="Not Completed";
    private String programmeDirectorDetailsTrigger="Not Completed";

    //Triggers for flow of assessment review process - Stage2
    private String internalModeratorModerationOfMarksTrigger="Not Completed";
    private String stage2ModuleAssessmentLeadCommentsTrigger="Not Completed";
    private String stage2ModeratorCommentsTrigger="Not Completed";




    // Add this field for participants
    private List<AssessmentParticipantDTO> participants;

    public Set<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<String> userRoles) {
        this.userRoles = userRoles;
    }


}