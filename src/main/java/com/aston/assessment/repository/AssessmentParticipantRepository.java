package com.aston.assessment.repository;

import com.aston.assessment.model.Assessment;
import com.aston.assessment.model.AssessmentParticipant;
import com.aston.assessment.model.AssessmentRoles;
import com.aston.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AssessmentParticipantRepository extends JpaRepository<AssessmentParticipant, Long> {
   // List<AssessmentParticipant> findByAssessmentIdAndRole(Long assessmentId, AssessmentRoles role);

    //boolean existsByAssessmentAndRole(Assessment assessment, AssessmentRoles role);

    boolean existsByAssessmentAndUserAndRoles(Assessment assessment, Users user, AssessmentRoles role);
    List<AssessmentParticipant> findByAssessmentId(Long assessmentId);
    boolean existsByAssessmentAndRolesContaining(Assessment assessment, AssessmentRoles role);
    Optional<AssessmentParticipant> findByAssessmentAndUser(Assessment assessment, Users user);
}
