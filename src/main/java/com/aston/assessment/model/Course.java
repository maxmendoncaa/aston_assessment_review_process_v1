package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;
}