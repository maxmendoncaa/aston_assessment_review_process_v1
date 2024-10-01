package com.aston.assessment.DTO;

import com.aston.assessment.model.AssessmentRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentParticipantDTO {
    private Long userId;
    private AssessmentRoles role;
}