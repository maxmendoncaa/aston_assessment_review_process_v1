package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExternalExaminerResponses")
public class ExternalExaminerResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="external_examiner_response_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users user;

    @Column(name="external_examiner_comments")
    private String external_examiner_comments;

    @Column
    private boolean reviewAssessmentAgain;

    @Column(name="externalExaminer_signatureDateTime")
    private LocalDateTime externalExaminer_signatureDateTime;
    @Column(name="externalExaminer_signature")
    private String externalExaminer_signature;
}