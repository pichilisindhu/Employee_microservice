package com.hrms.project.service;


import com.hrms.project.entity.Employee;
import com.hrms.project.entity.VoterId;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.payload.VoterDto;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.VoterIdRepository;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VoterIdServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private VoterIdRepository voterIdRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public VoterDto createVoter(String employeeId, MultipartFile voterImage, VoterDto voterDto) throws IOException {
        Employee employee=employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));
        if(voterIdRepository.findByEmployee_EmployeeId(employeeId).isPresent()){
            throw new APIException("Voter  Already Exists for employee");
        }

        if (voterIdRepository.existsByVoterIDNumber(voterDto.getVoterIDNumber())) {
            throw new APIException("Voter ID already assigned to another voter");
        }
        VoterId voterIdCardDetails1=modelMapper.map(voterDto, VoterId.class);
        voterIdCardDetails1.setEmployee(employee);

        if (voterImage != null && !voterImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, voterImage);
            voterIdCardDetails1.setUploadVoter(fileName);
        }
        VoterId voterIdCardDetails=voterIdRepository.save(voterIdCardDetails1);

        return modelMapper.map(voterIdCardDetails, VoterDto.class);
    }


    public VoterDto getVoterByEmployee(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));

        VoterId voterIdCardDetails1=voterIdRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new APIException("Voter Id Not Found"));

        VoterDto voterDto=modelMapper.map(voterIdCardDetails1, VoterDto.class);

        return voterDto;

    }


    public VoterDto updateVoter(String employeeId, MultipartFile voterImage, VoterDto voterDto) throws IOException {
        Employee employee=employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found"));
        VoterId voterIdCardDetails=voterIdRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new APIException("Voter Id Not Found"));
        if(!voterIdCardDetails.getVoterIDNumber().equals(voterDto.getVoterIDNumber())){
            throw new APIException("Voter ID number cannot be changed once submitted");
        }
        voterIdCardDetails.setFullName(voterDto.getFullName());
        voterIdCardDetails.setRelationName(voterDto.getRelationName());
        voterIdCardDetails.setGender(voterDto.getGender());
        voterIdCardDetails.setDateOfBirth(voterDto.getDateOfBirth());
        voterIdCardDetails.setAddress(voterDto.getAddress());
        voterIdCardDetails.setIssuedDate(voterDto.getIssuedDate());
        voterIdCardDetails.setUploadVoter(voterDto.getUploadVoter());

        if (voterImage != null && !voterImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, voterImage);
            voterIdCardDetails.setUploadVoter(fileName);
        }

        VoterId voterIdCardDetails1=voterIdRepository.save(voterIdCardDetails);
        VoterDto  voterDto1=modelMapper.map(voterIdCardDetails1, VoterDto.class);
        return voterDto1;
}
}
