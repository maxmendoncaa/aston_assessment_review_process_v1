package com.aston.assessment.repository;

import com.aston.assessment.model.Module;
import com.aston.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findByModuleCode(String moduleCode);

    List<Module> findByLevel(int level);

    List<Module> findByModuleLeader(String moduleLeader);

    List<Module> findByCredits(int credits);

    List<Module> findByModuleNameContainingIgnoreCase(String moduleName);

    // Corrected method name to match the property name in Module entity
   // List<Module> findBySkills(String skills);

    //List<Module> findByNameContainingIgnoreCase(String moduleName);

    boolean existsByModuleCode(String moduleCode);

    @Query("SELECT m FROM Module m WHERE SIZE(m.assessments) >= :count")
    List<Module> findModulesWithAtLeastNAssessments(@Param("count") int count);

    @Query("SELECT DISTINCT m FROM Module m " +
            "JOIN m.assessments a " +
            "JOIN a.participants p " +
            "WHERE p.user = :user")
    List<Module> findModulesByUser(@Param("user") Users user);




}