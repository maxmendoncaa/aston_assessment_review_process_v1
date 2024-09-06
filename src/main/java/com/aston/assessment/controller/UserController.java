package com.aston.assessment.controller;

import com.aston.assessment.model.UserRoles;
import com.aston.assessment.model.Users;
import com.aston.assessment.requests.ChangePasswordRequest;
import com.aston.assessment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

   @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok(service.findAll());
   }

   @GetMapping("/by-id")
   @PreAuthorize("#id.contains(#user.userId)")
    public ResponseEntity<List<Users>> getAllUsersById(@RequestParam List<Integer> id, @AuthenticationPrincipal Users user){
        return ResponseEntity.ok(service.getUsersByIds(id));
   }

   @GetMapping("/supervisor")
    public ResponseEntity<List<Users>> getSupervisors(@RequestParam UserRoles role) {
        return  ResponseEntity.ok(service.getAllSupervisors(role));
   }
}
