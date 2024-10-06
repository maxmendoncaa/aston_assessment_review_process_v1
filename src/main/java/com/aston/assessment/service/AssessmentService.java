package com.aston.assessment.service;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.AssessmentUpdateDTO;
import com.aston.assessment.model.*;
import com.aston.assessment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Check if the role is already assigned for roles that should be unique
        if ((role == AssessmentRoles.PROGRAMME_DIRECTOR || role == AssessmentRoles.MODULE_ASSESSMENT_LEAD) &&
                participantRepository.existsByAssessmentAndRolesContaining(assessment, role)) {
            throw new RuntimeException("This unique role is already assigned for this assessment");
        }

        // Find existing participant or create a new one
        AssessmentParticipant participant = participantRepository.findByAssessmentAndUser(assessment, user)
                .orElse(new AssessmentParticipant());

        if (participant.getId() == null) {
            // This is a new participant
            participant.setAssessment(assessment);
            participant.setUser(user);
            participant.setRoles(new HashSet<>());
        }

        // Add the new role to the participant's roles
        participant.getRoles().add(role);

        participantRepository.save(participant);
    }

    @Transactional
    public void addExternalExaminerResponse(Long assessmentId, Long userId, String comments, boolean reviewAgain) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!participantRepository.existsByAssessmentAndUserAndRoles(assessment, user, AssessmentRoles.EXTERNAL_EXAMINER)) {
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

        if (!participantRepository.existsByAssessmentAndUserAndRoles(assessment, user, AssessmentRoles.PROGRAMME_DIRECTOR)) {
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

        if (!participantRepository.existsByAssessmentAndUserAndRoles(assessment, user, AssessmentRoles.INTERNAL_MODERATOR)) {
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

        if (!participantRepository.existsByAssessmentAndUserAndRoles(assessment, user, AssessmentRoles.MODULE_ASSESSMENT_LEAD)) {
            throw new RuntimeException("User is not the module assessment lead for this assessment");
        }

        assessment.setModuleAssessmentLeadSignature(signature);
        assessment.setModuleAssessmentLeadSignatureDate(LocalDateTime.now());
        assessmentRepository.save(assessment);
    }
//    public AssessmentDTO getAssessmentForUser(Long id, String userEmail) {
//        Assessment assessment = assessmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Assessment not found"));
//        Users user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        AssessmentParticipant participant = assessment.getParticipants().stream()
//                .filter(p -> p.getUser().equals(user))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("User is not a participant of this assessment"));
//
//        return mapToAssessmentDTO(assessment, participant.getRole());
//    }
//
//    @Transactional
//    public AssessmentDTO updateAssessment(Long id, AssessmentUpdateDTO updateDTO, String userEmail) {
//        Assessment assessment = assessmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Assessment not found"));
//        Users user = userRepository.findByEmail(userEmail)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        AssessmentParticipant participant = assessment.getParticipants().stream()
//                .filter(p -> p.getUser().equals(user))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("User is not a participant of this assessment"));
//
//        updateAssessmentBasedOnRole(assessment, updateDTO, participant.getRole());
//
//        Assessment savedAssessment = assessmentRepository.save(assessment);
//        return mapToAssessmentDTO(savedAssessment, participant.getRole());
//    }
//
//    private void updateAssessmentBasedOnRole(Assessment assessment, AssessmentUpdateDTO updateDTO, AssessmentRoles role) {
//        switch (role) {
//            case MODULE_ASSESSMENT_LEAD:
//                assessment.setTotalSubmissions(updateDTO.getTotalSubmissions());
//                assessment.setFailedSubmissions(updateDTO.getFailedSubmissions());
//                break;
//            case INTERNAL_MODERATOR:
//                assessment.getInternalModerator().setGeneralComment(updateDTO.getGeneralComment());
//                break;
//            case EXTERNAL_EXAMINER:
//                // Update external examiner specific fields
//                break;
//            case PROGRAMME_DIRECTOR:
//                // Update programme director specific fields
//                break;
//        }
//    }
//
//    private AssessmentDTO mapToAssessmentDTO(Assessment assessment, AssessmentRoles userRole) {
//        AssessmentDTO dto = new AssessmentDTO();
//        dto.setId(assessment.getId());
//        dto.setTitle(assessment.getTitle());
//        dto.setAssessmentCategory(assessment.getAssessmentCategory());
//        dto.setAssessmentWeighting(assessment.getAssessmentWeighting());
//        dto.setPlannedIssueDate(assessment.getPlannedIssueDate());
//        dto.setCourseworkSubmissionDate(assessment.getCourseworkSubmissionDate());
//        dto.setUserRole(String.valueOf(userRole));
//
//        // Add role-specific fields
//        switch (userRole) {
//            case MODULE_ASSESSMENT_LEAD:
//                //dto.setTotalSubmissions(assessment.getTotalSubmissions());
//                //dto.setFailedSubmissions(assessment.getFailedSubmissions());
//                break;
//            case INTERNAL_MODERATOR:
//                if (assessment.getInternalModerator() != null) {
//                  //  dto.setGeneralComment(assessment.getInternalModerator().getGeneralComment());
//                }
//                break;
//            // Add cases for other roles as needed
//        }
//
//        return dto;
//    }

    public AssessmentDTO getAssessmentForUser(Long id, String userEmail) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssessmentParticipant participant = participantRepository.findByAssessmentAndUser(assessment, user)
                .orElseThrow(() -> new RuntimeException("User is not a participant of this assessment"));

        return mapToAssessmentDTO(assessment, participant.getRoles());
    }

    @Transactional
    public AssessmentDTO updateAssessment(Long id, AssessmentUpdateDTO updateDTO, String userEmail) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssessmentParticipant participant = participantRepository.findByAssessmentAndUser(assessment, user)
                .orElseThrow(() -> new RuntimeException("User is not a participant of this assessment"));

        updateAssessmentBasedOnRoles(assessment, updateDTO, participant.getRoles());

        Assessment savedAssessment = assessmentRepository.save(assessment);
        return mapToAssessmentDTO(savedAssessment, participant.getRoles());
    }

    private void updateAssessmentBasedOnRoles(Assessment assessment, AssessmentUpdateDTO updateDTO, Set<AssessmentRoles> roles) {
        if (roles.contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD)) {
            updateAssessmentDetails(assessment, updateDTO);
        }
        if (roles.contains(AssessmentRoles.INTERNAL_MODERATOR)) {
            updateInternalModeration(assessment, updateDTO);
        }
        if (roles.contains(AssessmentRoles.EXTERNAL_EXAMINER)) {
            updateExternalExaminerReview(assessment, updateDTO);
        }
        if (roles.contains(AssessmentRoles.PROGRAMME_DIRECTOR)) {
            updateProgrammeDirectorConfirmation(assessment, updateDTO);
        }
    }

    private void updateAssessmentDetails(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        assessment.setTitle(updateDTO.getTitle());
        //assessment.setModuleCode(updateDTO.getModuleCode());
        assessment.setAssessmentWeighting(updateDTO.getAssessmentWeighting());
        assessment.setAssessmentCategory(updateDTO.getAssessmentCategory());
        assessment.setModuleAssessmentLeadSignature("Signed");
        assessment.setModuleAssessmentLeadSignatureDate(LocalDateTime.now());
    }

    private void updateInternalModeration(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        assessment.getInternalModerator().setGeneralComment(updateDTO.getInternalModeratorComments());
        assessment.getInternalModerator().setInternalModeratorSignature("Signed");
        assessment.getInternalModerator().setInternalModeratorSignatureDate(LocalDateTime.now().toLocalDate());
    }

    private void updateExternalExaminerReview(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        ExternalExaminerResponse response = new ExternalExaminerResponse();
        response.setComments(updateDTO.getExternalExaminerComments());
        response.setReviewAssessmentAgain(updateDTO.getExternalExaminerApproval().equals("NEEDS_REVISION"));
        response.setSignatureDate(LocalDateTime.now().toLocalDate());
        assessment.getExternalExaminerResponses().add(response);
    }

    private void updateProgrammeDirectorConfirmation(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
        if (confirmation == null) {
            confirmation = new ProgrammeDirectorConfirmation();
            assessment.setProgrammeDirectorConfirmation(confirmation);
        }
        confirmation.setAppropriatelyResponded(updateDTO.getProgrammeDirectorApproval().equals("APPROVED"));
        confirmation.setSignatureDate(LocalDateTime.now().toLocalDate());
    }

    private AssessmentDTO mapToAssessmentDTO(Assessment assessment, Set<AssessmentRoles> userRoles) {
        AssessmentDTO dto = new AssessmentDTO();
        // Map basic fields
        dto.setId(assessment.getId());
        dto.setTitle(assessment.getTitle());
        dto.setAssessmentCategory(assessment.getAssessmentCategory());
        dto.setAssessmentWeighting(assessment.getAssessmentWeighting());
        dto.setPlannedIssueDate(assessment.getPlannedIssueDate());
        dto.setCourseworkSubmissionDate(assessment.getCourseworkSubmissionDate());
        dto.setUserRoles(userRoles.stream().map(Enum::name).collect(Collectors.toSet()));

        // Map role-specific fields
        if (userRoles.contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD)) {
            dto.setModuleAssessmentLeadSignature(assessment.getModuleAssessmentLeadSignature());
            dto.setModuleAssessmentLeadSignatureDate(assessment.getModuleAssessmentLeadSignatureDate());
        }
        if (userRoles.contains(AssessmentRoles.INTERNAL_MODERATOR) && assessment.getInternalModerator() != null) {
            dto.setInternalModeratorComments(assessment.getInternalModerator().getGeneralComment());
            dto.setInternalModeratorSignature(assessment.getInternalModerator().getInternalModeratorSignature());
            dto.setInternalModeratorSignatureDate(assessment.getInternalModerator().getInternalModeratorSignatureDate());
        }
        if (userRoles.contains(AssessmentRoles.EXTERNAL_EXAMINER) && !assessment.getExternalExaminerResponses().isEmpty()) {
            ExternalExaminerResponse latestResponse = assessment.getExternalExaminerResponses().get(assessment.getExternalExaminerResponses().size() - 1);
            dto.setExternalExaminerComments(latestResponse.getComments());
            dto.setExternalExaminerApproval(latestResponse.isReviewAssessmentAgain() ? "NEEDS_REVISION" : "APPROVED");
            dto.setExternalExaminerSignatureDate(latestResponse.getSignatureDate());
        }
        if (userRoles.contains(AssessmentRoles.PROGRAMME_DIRECTOR) && assessment.getProgrammeDirectorConfirmation() != null) {
            dto.setProgrammeDirectorApproval(assessment.getProgrammeDirectorConfirmation().isAppropriatelyResponded() ? "APPROVED" : "NEEDS_REVISION");
            dto.setProgrammeDirectorSignatureDate(assessment.getProgrammeDirectorConfirmation().getSignatureDate());
        }



        return dto;
    }
}