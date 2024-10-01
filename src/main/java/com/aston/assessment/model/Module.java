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

    @Column(name = "module_name", unique = true)
    private String moduleName;

    @Column(name = "module_code", unique = true)
    private String moduleCode;

    @Column(name = "level")
    private int level;

    @Column(name = "module_leader")
    private String moduleLeader;

    @Column(name = "credits")
    private int credits;

    @Column(name = "module_outcomes")
    private String moduleOutcomes; // This could also be a List<String> if you want multiple outcomes stored separately

    @Column(name = "skills")
    private String skills; // This could also be a List<String> if you want multiple skills/behaviours stored separately

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<Assessment> assessments;

    // Additional methods or fields if necessary
}
