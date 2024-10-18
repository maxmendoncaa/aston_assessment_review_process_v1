package com.aston.assessment.service;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.AssessmentParticipantDTO;
import com.aston.assessment.DTO.ModuleWithAssessmentsDTO;
import com.aston.assessment.model.*;
import com.aston.assessment.model.Module;
import com.aston.assessment.repository.AssessmentParticipantRepository;
import com.aston.assessment.repository.ModuleRepository;
import com.aston.assessment.repository.AssessmentRepository;
import com.aston.assessment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

//    @Transactional
//    public void deleteModule(Long id) {
//        moduleRepository.deleteById(id);
//    }

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
//

//    @Transactional
//    public Module createModuleWithAssessments(ModuleWithAssessmentsDTO moduleDTO, Long userId) {
//        Users currentUser = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (moduleDTO.getModuleName() == null || moduleDTO.getModuleCode() == null ||
//                moduleDTO.getCredits() == 0 || moduleDTO.getLevel() == 0 || moduleDTO.getModuleLeader() == null) {
//            throw new IllegalArgumentException("All required fields must be provided");
//        }
//
//        if (moduleRepository.existsByModuleCode(moduleDTO.getModuleCode())) {
//            throw new IllegalArgumentException("A module with this code already exists");
//        }
//
//        Module module = new Module();
//        module.setModuleName(moduleDTO.getModuleName());
//        module.setModuleCode(moduleDTO.getModuleCode());
//        module.setCredits(moduleDTO.getCredits());
//        module.setLevel(moduleDTO.getLevel());
//        module.setModuleOutcomes(moduleDTO.getModuleOutcomes());
//        module.setModuleLeader(moduleDTO.getModuleLeader()); // Set the module leader as a string
//
//        module = moduleRepository.save(module);
//
//        if (moduleDTO.getAssessments() != null) {
//            for (AssessmentDTO assessmentDTO : moduleDTO.getAssessments()) {
//                Assessment assessment = new Assessment();
//                assessment.setAssessmentCategory(assessmentDTO.getAssessmentCategory());
//                assessment.setTitle(assessmentDTO.getTitle());
//                assessment.setAssessmentWeighting(assessmentDTO.getAssessmentWeighting());
//                assessment.setPlannedIssueDate(assessmentDTO.getPlannedIssueDate());
//                assessment.setCourseworkSubmissionDate(assessmentDTO.getCourseworkSubmissionDate());
//                assessment.setModule(module);
//
//                assessment = assessmentRepository.save(assessment);
//
//                if (assessmentDTO.getParticipants() != null) {
//                    for (AssessmentParticipantDTO participantDTO : assessmentDTO.getParticipants()) {
//                        Users participantUser = userRepository.findById(participantDTO.getUserId())
//                                .orElseThrow(() -> new RuntimeException("User not found"));
//
//                        AssessmentParticipant participant = new AssessmentParticipant();
//                        participant.setAssessment(assessment);
//                        participant.setUser(participantUser);
//                        participant.setRoles(new HashSet<>(participantDTO.getRoles()));
//
//                        assessmentParticipantRepository.save(participant);
//                    }
//                }
//            }
//        }
//
//        return module;
//    }

    @Transactional
    public Module createModuleWithAssessments(ModuleWithAssessmentsDTO moduleDTO, Long userId) {
        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (moduleDTO.getModuleName() == null || moduleDTO.getModuleCode() == null ||
                moduleDTO.getCredits() == 0 || moduleDTO.getLevel() == 0 || moduleDTO.getModuleLeader() == null) {
            throw new IllegalArgumentException("All required fields must be provided");
        }

        if (moduleRepository.existsByModuleCode(moduleDTO.getModuleCode())) {
            throw new IllegalArgumentException("A module with this code already exists");
        }

        Module module = new Module();
        module.setModuleName(moduleDTO.getModuleName());
        module.setModuleCode(moduleDTO.getModuleCode());
        module.setCredits(moduleDTO.getCredits());
        module.setLevel(moduleDTO.getLevel());
        module.setModuleOutcomes(moduleDTO.getModuleOutcomes());
        module.setModuleLeader(moduleDTO.getModuleLeader()); // Set the module leader as a string

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
                assessment.setSkills(assessmentDTO.getSkills());

                assessment = assessmentRepository.save(assessment);

                if (assessmentDTO.getParticipants() != null) {
                    for (AssessmentParticipantDTO participantDTO : assessmentDTO.getParticipants()) {
                        Users participantUser = userRepository.findById(participantDTO.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        AssessmentParticipant participant = new AssessmentParticipant();
                        participant.setAssessment(assessment);
                        participant.setUser(participantUser);
                        participant.setRoles(new HashSet<>(participantDTO.getRoles()));

                        assessmentParticipantRepository.save(participant);
                    }
                }
            }
        }

        return module;
    }
//    @Transactional
//    public Module createModuleWithAssessments(ModuleWithAssessmentsDTO moduleDTO, Long userId) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (moduleDTO.getModuleName() == null || moduleDTO.getModuleCode() == null ||
//                moduleDTO.getCredits() == 0 || moduleDTO.getLevel() == 0) {
//            throw new IllegalArgumentException("All required fields must be provided");
//        }
//
//        if (moduleRepository.existsByModuleCode(moduleDTO.getModuleCode())) {
//            throw new IllegalArgumentException("A module with this code already exists");
//        }
//
//        Module module = new Module();
//        module.setModuleName(moduleDTO.getModuleName());
//        module.setModuleCode(moduleDTO.getModuleCode());
//        module.setCredits(moduleDTO.getCredits());
//        module.setLevel(moduleDTO.getLevel());
//        module.setModuleOutcomes(moduleDTO.getModuleOutcomes());
//        module.setModuleLeader(user.getFirstName() + " " + user.getLastName());
//
//        module = moduleRepository.save(module);
//
//        if (moduleDTO.getAssessments() != null) {
//            for (AssessmentDTO assessmentDTO : moduleDTO.getAssessments()) {
//                Assessment assessment = new Assessment();
//                assessment.setAssessmentCategory(assessmentDTO.getAssessmentCategory());
//                assessment.setTitle(assessmentDTO.getTitle());
//                assessment.setAssessmentWeighting(assessmentDTO.getAssessmentWeighting());
//                assessment.setPlannedIssueDate(assessmentDTO.getPlannedIssueDate());
//                assessment.setCourseworkSubmissionDate(assessmentDTO.getCourseworkSubmissionDate());
//                assessment.setModule(module);
//
//                assessment = assessmentRepository.save(assessment);
//
//                if (assessmentDTO.getParticipants() != null) {
//                    Map<Long, AssessmentParticipant> participantMap = new HashMap<>();
//
//                    for (AssessmentParticipantDTO participantDTO : assessmentDTO.getParticipants()) {
//                        Users participantUser = userRepository.findById(participantDTO.getUserId())
//                                .orElseThrow(() -> new RuntimeException("User not found"));
//
//                        Assessment finalAssessment = assessment;
//                        AssessmentParticipant participant = participantMap.computeIfAbsent(
//                                participantUser.getUserId(),
//                                k -> {
//                                    AssessmentParticipant newParticipant = new AssessmentParticipant();
//                                    newParticipant.setAssessment(finalAssessment);
//                                    newParticipant.setUser(participantUser);
//                                    newParticipant.setRoles(new HashSet<>());
//                                    return newParticipant;
//                                }
//                        );
//
//                        participant.getRoles().addAll(participantDTO.getRoles());
//                    }
//
//                    // Save all participants
//                    assessmentParticipantRepository.saveAll(participantMap.values());
//                }
//            }
//        }
//
//        return module;
//    }


    public List<AssessmentDTO> getUserAssessmentsForModule(Long moduleId, String userEmail) {
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        List<Assessment> assessments = assessmentRepository.findByModuleAndParticipantsUserEmail(module, userEmail);

        if (assessments.isEmpty()) {
            throw new RuntimeException("No assessments found for this user in the specified module");
        }

        return assessments.stream()
                .map(assessment -> mapToAssessmentDTO(assessment, user))
                .collect(Collectors.toList());
    }

    private AssessmentDTO mapToAssessmentDTO(Assessment assessment, Users user) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setTitle(assessment.getTitle());
        dto.setAssessmentCategory(assessment.getAssessmentCategory());
        dto.setAssessmentWeighting(assessment.getAssessmentWeighting());
        dto.setPlannedIssueDate(assessment.getPlannedIssueDate());
        dto.setCourseworkSubmissionDate(assessment.getCourseworkSubmissionDate());

        dto.setParticipants(assessment.getParticipants().stream()
                .map(this::mapToAssessmentParticipantDTO)
                .collect(Collectors.toList()));

        // Find the user's roles for this assessment
        AssessmentParticipant userParticipant = assessment.getParticipants().stream()
                .filter(p -> p.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not a participant of this assessment"));

        // Convert the Set<AssessmentRoles> to Set<String>
        dto.setUserRoles(userParticipant.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));

        return dto;
    }

    //    private AssessmentParticipantDTO mapToAssessmentParticipantDTO(AssessmentParticipant participant) {
//        return new AssessmentParticipantDTO(participant.getUser().getUserId(), participant.getRoles());
//    }
    private AssessmentParticipantDTO mapToAssessmentParticipantDTO(AssessmentParticipant participant) {
        return new AssessmentParticipantDTO(
                participant.getUser().getUserId(),
                participant.getUser().getFirstName(),
                participant.getUser().getLastName(),
                new ArrayList<>(participant.getRoles()) // Convert Set to List
        );
    }

    public ModuleWithAssessmentsDTO getModuleWithAssessments(String moduleCode) {
        Module module = moduleRepository.findByModuleCode(moduleCode)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with code: " + moduleCode));

        List<Assessment> assessments = assessmentRepository.findByModule(module);

        ModuleWithAssessmentsDTO dto = new ModuleWithAssessmentsDTO();
        dto.setModuleName(module.getModuleName());
        dto.setModuleLeader(module.getModuleLeader());
        dto.setModuleCode(module.getModuleCode());
        dto.setCredits(module.getCredits());
        dto.setLevel(module.getLevel());
        dto.setModuleOutcomes(module.getModuleOutcomes());
        //dto.setSkills(module.getSkills());

        List<AssessmentDTO> assessmentDTOs = assessments.stream()
                .map(this::mapToAssessmentDTO)  // Using the new overloaded method
                .collect(Collectors.toList());
        dto.setAssessments(assessmentDTOs);

        return dto;
    }

    // New overloaded method for mapping Assessment to AssessmentDTO
    private AssessmentDTO mapToAssessmentDTO(Assessment assessment) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setTitle(assessment.getTitle());
        dto.setAssessmentCategory(assessment.getAssessmentCategory());
        dto.setAssessmentWeighting(assessment.getAssessmentWeighting());
        dto.setPlannedIssueDate(assessment.getPlannedIssueDate());
        dto.setCourseworkSubmissionDate(assessment.getCourseworkSubmissionDate());
        dto.setSkills(assessment.getSkills());

        // Map other fields as necessary
        return dto;
    }













    //manage modules

    @Transactional
    public Module updateModule(Long id, Module updatedModule) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        module.setModuleName(updatedModule.getModuleName());
        module.setModuleCode(updatedModule.getModuleCode());
        module.setCredits(updatedModule.getCredits());
        module.setLevel(updatedModule.getLevel());
        module.setModuleOutcomes(updatedModule.getModuleOutcomes());

        // Update assessments
        for (Assessment updatedAssessment : updatedModule.getAssessments()) {
            Assessment assessment = module.getAssessments().stream()
                    .filter(a -> a.getId().equals(updatedAssessment.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            assessment.setAssessmentCategory(updatedAssessment.getAssessmentCategory());
            assessment.setTitle(updatedAssessment.getTitle());
            assessment.setAssessmentWeighting(updatedAssessment.getAssessmentWeighting());
            assessment.setPlannedIssueDate(updatedAssessment.getPlannedIssueDate());
            assessment.setCourseworkSubmissionDate(updatedAssessment.getCourseworkSubmissionDate());

            // Update participants
            for (AssessmentParticipant updatedParticipant : updatedAssessment.getParticipants()) {
                AssessmentParticipant participant = assessment.getParticipants().stream()
                        .filter(p -> p.getId().equals(updatedParticipant.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Participant not found"));

                participant.setUser(updatedParticipant.getUser());
                participant.setRoles(updatedParticipant.getRoles());
            }
        }

        return moduleRepository.save(module);
    }

    @Transactional
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    @Transactional
    public void deleteAssessment(Long moduleId, Long assessmentId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        module.getAssessments().removeIf(assessment -> assessment.getId().equals(assessmentId));
        moduleRepository.save(module);
    }



    @Transactional
    public void deleteParticipant(Long moduleId, Long assessmentId, Long participantId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Assessment assessment = module.getAssessments().stream()
                .filter(a -> a.getId().equals(assessmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        assessment.getParticipants().removeIf(participant -> participant.getId().equals(participantId));
        moduleRepository.save(module);
    }

    @Transactional
    public void updateParticipantRoles(Long moduleId, Long assessmentId, Long participantId, Set<AssessmentRoles> roles) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Assessment assessment = module.getAssessments().stream()
                .filter(a -> a.getId().equals(assessmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        AssessmentParticipant participant = assessment.getParticipants().stream()
                .filter(p -> p.getUser().getUserId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setRoles(roles);
        moduleRepository.save(module);
    }

    // Add this method to check if a user is an assessment lead for a module
    public boolean isUserAssessmentLead(Long moduleId, String userEmail) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        return module.getAssessments().stream()
                .anyMatch(assessment -> assessment.getParticipants().stream()
                        .anyMatch(participant ->
                                participant.getUser().getEmail().equals(userEmail) &&
                                        participant.getRoles().contains(AssessmentRoles.MODULE_ASSESSMENT_LEAD)
                        )
                );
    }

    @Transactional
    public AssessmentParticipantDTO addParticipantToAssessment(Long moduleId, Long assessmentId, AssessmentParticipantDTO participantDTO) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Assessment assessment = module.getAssessments().stream()
                .filter(a -> a.getId().equals(assessmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        Users user = userRepository.findById(participantDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssessmentParticipant participant = new AssessmentParticipant();
        participant.setAssessment(assessment);
        participant.setUser(user);
        participant.setRoles(new HashSet<>(participantDTO.getRoles()));

        assessment.getParticipants().add(participant);
        moduleRepository.save(module);

        return mapToAssessmentParticipantDTO(participant);
    }
}