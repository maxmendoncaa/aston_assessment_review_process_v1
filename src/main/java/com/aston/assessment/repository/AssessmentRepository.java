package com.aston.assessment.repository;

import com.aston.assessment.model.Assessment;
import com.aston.assessment.model.AssessmentStage;
import com.aston.assessment.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    // Find assessments by module ID
    List<Assessment> findByModuleId(Long moduleId);

    // Find assessments by stage
    List<Assessment> findByStage(AssessmentStage stage);

    // Find assessments by category
    List<Assessment> findByAssessmentCategory(String category);

    // Find assessments with submission date before a given date
    List<Assessment> findByCourseworkSubmissionDateBefore(LocalDate date);

    // Find assessments with submission date after a given date
    List<Assessment> findByCourseworkSubmissionDateAfter(LocalDate date);

    // Find assessments by title (case-insensitive, partial match)
    List<Assessment> findByTitleContainingIgnoreCase(String title);

    // Find assessments by weighting greater than or equal to a given value
    List<Assessment> findByAssessmentWeightingGreaterThanEqual(int weighting);

    // Custom query to find assessments with a specific number of participants or more
    @Query("SELECT a FROM Assessment a WHERE SIZE(a.participants) >= :count")
    List<Assessment> findAssessmentsWithAtLeastNParticipants(@Param("count") int count);

    // Find assessments for a specific module and stage
    List<Assessment> findByModuleIdAndStage(Long moduleId, AssessmentStage stage);

    // Count assessments for a specific module
    long countByModuleId(Long moduleId);

    List<Assessment> findByModuleAndParticipantsUserEmail(Module module, String userEmail);List<Assessment> findByModule(Module module);
}