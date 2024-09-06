package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExternalExaminerResponses")
public class ExternalExaminerResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_examiner_response_id")
    private Long id;

    @Column(name = "comments")
    private String comments;

    @Column(name = "review_assessment_again")
    private boolean reviewAssessmentAgain;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "response_to_external_examiner_id")
    private ResponseToExternalExaminer responseToExternalExaminer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "programme_director_confirmation_id")
    private ProgrammeDirectorConfirmation programmeDirectorConfirmation;

    // Additional methods or fields if necessary
}
