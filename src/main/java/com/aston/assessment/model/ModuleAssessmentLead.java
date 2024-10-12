package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ModuleAssessmentLeadSignatures")
public class ModuleAssessmentLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(name = "response_to_internal_moderator")
    private String responseToInternalModerator;
    @Column
    private LocalDateTime responseToInternalModeratorDateTime;

    @Column(name = "response_to_external_examiner")
    private String responseToExternalExaminer;
    @Column
    private LocalDateTime responseToExternalExaminerDateTime;

    @Column(name = "module_assessment_lead_signature")
    private String moduleAssessmentLeadSignature="Pending";

    @Column(name = "module_assessment_lead_signature_date")
    private LocalDateTime moduleAssessmentLeadSignatureDateTime;

    @Column(name = "stage2_module_assessment_lead_signature_date")
    private LocalDateTime stage2ModuleAssessmentLeadSignatureDateTime;

    @Lob
    @Column(name="stage2_assessment_lead_comments")
    private String stage2_assessmentLeadComments;


}