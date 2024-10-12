package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ProgrammeDirectorConfirmations")
public class ProgrammeDirectorConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "programme_director_confirmation_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users user;

    @Column
    private boolean appropriatelyResponded;

    @Column(name="programme_Director_Confirmation_signature_DateTime")
    private LocalDateTime programmeDirectorConfirmation_signatureDateTime;
    @Column(name="programmeDirector_Confirmation_signature")
    private String programmeDirectorConfirmation_signature="Pending";

    @Column(name="programmeDirectorConfirmation_signatureDateTime_stage2")
    private LocalDateTime programmeDirectorConfirmation_signatureDateTime_stage2;
    @Column(name="programmeDirectorConfirmation_signature_stage2")
    private String programmeDirectorConfirmation_signature_stage2;


}