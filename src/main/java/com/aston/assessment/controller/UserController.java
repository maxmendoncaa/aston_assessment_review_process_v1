package com.aston.assessment.controller;

import com.aston.assessment.model.UserRoles;
import com.aston.assessment.model.Users;
import com.aston.assessment.requests.ChangePasswordRequest;
import com.aston.assessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Users>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Users)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users user = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(user.getRole().name());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<Users>> getUsersByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(service.getUsersByIds(ids));
    }

    @GetMapping("/supervisors")
    public ResponseEntity<List<Users>> getAllSupervisors() {
        return ResponseEntity.ok(service.getAllUsersByRole(UserRoles.ACADEMIC));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return service.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<Users> getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        return ResponseEntity.ok(service.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users user) {
        if (!id.equals(user.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }


}