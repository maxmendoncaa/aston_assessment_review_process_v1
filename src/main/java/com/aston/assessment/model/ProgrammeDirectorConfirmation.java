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
@Table(name = "ProgrammeDirectorConfirmations")
public class ProgrammeDirectorConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "programme_director_confirmation_id")
    private Long id;

    @Column(name = "appropriately_responded", nullable = false)
    private boolean appropriatelyResponded;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Additional methods or fields if necessary
}
