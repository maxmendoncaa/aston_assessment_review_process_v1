package com.aston.assessment.service;

import com.aston.assessment.model.*;
import com.aston.assessment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExternalExaminerResponseRepository externalExaminerResponseRepository;

    @Autowired
    private ProgrammeDirectorConfirmationRepository programmeDirectorConfirmationRepository;

    @Transactional
    public Assessment createAssessment(Assessment assessment) {
        assessment.setStage(AssessmentStage.STAGE_1);
        return assessmentRepository.save(assessment);
    }

    @Transactional
    public void addParticipantToAssessment(Long assessmentId, Long userId, AssessmentRoles role) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ((role == AssessmentRoles.PROGRAMME_DIRECTOR || role == AssessmentRoles.MODULE_ASSESSMENT_LEAD) &&
                participantRepository.existsByAssessmentAndRole(assessment, role)) {
            throw new RuntimeException("This role is already assigned for this assessment");
        }

        AssessmentParticipant participant = new AssessmentParticipant();
        participant.setAssessment(assessment);
        participant.setUser(user);
        participant.setRole(role);
        participantRepository.save(participant);
    }

    @Transactional
    public void addExternalExaminerResponse(Long assessmentId, Long userId, String comments, boolean reviewAgain) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByAssessmentAndUserAndRole(assessment, user, AssessmentRoles.EXTERNAL_EXAMINER)) {
            throw new RuntimeException("User is not an external examiner for this assessment");
        }

        ExternalExaminerResponse response = new ExternalExaminerResponse();
        response.setAssessment(assessment);
        response.setUser(user);
        response.setComments(comments);
        response.setReviewAssessmentAgain(reviewAgain);
        response.setSignatureDate(LocalDate.now());

        externalExaminerResponseRepository.save(response);
    }

    @Transactional
    public void addProgrammeDirectorConfirmation(Long assessmentId, Long userId, boolean appropriatelyResponded) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByAssessmentAndUserAndRole(assessment, user, AssessmentRoles.PROGRAMME_DIRECTOR)) {
            throw new RuntimeException("User is not the programme director for this assessment");
        }

        ProgrammeDirectorConfirmation confirmation = new ProgrammeDirectorConfirmation();
        confirmation.setAssessment(assessment);
        confirmation.setUser(user);
        confirmation.setAppropriatelyResponded(appropriatelyResponded);
        confirmation.setSignatureDate(LocalDate.now());

        programmeDirectorConfirmationRepository.save(confirmation);
    }

    @Transactional
    public void moveToStage2(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (assessment.getStage() != AssessmentStage.STAGE_1) {
            throw new RuntimeException("Assessment is not in Stage 1");
        }

        if (assessment.getModuleAssessmentLeadSignature() == null ||
                assessment.getModuleAssessmentLeadSignatureDate() == null) {
            throw new RuntimeException("Module Assessment Lead signature is required to move to Stage 2");
        }

        if (assessment.getInternalModerator().getInternalModeratorSignature() == null ||
                assessment.getInternalModerator().getInternalModeratorSignatureDate() == null) {
            throw new RuntimeException("Internal Moderator signature is required to move to Stage 2");
        }

        assessment.setStage(AssessmentStage.STAGE_2);
        assessmentRepository.save(assessment);
    }

    @Transactional
    public void addQuestionToAssessment(Long assessmentId, Question question) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        question.setAssessment(assessment);
        questionRepository.save(question);
    }

    public List<Question> getQuestionsForAssessment(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        return assessment.getQuestions();
    }

    @Transactional
    public void setInternalModeratorSignature(Long assessmentId, Long userId, String signature) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByAssessmentAndUserAndRole(assessment, user, AssessmentRoles.INTERNAL_MODERATOR)) {
            throw new RuntimeException("User is not an internal moderator for this assessment");
        }

        assessment.getInternalModerator().setInternalModeratorSignature(signature);
        assessment.getInternalModerator().setInternalModeratorSignatureDate(LocalDate.now());
        assessmentRepository.save(assessment);
    }

    @Transactional
    public void setModuleAssessmentLeadSignature(Long assessmentId, Long userId, String signature) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByAssessmentAndUserAndRole(assessment, user, AssessmentRoles.MODULE_ASSESSMENT_LEAD)) {
            throw new RuntimeException("User is not the module assessment lead for this assessment");
        }

        assessment.setModuleAssessmentLeadSignature(signature);
        assessment.setModuleAssessmentLeadSignatureDate(LocalDate.now());
        assessmentRepository.save(assessment);
    }

    // The following methods have been removed or modified as they reference non-existent relationships:
    // - addQuestionToInternalModerator
    // - getQuestionsForInternalModerator
    // - updateExternalExaminerResponse
    // - updateProgrammeDirectorConfirmation
}