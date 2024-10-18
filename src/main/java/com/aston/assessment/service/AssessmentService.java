package com.aston.assessment.service;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.AssessmentParticipantDTO;
import com.aston.assessment.DTO.AssessmentUpdateDTO;
import com.aston.assessment.model.*;
import com.aston.assessment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
       // response.setUser(user);
        response.setExternal_examiner_comments(comments);
        response.setReviewAssessmentAgain(reviewAgain);
        response.setExternalExaminer_signatureDateTime(LocalDateTime.now());

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
       // confirmation.setUser(user);
        confirmation.setAppropriatelyResponded(appropriatelyResponded);
        confirmation.setProgrammeDirectorConfirmation_signatureDateTime(LocalDateTime.now());

        programmeDirectorConfirmationRepository.save(confirmation);
    }

    @Transactional
    public void moveToStage2(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (assessment.getStage() != AssessmentStage.STAGE_1) {
            throw new RuntimeException("Assessment is not in Stage 1");
        }

        if (assessment.getModuleAssessmentLead().getModuleAssessmentLeadSignature() == null ||
                assessment.getModuleAssessmentLead().getModuleAssessmentLeadSignatureDateTime() == null) {
            throw new RuntimeException("Module Assessment Lead signature is required to move to Stage 2");
        }

        if (assessment.getInternalModerator().getInternalModeratorSignature() == null ||
                assessment.getInternalModerator().getInternalModeratorSignatureDateTime() == null) {
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
        assessment.getInternalModerator().setInternalModeratorSignatureDateTime(LocalDateTime.now());
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

        assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignature(signature);
        assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignatureDateTime(LocalDateTime.now());
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
       // assessment.setModuleCode(updateDTO.getModuleCode());
        assessment.setAssessmentWeighting(updateDTO.getAssessmentWeighting());
        assessment.setAssessmentCategory(updateDTO.getAssessmentCategory());
        assessment.setPlannedIssueDate(updateDTO.getPlannedIssueDate());
        assessment.setCourseworkSubmissionDate(updateDTO.getCourseworkSubmissionDate());
        assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignature("Signed");
        assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignatureDateTime(LocalDateTime.now());

    }

    private void updateInternalModeration(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        assessment.getInternalModerator().setGeneralComment(updateDTO.getInternalModeratorComments());
        assessment.getInternalModerator().setInternalModeratorSignature("Signed");
        assessment.getInternalModerator().setInternalModeratorSignatureDateTime(LocalDateTime.now());
    }

    private void updateExternalExaminerReview(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        ExternalExaminerResponse response = new ExternalExaminerResponse();
        response.setExternal_examiner_comments(updateDTO.getExternalExaminerComments());
        response.setReviewAssessmentAgain(updateDTO.getExternalExaminerApproval().equals("NEEDS_REVISION"));
        response.setExternalExaminer_signatureDateTime(LocalDateTime.now());
       // assessment.getExternalExaminerResponses();
    }

    private void updateProgrammeDirectorConfirmation(Assessment assessment, AssessmentUpdateDTO updateDTO) {
        ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
        if (confirmation == null) {
            confirmation = new ProgrammeDirectorConfirmation();
            assessment.setProgrammeDirectorConfirmation(confirmation);
        }
        confirmation.setAppropriatelyResponded(updateDTO.getProgrammeDirectorApproval().equals("APPROVED"));
        confirmation.setProgrammeDirectorConfirmation_signatureDateTime(LocalDateTime.now());
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
            dto.setModuleAssessmentLeadSignature(assessment.getModuleAssessmentLead().getModuleAssessmentLeadSignature());
            dto.setModuleAssessmentLeadSignatureDateTime(assessment.getModuleAssessmentLead().getModuleAssessmentLeadSignatureDateTime());
        }
        if (userRoles.contains(AssessmentRoles.INTERNAL_MODERATOR) && assessment.getInternalModerator() != null) {
            dto.setInternalModeratorComments(assessment.getInternalModerator().getGeneralComment());
            dto.setInternalModeratorSignature(assessment.getInternalModerator().getInternalModeratorSignature());
            dto.setInternalModeratorSignatureDateTime(assessment.getInternalModerator().getInternalModeratorSignatureDateTime());
        }
        if (userRoles.contains(AssessmentRoles.EXTERNAL_EXAMINER) && assessment.getExternalExaminerResponses()!=null) {
            ExternalExaminerResponse latestResponse = assessment.getExternalExaminerResponses();
            dto.setExternalExaminerComments(latestResponse.getExternal_examiner_comments());
            dto.setExternalExaminerApproval(latestResponse.isReviewAssessmentAgain() ? "NEEDS_REVISION" : "APPROVED");
            dto.setExternalExaminerSignatureDateTime(LocalDateTime.from(latestResponse.getExternalExaminer_signatureDateTime()));
        }
        if (userRoles.contains(AssessmentRoles.PROGRAMME_DIRECTOR) && assessment.getProgrammeDirectorConfirmation() != null) {
            dto.setProgrammeDirectorApproval(assessment.getProgrammeDirectorConfirmation().isAppropriatelyResponded() ? "APPROVED" : "NEEDS_REVISION");
            dto.setProgrammeDirectorSignatureDateTime(assessment.getProgrammeDirectorConfirmation().getProgrammeDirectorConfirmation_signatureDateTime());
        }



        return dto;
    }
    public AssessmentDTO getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + id));
        return mapToDTO(assessment);
    }

//    @Transactional
//    public AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO) {
//        Assessment assessment = assessmentRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + id));
//
//        // Update fields based on user roles
//        Set<String> userRoles = assessmentDTO.getUserRoles();
//
//        if (userRoles.contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD.name())) {
//            assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignature(assessmentDTO.getModuleAssessmentLeadSignature());
//            assessment.getModuleAssessmentLead().setModuleAssessmentLeadSignatureDateTime(assessmentDTO.getModuleAssessmentLeadSignatureDateTime());
//            assessment.setSkills(assessmentDTO.getSkills());
//            assessment.getModuleAssessmentLead().setResponseToInternalModerator(assessmentDTO.getResponseToInternalModerator());
//            assessment.getModuleAssessmentLead().setResponseToInternalModeratorDateTime(assessmentDTO.getResponseToInternalModeratorDateTime());
//            assessment.getModuleAssessmentLead().setResponseToExternalExaminer(assessmentDTO.getResponseToExternalExaminer());
//            assessment.getModuleAssessmentLead().setResponseToExternalExaminerDateTime(assessmentDTO.getResponseToExternalExaminerDateTime());
//            assessment.getModuleAssessmentLead().setStage2_assessmentLeadComments(assessmentDTO.getStage2_assessmentLeadComments());
//            assessment.getModuleAssessmentLead().setStage2ModuleAssessmentLeadSignatureDateTime(assessmentDTO.getStage2ModuleAssessmentLeadSignatureDateTime());
//
//            // Stage 2 fields
//            assessment.setAssessmentDeadline(assessmentDTO.getAssessmentDeadline());
//            assessment.setTotalSubmissions(assessmentDTO.getTotalSubmissions());
//            assessment.setFailedSubmissions(assessmentDTO.getFailedSubmissions());
//            assessment.setModeratedSubmissions(assessmentDTO.getModeratedSubmissions());
//            assessment.setTeachingImpactDetails(assessmentDTO.getTeachingImpactDetails());
//
////            assessment.setModuleAssessmentLeadSignatureDateTime(
////                    assessmentDTO.getModuleAssessmentLeadSignatureDateTime() != null
////                            ? assessmentDTO.getModuleAssessmentLeadSignatureDateTime()
////                            : LocalDateTime.now()
////            );
//        }
//
//        if (userRoles.contains(AssessmentRoles.INTERNAL_MODERATOR.name())) {
//            InternalModerator moderator = assessment.getInternalModerator();
//            if (moderator == null) {
//                moderator = new InternalModerator();
//                moderator.setAssessment(assessment);
//                assessment.setInternalModerator(moderator);
//            }
//            moderator.setGeneralComment(assessmentDTO.getInternalModeratorComments());
//            moderator.setInternalModeratorSignature(assessmentDTO.getInternalModeratorSignature());
//            moderator.setInternalModeratorSignatureDateTime(assessmentDTO.getInternalModeratorSignatureDateTime());
//            moderator.setStage2_moderatorComments(assessmentDTO.getStage2_moderatorComments());
//        }
//
//        if (userRoles.contains(AssessmentRoles.EXTERNAL_EXAMINER.name())) {
//            ExternalExaminerResponse response = new ExternalExaminerResponse();
//            response.setExternal_examiner_comments(assessmentDTO.getExternalExaminerComments());
//            response.setReviewAssessmentAgain(assessmentDTO.getExternalExaminerApproval().equals("NEEDS_REVISION"));
//            response.setExternalExaminer_signatureDateTime(assessmentDTO.getExternalExaminerSignatureDateTime());
//            response.setExternalExaminer_signature(assessmentDTO.getExternalExaminer_signature());
//            assessment.getExternalExaminerResponses().add(response);
//        }
//
//        if (userRoles.contains(AssessmentRoles.PROGRAMME_DIRECTOR.name())) {
//            ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
//            if (confirmation == null) {
//                confirmation = new ProgrammeDirectorConfirmation();
//                assessment.setProgrammeDirectorConfirmation(confirmation);
//            }
//            confirmation.setAppropriatelyResponded(assessmentDTO.getProgrammeDirectorApproval().equals("APPROVED"));
//            confirmation.setProgrammeDirectorConfirmation_signatureDateTime(assessmentDTO.getProgrammeDirectorSignatureDateTime());
//            confirmation.setProgrammeDirectorConfirmation_signature(assessmentDTO.getProgrammeDirectorConfirmation_signature());
//            confirmation.setProgrammeDirectorConfirmation_signature_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signature_stage2());
//            confirmation.setProgrammeDirectorConfirmation_signatureDateTime_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signatureDateTime_stage2());
//        }
//
//        assessment.setMarkingCompletedDate(assessmentDTO.getMarkingCompletedDate());
//        assessment.setModerationCompletedDate(assessmentDTO.getModerationCompletedDate());
//        assessment.getInternalModerator().setModeratorSignatureDateTime(assessmentDTO.getModeratorSignatureDateTime());
//
//        // Update triggers
//        assessment.setAssessmentDetailsTrigger(assessmentDTO.getAssessmentDetailsTrigger());
//        assessment.setInternalModeratorDetailsTrigger(assessmentDTO.getInternalModeratorDetailsTrigger());
//        assessment.setExternalExaminerDetailsTrigger(assessmentDTO.getExternalExaminerDetailsTrigger());
//        assessment.setProgrammeDirectorDetailsTrigger(assessmentDTO.getProgrammeDirectorDetailsTrigger());
//        assessment.setInternalModeratorModerationOfMarksTrigger(assessmentDTO.getInternalModeratorModerationOfMarksTrigger());
//        assessment.setStage2ModuleAssessmentLeadCommentsTrigger(assessmentDTO.getStage2ModuleAssessmentLeadCommentsTrigger());
//        assessment.setStage2ModeratorCommentsTrigger(assessmentDTO.getStage2ModeratorCommentsTrigger());
//
//        Assessment updatedAssessment = assessmentRepository.save(assessment);
//        return mapToDTO(updatedAssessment);
//    }


// //Last working code as of 13th October
//@Transactional
//public AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO) {
//    Assessment assessment = assessmentRepository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + id));
//
//    // Update fields based on user roles
//    Set<String> userRoles = assessmentDTO.getUserRoles();
//
//    if (userRoles.contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD.name())) {
//        if (assessment.getModuleAssessmentLead() == null) {
//            ModuleAssessmentLead newLead = new ModuleAssessmentLead();
//            newLead.setAssessment(assessment);
//            assessment.setModuleAssessmentLead(newLead);
//        }
//
//        ModuleAssessmentLead lead = assessment.getModuleAssessmentLead();
//        lead.setModuleAssessmentLeadSignature(assessmentDTO.getModuleAssessmentLeadSignature());
//        lead.setModuleAssessmentLeadSignatureDateTime(assessmentDTO.getModuleAssessmentLeadSignatureDateTime());
//        lead.setResponseToInternalModerator(assessmentDTO.getResponseToInternalModerator());
//        lead.setResponseToInternalModeratorDateTime(assessmentDTO.getResponseToInternalModeratorDateTime());
//        lead.setResponseToExternalExaminer(assessmentDTO.getResponseToExternalExaminer());
//        lead.setResponseToExternalExaminerDateTime(assessmentDTO.getResponseToExternalExaminerDateTime());
//        lead.setStage2_assessmentLeadComments(assessmentDTO.getStage2_assessmentLeadComments());
//
//        assessment.setSkills(assessmentDTO.getSkills());
//        assessment.setAssessmentDeadline(assessmentDTO.getAssessmentDeadline());
//        assessment.setTotalSubmissions(assessmentDTO.getTotalSubmissions());
//        assessment.setFailedSubmissions(assessmentDTO.getFailedSubmissions());
//        assessment.setModeratedSubmissions(assessmentDTO.getModeratedSubmissions());
//        assessment.setTeachingImpactDetails(assessmentDTO.getTeachingImpactDetails());
//    }
//
//    if (userRoles.contains(AssessmentRoles.INTERNAL_MODERATOR.name())) {
//        InternalModerator moderator = assessment.getInternalModerator();
//        if (moderator == null) {
//            moderator = new InternalModerator();
//            moderator.setAssessment(assessment);
//            assessment.setInternalModerator(moderator);
//        }
//        moderator.setGeneralComment(assessmentDTO.getInternalModeratorComments());
//        moderator.setInternalModeratorSignature(assessmentDTO.getInternalModeratorSignature());
//        moderator.setInternalModeratorSignatureDateTime(assessmentDTO.getInternalModeratorSignatureDateTime());
//        moderator.setStage2_moderatorComments(assessmentDTO.getStage2_moderatorComments());
//    }
//
//    if (userRoles.contains(AssessmentRoles.EXTERNAL_EXAMINER.name())) {
//        ExternalExaminerResponse response = new ExternalExaminerResponse();
//        response.setExternal_examiner_comments(assessmentDTO.getExternalExaminerComments());
//        // Add null check here
//        String externalExaminerApproval = assessmentDTO.getExternalExaminerApproval();
//        response.setReviewAssessmentAgain(externalExaminerApproval != null && externalExaminerApproval.equals("NEEDS_REVISION"));
//
//        //response.setReviewAssessmentAgain(assessmentDTO.getExternalExaminerApproval().equals("NEEDS_REVISION"));
//        response.setExternalExaminer_signatureDateTime(assessmentDTO.getExternalExaminerSignatureDateTime());
//        response.setExternalExaminer_signature(assessmentDTO.getExternalExaminer_signature());
//        response.setAssessment(assessment);
////        Users currentUser = getCurrentUser(); // You need to implement this method
////        response.setUser(currentUser);
//     //   assessment.getExternalExaminerResponses().add(response);
//
//    }
//
//    if (userRoles.contains(AssessmentRoles.PROGRAMME_DIRECTOR.name())) {
//        ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
//        if (confirmation == null) {
//            confirmation = new ProgrammeDirectorConfirmation();
//            confirmation.setAssessment(assessment);
//            assessment.setProgrammeDirectorConfirmation(confirmation);
//        }
//        // Add null check here
//        String programmeDirectorApproval = assessmentDTO.getProgrammeDirectorApproval();
//        confirmation.setAppropriatelyResponded(programmeDirectorApproval != null && programmeDirectorApproval.equals("APPROVED"));
//
//       // confirmation.setAppropriatelyResponded(assessmentDTO.getProgrammeDirectorApproval().equals("APPROVED"));
//        confirmation.setProgrammeDirectorConfirmation_signatureDateTime(assessmentDTO.getProgrammeDirectorSignatureDateTime());
//        confirmation.setProgrammeDirectorConfirmation_signature(assessmentDTO.getProgrammeDirectorConfirmation_signature());
//        confirmation.setProgrammeDirectorConfirmation_signature_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signature_stage2());
//        confirmation.setProgrammeDirectorConfirmation_signatureDateTime_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signatureDateTime_stage2());
//    }
//
//    assessment.setMarkingCompletedDate(assessmentDTO.getMarkingCompletedDate());
//    assessment.setModerationCompletedDate(assessmentDTO.getModerationCompletedDate());
//
//    // Update triggers
//    assessment.setAssessmentDetailsTrigger(assessmentDTO.getAssessmentDetailsTrigger());
//    assessment.setInternalModeratorDetailsTrigger(assessmentDTO.getInternalModeratorDetailsTrigger());
//    assessment.setExternalExaminerDetailsTrigger(assessmentDTO.getExternalExaminerDetailsTrigger());
//    assessment.setProgrammeDirectorDetailsTrigger(assessmentDTO.getProgrammeDirectorDetailsTrigger());
//    assessment.setInternalModeratorModerationOfMarksTrigger(assessmentDTO.getInternalModeratorModerationOfMarksTrigger());
//    assessment.setStage2ModuleAssessmentLeadCommentsTrigger(assessmentDTO.getStage2ModuleAssessmentLeadCommentsTrigger());
//    assessment.setStage2ModeratorCommentsTrigger(assessmentDTO.getStage2ModeratorCommentsTrigger());
//
//    Assessment updatedAssessment = assessmentRepository.save(assessment);
//    return mapToDTO(updatedAssessment);
//}
//working code ends here
@Transactional
public AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO) {
    Assessment assessment = assessmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Assessment not found with id: " + id));

    Set<String> userRoles = assessmentDTO.getUserRoles();

    // Update common fields
    assessment.setTitle(assessmentDTO.getTitle());
    assessment.setAssessmentCategory(assessmentDTO.getAssessmentCategory());
    assessment.setAssessmentWeighting(assessmentDTO.getAssessmentWeighting());
    assessment.setPlannedIssueDate(assessmentDTO.getPlannedIssueDate());
    assessment.setCourseworkSubmissionDate(assessmentDTO.getCourseworkSubmissionDate());

    // MODULE_ASSESSMENT_LEAD updates
    if (userRoles.contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD.name())) {
        updateModuleAssessmentLeadFields(assessment, assessmentDTO);
    }

    // INTERNAL_MODERATOR updates
    if (userRoles.contains(AssessmentRoles.INTERNAL_MODERATOR.name())) {
        updateInternalModeratorFields(assessment, assessmentDTO);
    }

    // EXTERNAL_EXAMINER updates
    if (userRoles.contains(AssessmentRoles.EXTERNAL_EXAMINER.name())) {
        updateExternalExaminerFields(assessment, assessmentDTO);
    }

    // PROGRAMME_DIRECTOR updates
    if (userRoles.contains(AssessmentRoles.PROGRAMME_DIRECTOR.name())) {
        updateProgrammeDirectorFields(assessment, assessmentDTO);
    }

    // Update triggers
    updateTriggers(assessment, assessmentDTO);

    // Save the updated assessment
    Assessment updatedAssessment = assessmentRepository.save(assessment);

    // Map the updated assessment back to DTO
    return mapToDTO(updatedAssessment);
}

    private void updateModuleAssessmentLeadFields(Assessment assessment, AssessmentDTO assessmentDTO) {
        if (assessment.getModuleAssessmentLead() == null) {
            ModuleAssessmentLead newLead = new ModuleAssessmentLead();
            newLead.setAssessment(assessment);
            assessment.setModuleAssessmentLead(newLead);
        }

        ModuleAssessmentLead lead = assessment.getModuleAssessmentLead();
        lead.setModuleAssessmentLeadSignature(assessmentDTO.getModuleAssessmentLeadSignature());
        lead.setModuleAssessmentLeadSignatureDateTime(assessmentDTO.getModuleAssessmentLeadSignatureDateTime());
        lead.setResponseToInternalModerator(assessmentDTO.getResponseToInternalModerator());
        lead.setResponseToInternalModeratorDateTime(assessmentDTO.getResponseToInternalModeratorDateTime());
        lead.setResponseToExternalExaminer(assessmentDTO.getResponseToExternalExaminer());
        lead.setResponseToExternalExaminerDateTime(assessmentDTO.getResponseToExternalExaminerDateTime());
        lead.setStage2_assessmentLeadComments(assessmentDTO.getStage2_assessmentLeadComments());

        assessment.setSkills(assessmentDTO.getSkills());
        assessment.setAssessmentDeadline(assessmentDTO.getAssessmentDeadline());
        assessment.setTotalSubmissions(assessmentDTO.getTotalSubmissions());
        assessment.setFailedSubmissions(assessmentDTO.getFailedSubmissions());
        assessment.setModeratedSubmissions(assessmentDTO.getModeratedSubmissions());
        assessment.setTeachingImpactDetails(assessmentDTO.getTeachingImpactDetails());
        assessment.setMarkingCompletedDate(assessmentDTO.getMarkingCompletedDate());
        assessment.setModerationCompletedDate(assessmentDTO.getModerationCompletedDate());
    }

    private void updateInternalModeratorFields(Assessment assessment, AssessmentDTO assessmentDTO) {
        InternalModerator moderator = assessment.getInternalModerator();
        if (moderator == null) {
            moderator = new InternalModerator();
            moderator.setAssessment(assessment);
            assessment.setInternalModerator(moderator);
        }
        moderator.setGeneralComment(assessmentDTO.getInternalModeratorComments());
        moderator.setInternalModeratorSignature(assessmentDTO.getInternalModeratorSignature());
        moderator.setInternalModeratorSignatureDateTime(assessmentDTO.getInternalModeratorSignatureDateTime());
        moderator.setStage2_moderatorComments(assessmentDTO.getStage2_moderatorComments());
        moderator.setModeratorSignatureDateTime(assessmentDTO.getModeratorSignatureDateTime());
    }

    private void updateExternalExaminerFields(Assessment assessment, AssessmentDTO assessmentDTO) {
        ExternalExaminerResponse response = new ExternalExaminerResponse();
        response.setExternal_examiner_comments(assessmentDTO.getExternalExaminerComments());
        String externalExaminerApproval = assessmentDTO.getExternalExaminerApproval();
        response.setReviewAssessmentAgain(externalExaminerApproval != null && externalExaminerApproval.equals("NEEDS_REVISION"));
        response.setExternalExaminer_signatureDateTime(assessmentDTO.getExternalExaminerSignatureDateTime());
        response.setExternalExaminer_signature(assessmentDTO.getExternalExaminer_signature());
        //response.setAssessment(assessment);
       // assessment.setExternalExaminerResponses(response);
    }

    private void updateProgrammeDirectorFields(Assessment assessment, AssessmentDTO assessmentDTO) {
        ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
        if (confirmation == null) {
            confirmation = new ProgrammeDirectorConfirmation();
            confirmation.setAssessment(assessment);
            assessment.setProgrammeDirectorConfirmation(confirmation);
        }
        String programmeDirectorApproval = assessmentDTO.getProgrammeDirectorApproval();
        confirmation.setAppropriatelyResponded(programmeDirectorApproval != null && programmeDirectorApproval.equals("APPROVED"));
        confirmation.setProgrammeDirectorConfirmation_signatureDateTime(assessmentDTO.getProgrammeDirectorSignatureDateTime());
        confirmation.setProgrammeDirectorConfirmation_signature(assessmentDTO.getProgrammeDirectorConfirmation_signature());
        confirmation.setProgrammeDirectorConfirmation_signature_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signature_stage2());
        confirmation.setProgrammeDirectorConfirmation_signatureDateTime_stage2(assessmentDTO.getProgrammeDirectorConfirmation_signatureDateTime_stage2());
    }

    private void updateTriggers(Assessment assessment, AssessmentDTO assessmentDTO) {
        assessment.setAssessmentDetailsTrigger(assessmentDTO.getAssessmentDetailsTrigger());
        assessment.setInternalModeratorDetailsTrigger(assessmentDTO.getInternalModeratorDetailsTrigger());
        assessment.setResponseToInternalModeratorTrigger(assessmentDTO.getResponseToInternalModeratorTrigger());
        assessment.setExternalExaminerDetailsTrigger(assessmentDTO.getExternalExaminerDetailsTrigger());
        assessment.setResponseToExternalExaminerTrigger(assessmentDTO.getResponseToExternalExaminerTrigger());
        assessment.setProgrammeDirectorDetailsTrigger(assessmentDTO.getProgrammeDirectorDetailsTrigger());
        assessment.setInternalModeratorModerationOfMarksTrigger(assessmentDTO.getInternalModeratorModerationOfMarksTrigger());
        assessment.setStage2ModuleAssessmentLeadCommentsTrigger(assessmentDTO.getStage2ModuleAssessmentLeadCommentsTrigger());
        assessment.setStage2ModeratorCommentsTrigger(assessmentDTO.getStage2ModeratorCommentsTrigger());
    }

private AssessmentDTO mapToDTO(Assessment assessment) {
    AssessmentDTO dto = new AssessmentDTO();

    dto.setId(assessment.getId());
    dto.setTitle(assessment.getTitle());
    dto.setAssessmentCategory(assessment.getAssessmentCategory());
    dto.setSkills(assessment.getSkills());
    dto.setAssessmentWeighting(assessment.getAssessmentWeighting());
    dto.setPlannedIssueDate(assessment.getPlannedIssueDate());
    dto.setCourseworkSubmissionDate(assessment.getCourseworkSubmissionDate());

    if (assessment.getModule() != null) {
        dto.setModuleCode(assessment.getModule().getModuleCode());
        dto.setModuleLeader(assessment.getModule().getModuleLeader());
    }

    // Handle ModuleAssessmentLead fields
    if (assessment.getModuleAssessmentLead() != null) {
        ModuleAssessmentLead lead = assessment.getModuleAssessmentLead();
        dto.setModuleAssessmentLeadSignature(lead.getModuleAssessmentLeadSignature());
        dto.setModuleAssessmentLeadSignatureDateTime(lead.getModuleAssessmentLeadSignatureDateTime());
        dto.setResponseToInternalModerator(lead.getResponseToInternalModerator());
        dto.setResponseToInternalModeratorDateTime(lead.getResponseToInternalModeratorDateTime());
        dto.setResponseToExternalExaminer(lead.getResponseToExternalExaminer());
        dto.setResponseToExternalExaminerDateTime(lead.getResponseToExternalExaminerDateTime());
        dto.setStage2_assessmentLeadComments(lead.getStage2_assessmentLeadComments());
    }

    // Handle InternalModerator fields
    if (assessment.getInternalModerator() != null) {
        InternalModerator moderator = assessment.getInternalModerator();
        dto.setInternalModeratorComments(moderator.getGeneralComment());
        dto.setInternalModeratorSignature(moderator.getInternalModeratorSignature());
        dto.setInternalModeratorSignatureDateTime(moderator.getInternalModeratorSignatureDateTime());
        dto.setModeratorSignatureDateTime(moderator.getModeratorSignatureDateTime());
        dto.setStage2_moderatorComments(moderator.getStage2_moderatorComments());
    }

    // Handle ExternalExaminerResponse
    if (assessment.getExternalExaminerResponses()!=null) {
        ExternalExaminerResponse latestResponse = assessment.getExternalExaminerResponses();
        dto.setExternalExaminerComments(latestResponse.getExternal_examiner_comments());
        dto.setExternalExaminerApproval(latestResponse.isReviewAssessmentAgain() ? "NEEDS_REVISION" : "APPROVED");
        dto.setExternalExaminerSignatureDateTime(latestResponse.getExternalExaminer_signatureDateTime());
        dto.setExternalExaminer_signature(latestResponse.getExternalExaminer_signature());
    }

    // Handle ProgrammeDirectorConfirmation
    if (assessment.getProgrammeDirectorConfirmation() != null) {
        ProgrammeDirectorConfirmation confirmation = assessment.getProgrammeDirectorConfirmation();
        dto.setProgrammeDirectorApproval(confirmation.isAppropriatelyResponded() ? "APPROVED" : "NEEDS_REVISION");
        dto.setProgrammeDirectorSignatureDateTime(confirmation.getProgrammeDirectorConfirmation_signatureDateTime());
        dto.setProgrammeDirectorConfirmation_signature(confirmation.getProgrammeDirectorConfirmation_signature());
        dto.setProgrammeDirectorConfirmation_signature_stage2(confirmation.getProgrammeDirectorConfirmation_signature_stage2());
        dto.setProgrammeDirectorConfirmation_signatureDateTime_stage2(confirmation.getProgrammeDirectorConfirmation_signatureDateTime_stage2());
    }

    // Stage 2 fields
    dto.setAssessmentDeadline(assessment.getAssessmentDeadline());
    dto.setTotalSubmissions(assessment.getTotalSubmissions());
    dto.setFailedSubmissions(assessment.getFailedSubmissions());
    dto.setModeratedSubmissions(assessment.getModeratedSubmissions());
    dto.setTeachingImpactDetails(assessment.getTeachingImpactDetails());
    dto.setMarkingCompletedDate(assessment.getMarkingCompletedDate());
    dto.setModerationCompletedDate(assessment.getModerationCompletedDate());

    // Triggers
    dto.setAssessmentDetailsTrigger(assessment.getAssessmentDetailsTrigger());
    dto.setInternalModeratorDetailsTrigger(assessment.getInternalModeratorDetailsTrigger());
    dto.setResponseToInternalModeratorTrigger(assessment.getResponseToInternalModeratorTrigger());
    dto.setExternalExaminerDetailsTrigger(assessment.getExternalExaminerDetailsTrigger());
    dto.setResponseToExternalExaminerTrigger(assessment.getResponseToExternalExaminerTrigger());
    dto.setProgrammeDirectorDetailsTrigger(assessment.getProgrammeDirectorDetailsTrigger());
    dto.setInternalModeratorModerationOfMarksTrigger(assessment.getInternalModeratorModerationOfMarksTrigger());
    dto.setStage2ModuleAssessmentLeadCommentsTrigger(assessment.getStage2ModuleAssessmentLeadCommentsTrigger());
    dto.setStage2ModeratorCommentsTrigger(assessment.getStage2ModeratorCommentsTrigger());

    // Set user roles
    dto.setUserRoles(assessment.getParticipants().stream()
            .flatMap(participant -> participant.getRoles().stream())
            .map(Enum::name)
            .collect(Collectors.toSet()));

    // Set participants
    dto.setParticipants(assessment.getParticipants().stream()
            .map(this::mapToParticipantDTO)
            .collect(Collectors.toList()));

    return dto;
}
    private AssessmentParticipantDTO mapToParticipantDTO(AssessmentParticipant participant) {
        return new AssessmentParticipantDTO(
                participant.getUser().getUserId(),
                participant.getUser().getFirstName(),
                participant.getUser().getLastName(),
                new ArrayList<>(participant.getRoles())
        );
    }
//    public List<AssessmentParticipantDTO> getAssessmentParticipants(Long assessmentId) {
//        List<AssessmentParticipant> participants = participantRepository.findByAssessmentId(assessmentId);
//        return participants.stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }

    private AssessmentParticipantDTO mapToDTO(AssessmentParticipant participant) {
        return new AssessmentParticipantDTO(
                participant.getUser().getUserId(),
                participant.getUser().getFirstName(),
                participant.getUser().getLastName(),
                new ArrayList<>(participant.getRoles()) // Convert Set to List
        );
    }


    public List<AssessmentParticipantDTO> getAssessmentParticipants(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        return assessment.getParticipants().stream()
                .map(this::mapToAssessmentParticipantDTO)
                .collect(Collectors.toList());
    }

    private AssessmentParticipantDTO mapToAssessmentParticipantDTO(AssessmentParticipant participant) {
        return new AssessmentParticipantDTO(
                participant.getUser().getUserId(),
                participant.getUser().getFirstName(),
                participant.getUser().getLastName(),
                new ArrayList<>(participant.getRoles())
        );
    }


}
