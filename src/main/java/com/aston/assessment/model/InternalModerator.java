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
@Table(name = "InternalModerators")
public class InternalModerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "internal_moderator_id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "internal_moderator_id")
    private List<Question> questions;

    @Column(name = "general_comment")
    private String generalComment;

    @Column(name = "internal_moderator_signature", nullable = false)
    private String internalModeratorSignature;

    @Column(name = "internal_moderator_signature_date", nullable = false)
    private LocalDate internalModeratorSignatureDate;

    @Column(name = "response_to_internal_moderator")
    private String responseToInternalModerator;

    @Column(name = "response_signature", nullable = false)
    private String responseSignature;

    @Column(name = "response_date", nullable = false)
    private LocalDate responseDate;

    // Additional methods or fields if necessary
}
