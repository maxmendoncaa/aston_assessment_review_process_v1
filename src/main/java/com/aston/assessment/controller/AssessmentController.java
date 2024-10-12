package com.aston.assessment.controller;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.AssessmentParticipantDTO;
import com.aston.assessment.DTO.AssessmentUpdateDTO;
import com.aston.assessment.model.*;
import com.aston.assessment.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping
    public ResponseEntity<Assessment> createAssessment(@RequestBody Assessment assessment) {
        Assessment createdAssessment = assessmentService.createAssessment(assessment);
        return ResponseEntity.ok(createdAssessment);
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<Void> addParticipant(@PathVariable Long id, @RequestParam Long userId, @RequestParam AssessmentRoles role) {
        assessmentService.addParticipantToAssessment(id, userId, role);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/stage2")
    public ResponseEntity<Void> moveToStage2(@PathVariable Long id) {
        assessmentService.moveToStage2(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<Void> addQuestion(@PathVariable Long id, @RequestBody Question question) {
        assessmentService.addQuestionToAssessment(id, question);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable Long id) {
        List<Question> questions = assessmentService.getQuestionsForAssessment(id);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/{id}/external-examiner")
    public ResponseEntity<Void> addExternalExaminerResponse(@PathVariable Long id,
                                                            @RequestParam Long userId,
                                                            @RequestParam String comments,
                                                            @RequestParam boolean reviewAgain) {
        assessmentService.addExternalExaminerResponse(id, userId, comments, reviewAgain);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/programme-director")
    public ResponseEntity<Void> addProgrammeDirectorConfirmation(@PathVariable Long id,
                                                                 @RequestParam Long userId,
                                                                 @RequestParam boolean appropriatelyResponded) {
        assessmentService.addProgrammeDirectorConfirmation(id, userId, appropriatelyResponded);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/internal-moderator-signature")
    public ResponseEntity<Void> setInternalModeratorSignature(@PathVariable Long id,
                                                              @RequestParam Long userId,
                                                              @RequestParam String signature) {
        assessmentService.setInternalModeratorSignature(id, userId, signature);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/module-assessment-lead-signature")
    public ResponseEntity<Void> setModuleAssessmentLeadSignature(@PathVariable Long id,
                                                                 @RequestParam Long userId,
                                                                 @RequestBody String signature) {
        assessmentService.setModuleAssessmentLeadSignature(id, userId, signature);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<AssessmentDTO> getAssessment(@PathVariable Long id, Authentication authentication) {
//        String userEmail = authentication.getName();
//        AssessmentDTO assessment = assessmentService.getAssessmentForUser(id, userEmail);
//        return ResponseEntity.ok(assessment);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<AssessmentDTO> updateAssessment(
//            @PathVariable Long id,
//            @RequestBody AssessmentUpdateDTO updateDTO,
//            Authentication authentication) {
//        String userEmail = authentication.getName();
//        AssessmentDTO updatedAssessment = assessmentService.updateAssessment(id, updateDTO, userEmail);
//        return ResponseEntity.ok(updatedAssessment);
//    }
        @GetMapping("/{id}")
        public ResponseEntity<AssessmentDTO> getAssessment(@PathVariable Long id) {
            AssessmentDTO assessment = assessmentService.getAssessmentById(id);
            return ResponseEntity.ok(assessment);
        }

            @PutMapping("/{id}")
            public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable Long id, @RequestBody AssessmentDTO assessmentDTO) {

                AssessmentDTO updatedAssessment = assessmentService.updateAssessment(id, assessmentDTO);
                return ResponseEntity.ok(updatedAssessment);
            }


    @GetMapping("/{assessmentId}/participants")
    public ResponseEntity<List<AssessmentParticipantDTO>> getAssessmentParticipants(@PathVariable Long assessmentId) {
        List<AssessmentParticipantDTO> participants = assessmentService.getAssessmentParticipants(assessmentId);
        return ResponseEntity.ok(participants);
    }
}