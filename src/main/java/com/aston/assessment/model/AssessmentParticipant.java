package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AssessmentParticipants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_id", "user_id", "role"}))
public class AssessmentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "assessment_participant_roles",
            joinColumns = @JoinColumn(name = "participant_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<AssessmentRoles> roles = new HashSet<>();
}