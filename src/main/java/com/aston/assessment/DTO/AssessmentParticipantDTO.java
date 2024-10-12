package com.aston.assessment.DTO;

import com.aston.assessment.model.AssessmentRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class AssessmentParticipantDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private List<AssessmentRoles> roles;

    public AssessmentParticipantDTO(Long userId, String firstName, String lastName, List<AssessmentRoles> roles) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }


}