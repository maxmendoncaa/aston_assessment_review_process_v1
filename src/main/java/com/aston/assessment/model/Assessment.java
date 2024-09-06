package com.aston.assessment.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    @Column(name = "assessment_id")
    private Long id;

    @Column(nullable = false)
    private String assessmentCategory;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int assessmentWeighting;

    @Column(nullable = false)
    private String externalExaminer;

    @Column(nullable = false)
    private LocalDate plannedIssueDate;

    @Column(nullable = false)
    private LocalDate courseworkSubmissionDate;

    @Column(nullable = false)
    private String moduleAssessmentLeadSignature;

    @Column(nullable = false)
    private LocalDate moduleAssessmentLeadSignatureDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "internal_moderator_id")
    private InternalModerator internalModerator;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "external_examiner_response_id")
    private ExternalExaminerResponse externalExaminerResponse;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    // Additional methods or fields if necessary
}
