package com.hrms.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoterDto {
    private String voterIDNumber;
    private String fullName;
    private String relationName;
    private String gender;
    private LocalDate dateOfBirth;
    private String Address;
    private LocalDate issuedDate;
    private String uploadVoter;
}
