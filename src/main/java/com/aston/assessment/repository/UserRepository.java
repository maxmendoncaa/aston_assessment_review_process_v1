package com.aston.assessment.repository;

import java.util.List;
import java.util.Optional;

import com.aston.assessment.model.UserRoles;
import com.aston.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

  Optional<Users> findByEmail(String email);

//  Optional<Users> findByUserId(Integer id);
//  List<User> findAllById(List<Integer> ids);

  List<Users> findAllByUserIdIn(List<Integer> ids);

  List<Users> findByRole(UserRoles role);

}
