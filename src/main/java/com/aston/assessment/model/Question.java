package com.aston.assessment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="question_id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "yes_no_answer")
    private boolean yesNoAnswer;


    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;


}