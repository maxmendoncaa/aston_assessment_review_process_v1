//package com.aston.assessment.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "Assessments")
//public class Assessment {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String assessmentCategory;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false)
//    private int assessmentWeighting;
//
//    @Column(nullable = false)
//    private LocalDate plannedIssueDate;
//
//    @Column(nullable = false)
//    private LocalDate courseworkSubmissionDate;
//
//    @ManyToOne
//    @JoinColumn(name = "module_id", nullable = false)
//    private Module module;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AssessmentStage stage = AssessmentStage.STAGE_1;
//
//    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
//    private List<AssessmentParticipant> participants;
//
//    // Stage 1 fields
//    @Column(name = "module_assessment_lead_signature")
//    private String moduleAssessmentLeadSignature;
//
//    @Column(name = "module_assessment_lead_signature_date")
//    private LocalDate moduleAssessmentLeadSignatureDate;
//
//    @Column(name = "internal_moderator_signature")
//    private String internalModeratorSignature;
//
//    @Column(name = "internal_moderator_signature_date")
//    private LocalDate internalModeratorSignatureDate;
//
//    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
//    private InternalModerator internalModerator;
//
//    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
//    private List<Question> questions;
//
//
//    // Stage 2 fields
//    @Column(name = "assessment_deadline")
//    private LocalDate assessmentDeadline;
//
//    @Column(name = "marking_completed_date")
//    private LocalDate markingCompletedDate;
//
//    @Column(name = "moderation_completed_date")
//    private LocalDate moderationCompletedDate;
//
//    @Column(name = "total_submissions")
//    private Integer totalSubmissions;
//
//    @Column(name = "failed_submissions")
//    private Integer failedSubmissions;
//
//    @Column(name = "moderated_submissions")
//    private Integer moderatedSubmissions;
//
//    @Column(name = "teaching_impact_details")
//    private String teachingImpactDetails;
//
//    @Column(name = "moderator_signature_date")
//    private LocalDate moderatorSignatureDate;
//
//    @Column(name = "stage2_module_assessment_lead_signature_date")
//    private LocalDate stage2ModuleAssessmentLeadSignatureDate;
//
//    @Column(name = "programme_dir_signature_date")
//    private LocalDate programmeDirSignatureDate;
//
//    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
//    private List<ExternalExaminerResponse> externalExaminerResponses;
//
//    @OneToOne(mappedBy = "assessment", cascade = CascadeType.ALL)
//    private ProgrammeDirectorConfirmation programmeDirectorConfirmation;
//}
package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="assessment_id")
    private Long id;

    @Column(nullable = false)
    private String assessmentCategory;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(nullable = false)
    private int assessmentWeighting;

    @Column(nullable = false)
    private LocalDate plannedIssueDate;

    @Column(nullable = false)
    private LocalDate courseworkSubmissionDate;

    @OneToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentStage stage = AssessmentStage.STAGE_1;

    @OneToOne(mappedBy = "assessment", cascade = CascadeType.ALL)
    private InternalModerator internalModerator;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<Question> questions;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<AssessmentParticipant> participants;

    //    @JoinColumn(name = "module_assessment_lead_id")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "assessment")

    private ModuleAssessmentLead moduleAssessmentLead;



    //Triggers for flow of assessment review process - Stage1
    @Column(name="assessment_details_trigger")
    private String assessmentDetailsTrigger="Not Completed";
    @Column(name="internal_moderator_details_trigger")
    private String internalModeratorDetailsTrigger="Not Completed";
    @Column(name="external_examiner_details_trigger")
    private String externalExaminerDetailsTrigger="Not Completed";
    @Column(name="programme_director_details_trigger")
    private String programmeDirectorDetailsTrigger="Not Completed";



    // Stage 2 fields
    @Column(name = "assessment_deadline")
    private LocalDate assessmentDeadline;

    @Column(name = "marking_completed_date")
    private LocalDate markingCompletedDate;

    @Column(name = "moderation_completed_date")
    private LocalDate moderationCompletedDate;

    @Column(name = "total_submissions")
    private Integer totalSubmissions;

    @Column(name = "failed_submissions")
    private Integer failedSubmissions;

    @Column(name = "moderated_submissions")
    private Integer moderatedSubmissions;

    @Lob
    @Column(name = "teaching_impact_details")
    private String teachingImpactDetails;


    @OneToOne(mappedBy = "assessment", cascade = CascadeType.ALL)
    private ExternalExaminerResponse externalExaminerResponses;

    @OneToOne(mappedBy = "assessment", cascade = CascadeType.ALL)
    private ProgrammeDirectorConfirmation programmeDirectorConfirmation;

    //Triggers for flow of assessment review process - Stage2
    @Column(name="internal_moderator_moderation_of_marks_trigger")
    private String internalModeratorModerationOfMarksTrigger="Not Completed";
    @Column(name="stage2_module_assessment_lead_comments_trigger")
    private String stage2ModuleAssessmentLeadCommentsTrigger="Not Completed";
    @Column(name="stage2_moderator_comments_trigger")
    private String stage2ModeratorCommentsTrigger="Not Completed";

}