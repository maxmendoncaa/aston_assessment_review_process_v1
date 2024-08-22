package com.aston.assessment.service;
import com.aston.assessment.model.*;
import com.aston.assessment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IntakeService {
    @Autowired
    private IntakeRepository intakeRepository;

    public Intake createIntake(Intake intake) {
        return intakeRepository.save(intake);
    }

    public List<Intake> getAllIntakes() {
        return intakeRepository.findAll();
    }

    public Intake getIntakeById(Long id) {
        return intakeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Intake not found"));
    }


}
