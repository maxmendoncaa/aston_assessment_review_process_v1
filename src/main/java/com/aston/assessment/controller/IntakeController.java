package com.aston.assessment.controller;
import com.aston.assessment.model.*;
import com.aston.assessment.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/intakes")
public class IntakeController {
    @Autowired
    private IntakeService intakeService;

    @PostMapping
    public ResponseEntity<Intake> createIntake(@RequestBody Intake intake) {
        return ResponseEntity.ok(intakeService.createIntake(intake));
    }

    @GetMapping
    public ResponseEntity<List<Intake>> getAllIntakes() {
        return ResponseEntity.ok(intakeService.getAllIntakes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Intake> getIntakeById(@PathVariable Long id) {
        return ResponseEntity.ok(intakeService.getIntakeById(id));
    }


}
