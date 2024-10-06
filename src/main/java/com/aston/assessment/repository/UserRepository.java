//package com.aston.assessment.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import com.aston.assessment.model.UserRoles;
//import com.aston.assessment.model.Users;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends JpaRepository<Users, Integer> {
//
//  Optional<Users> findByEmail(String email);
//
////  Optional<Users> findByUserId(Integer id);
////  List<User> findAllById(List<Integer> ids);
//
//  List<Users> findAllByUserIdIn(List<Integer> ids);
//
//  List<Users> findByRole(UserRoles role);
//
//}
package com.aston.assessment.repository;

import com.aston.assessment.model.UserRoles;
import com.aston.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
  List<Users> findAllByUserIdIn(List<Long> ids);
  List<Users> findByRole(UserRoles role);
  Optional<Users> findByEmail(String email);
  boolean existsByEmail(String email);

}