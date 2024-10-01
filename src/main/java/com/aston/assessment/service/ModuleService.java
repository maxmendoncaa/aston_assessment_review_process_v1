package com.aston.assessment.service;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.AssessmentParticipantDTO;
import com.aston.assessment.DTO.ModuleWithAssessmentsDTO;
import com.aston.assessment.model.AssessmentParticipant;
import com.aston.assessment.model.Module;
import com.aston.assessment.model.Assessment;
import com.aston.assessment.model.Users;
import com.aston.assessment.repository.AssessmentParticipantRepository;
import com.aston.assessment.repository.ModuleRepository;
import com.aston.assessment.repository.AssessmentRepository;
import com.aston.assessment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    @Transactional
    public Module createModule(Module module) {
        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(Module module) {
        return moduleRepository.save(module);
    }

    @Transactional
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    public List<Assessment> getModuleAssessments(Long moduleId) {
        return assessmentRepository.findByModuleId(moduleId);
    }

    public List<Module> getModulesForUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return moduleRepository.findModulesByUser(user);
    }

    @Transactional
    public Assessment createAssessment(Long moduleId, Assessment assessment) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        assessment.setModule(module);
        return assessmentRepository.save(assessment);
    }

    @Transactional
    public void assignRole(Long moduleId, Long userId, String role) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Implement role assignment logic here
        // This might involve creating a new entity for module roles or updating an existing one
    }

    @Transactional
    public Module createModuleForUser(Module module, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (module.getModuleName() == null || module.getModuleCode() == null ||
                module.getCredits() == 0 || module.getLevel() == 0) {
            throw new IllegalArgumentException("All required fields must be provided");
        }

        // Check if module code already exists
        if (moduleRepository.existsByModuleCode(module.getModuleCode())) {
            throw new IllegalArgumentException("A module with this code already exists");
        }

        module.setModuleLeader(user.getFirstName() + " " + user.getLastName());
        return moduleRepository.save(module);
    }

    @Transactional
    public Module createModuleWithAssessments(ModuleWithAssessmentsDTO moduleDTO, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (moduleDTO.getName() == null || moduleDTO.getCode() == null ||
                moduleDTO.getCredits() == 0 || moduleDTO.getLevel() == 0) {
            throw new IllegalArgumentException("All required fields must be provided");
        }

        if (moduleRepository.existsByModuleCode(moduleDTO.getCode())) {
            throw new IllegalArgumentException("A module with this code already exists");
        }

        Module module = new Module();
        module.setModuleName(moduleDTO.getName());
        module.setModuleCode(moduleDTO.getCode());
        module.setCredits(moduleDTO.getCredits());
        module.setLevel(moduleDTO.getLevel());
        module.setModuleLeader(user.getFirstName() + " " + user.getLastName());

        module = moduleRepository.save(module);

        if (moduleDTO.getAssessments() != null) {
            for (AssessmentDTO assessmentDTO : moduleDTO.getAssessments()) {
                Assessment assessment = new Assessment();
                assessment.setAssessmentCategory(assessmentDTO.getAssessmentCategory());
                assessment.setTitle(assessmentDTO.getTitle());
                assessment.setAssessmentWeighting(assessmentDTO.getAssessmentWeighting());
                assessment.setPlannedIssueDate(assessmentDTO.getPlannedIssueDate());
                assessment.setCourseworkSubmissionDate(assessmentDTO.getCourseworkSubmissionDate());
                assessment.setModule(module);

                assessment = assessmentRepository.save(assessment);

                if (assessmentDTO.getParticipants() != null) {
                    for (AssessmentParticipantDTO participantDTO : assessmentDTO.getParticipants()) {
                        AssessmentParticipant participant = new AssessmentParticipant();
                        participant.setAssessment(assessment);
                        participant.setUser(userRepository.findById(participantDTO.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found")));
                        participant.setRole(participantDTO.getRole());
                        assessmentParticipantRepository.save(participant);
                    }
                }
            }
        }

        return module;
    }
//    public Module createModuleForUser(Module module, Long userId) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        module.setModuleLeader(user.getFirstName() + " " + user.getLastName());
//
//        // Ensure all required fields are set
////        if (module.getModuleName() == null || module.getModuleCode() == null ||
////                module.getCredits() == 0 || module.getLevel() == 0) {
////            throw new IllegalArgumentException("All required fields must be provided");
////        }
//
//        return moduleRepository.save(module);
//    }


}