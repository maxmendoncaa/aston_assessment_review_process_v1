package com.aston.assessment.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String moduleCode;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private String moduleLeader;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private String moduleOutcomes; // This could also be a List<String> if you want multiple outcomes stored separately

    @Column(nullable = false)
    private String skillsAndBehaviours; // This could also be a List<String> if you want multiple skills/behaviours stored separately

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Assessment> assessments;

    // Additional methods or fields if necessary
}
