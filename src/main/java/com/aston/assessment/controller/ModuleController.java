package com.aston.assessment.controller;

import com.aston.assessment.DTO.AssessmentDTO;
import com.aston.assessment.DTO.ModuleWithAssessmentsDTO;
import com.aston.assessment.model.Module;
import com.aston.assessment.model.Assessment;
import com.aston.assessment.model.Users;
import com.aston.assessment.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

//    @PostMapping("/create")
//    public ResponseEntity<Module> createModule(@RequestBody Module module, Authentication authentication) {
//        Users user = (Users) authentication.getPrincipal();
//        Module createdModule = moduleService.createModuleForUser(module, user.getUserId());
//        return ResponseEntity.ok(createdModule);
//    }
//@PostMapping("/create")
//public ResponseEntity<?> createModule(@RequestBody Module module, Authentication authentication) {
//    try {
//        if (authentication == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        Users user = (Users) authentication.getPrincipal();
//        Module createdModule = moduleService.createModuleForUser(module, user.getUserId());
//        return ResponseEntity.ok(createdModule);
//    } catch (IllegalArgumentException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating module: " + e.getMessage());
//    }
//}
    @PostMapping("/create")
    public ResponseEntity<?> createModuleWithAssessments(@RequestBody ModuleWithAssessmentsDTO moduleDTO, Principal principal) {
        try {

            Users user = (Users)((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            Module createdModule = moduleService.createModuleWithAssessments(moduleDTO, user.getUserId());
            return ResponseEntity.ok(createdModule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating module: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return moduleService.getModuleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        return ResponseEntity.ok(moduleService.createModule(module));
    }

    @GetMapping("/my-modules")
    public ResponseEntity<List<Module>> getMyModules(Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(moduleService.getModulesForUser(user.getUserId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Module> updateModule(@PathVariable Long id, @RequestBody Module module) {
        if (!id.equals(module.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(moduleService.updateModule(module));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/assessments")
    public ResponseEntity<List<Assessment>> getModuleAssessments(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleAssessments(id));
    }

    @PostMapping("/{id}/assessments")
    public ResponseEntity<Assessment> createAssessment(@PathVariable Long id, @RequestBody Assessment assessment) {
        return ResponseEntity.ok(moduleService.createAssessment(id, assessment));
    }

    @PostMapping("/{id}/assign-role")
    public ResponseEntity<?> assignRole(@PathVariable Long id, @RequestParam Long userId, @RequestParam String role) {
        moduleService.assignRole(id, userId, role);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{moduleId}/assessments/user")
    public ResponseEntity<?> getUserAssessmentsForModule(
            @PathVariable Long moduleId,
            Authentication authentication) {
        if (moduleId == null) {
            return ResponseEntity.badRequest().body("Invalid module ID");
        }
        String userEmail = authentication.getName();
        try {
            List<AssessmentDTO> assessments = moduleService.getUserAssessmentsForModule(moduleId, userEmail);
            return ResponseEntity.ok(assessments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}