package com.aston.assessment.repository;

import com.aston.assessment.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByInternalModeratorId(Long internalModeratorId);
}