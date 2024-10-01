//package com.aston.assessment.service;
//
//import com.aston.assessment.model.UserRoles;
//import com.aston.assessment.model.Users;
//import com.aston.assessment.requests.ChangePasswordRequest;
//import com.aston.assessment.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.security.Principal;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final PasswordEncoder passwordEncoder;
//    private final UserRepository repository;
//    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
//
//        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
//
//        // check if the current password is correct
//        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
//            throw new IllegalStateException("Wrong password");
//        }
//        // check if the two new passwords are the same
//        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
//            throw new IllegalStateException("Password are not the same");
//        }
//
//        // update the password
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//
//        // save the new password
//        repository.save(user);
//    }
//
//    public List<Users> findAll(){
//        return repository.findAll();
//    }
//
//    public List<Users> getUsersByIds(List<Integer> ids){
//        return repository.findAllByUserIdIn(ids);
//    }
//
//    public List<Users> getAllSupervisors(UserRoles role) {return repository.findByRole(role);}
//
//}
package com.aston.assessment.service;

import com.aston.assessment.model.UserRoles;
import com.aston.assessment.model.Users;
import com.aston.assessment.requests.ChangePasswordRequest;
import com.aston.assessment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    @Transactional
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (Users) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public List<Users> findAll() {
        return repository.findAll();
    }

    public List<Users> getUsersByIds(List<Long> ids) {
        return repository.findAllByUserIdIn(ids);
    }

    public List<Users> getAllUsersByRole(UserRoles role) {
        return repository.findByRole(role);
    }

    public Optional<Users> getUserById(Long id) {
        return repository.findById(id);
    }

    public Optional<Users> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Transactional
    public Users updateUser(Users user) {
        return repository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public UserRoles getUserRole(String email) {
        return repository.findByEmail(email)
                .map(Users::getRole)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}