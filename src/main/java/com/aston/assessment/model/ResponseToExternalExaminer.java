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
@Table(name = "ResponsesToExternalExaminers")
public class ResponseToExternalExaminer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_to_external_examiner_id")
    private Long id;

    @Column(name = "response_text", nullable = false)
    private String responseText;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Additional methods or fields if necessary
}
