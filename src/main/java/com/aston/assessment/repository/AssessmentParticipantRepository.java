package com.aston.assessment.repository;

import com.aston.assessment.model.Assessment;
import com.aston.assessment.model.AssessmentParticipant;
import com.aston.assessment.model.AssessmentRoles;
import com.aston.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentParticipantRepository extends JpaRepository<AssessmentParticipant, Long> {
    List<AssessmentParticipant> findByAssessmentIdAndRole(Long assessmentId, AssessmentRoles role);

    boolean existsByAssessmentAndRole(Assessment assessment, AssessmentRoles role);

    boolean existsByAssessmentAndUserAndRole(Assessment assessment, Users user, AssessmentRoles role);
}