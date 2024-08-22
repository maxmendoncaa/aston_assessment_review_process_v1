package com.aston.assessment.service;
import com.aston.assessment.model.*;
import com.aston.assessment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;

    public Form createForm(Form form) {
        form.setStatus(FormStatus.DRAFT);
        return formRepository.save(form);
    }

    public Form submitForm(Long id) {
        Form form = getFormById(id);
        form.setStatus(FormStatus.SUBMITTED);
        return formRepository.save(form);
    }

    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    public Form getFormById(Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));
    }

    // Add other methods as needed
}