package com.hrms.project.service;

import com.hrms.project.entity.AadhaarCardDetails;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PassportDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.payload.AadhaarDTO;
import com.hrms.project.payload.PassportDetailsDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.PassportDetailsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PassportDetailsServiceImpl {

    @Autowired
    private PassportDetailsRepository passportDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public PassportDetails createPassport(String employeeId, MultipartFile passportImage, PassportDetails passportDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("employee not found with id: " + employeeId));

        if (passportDetailsRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has passport assigned");
        }

            PassportDetails cardDetails;

        if (passportDetailsRepository.findById(passportDetails.getPassportNumber()).isEmpty()) {

            if (passportImage != null && !passportImage.isEmpty()) {
                String fileName = fileService.uploadImage(path, passportImage);
                passportDetails.setPassportImage(fileName);
            }

            passportDetails.setEmployee(employee);
            cardDetails = passportDetailsRepository.save(passportDetails);

        } else {
          PassportDetails  details = passportDetailsRepository.findById(passportDetails.getPassportNumber()).get();

            if (details.getEmployee() == null) {
                details.setEmployee(employee);
                cardDetails = passportDetailsRepository.save(details);
            } else {
                throw new APIException("Current passport is already assigned to another employee");
            }
        }

        return cardDetails;
    }




    public PassportDetailsDTO getPassportDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        PassportDetails details=employee.getPassportDetails();

        if(details==null)
        {
            throw new APIException("This employee does not have a PAN assigned");
        }
        return modelMapper.map(details,PassportDetailsDTO.class);

    }


    public PassportDetails updatePasswordDetails(String employeeId, MultipartFile passportImage, PassportDetails passportDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PassportDetails existing=employee.getPassportDetails();
        if(existing==null) {
            throw new APIException("Passport Details not found for employee with id: " + employeeId);

        }

        if (!existing.getPassportNumber().equals(passportDetails.getPassportNumber())) {
            throw new APIException("Passport  number cannot be changed once submitted");
        }

        if (passportImage != null && !passportImage.isEmpty()) {
            String fileName = fileService.uploadImage(path, passportImage);
            passportDetails.setPassportImage(fileName);
        }
            existing.setCountryCode(passportDetails.getCountryCode());
            existing.setPassportType(passportDetails.getPassportType());
            existing.setDateOfBirth(passportDetails.getDateOfBirth());
            existing.setName(passportDetails.getName());
            existing.setGender(passportDetails.getGender());
            existing.setDateOfIssue(passportDetails.getDateOfIssue());
            existing.setPlaceOfIssue(passportDetails.getPlaceOfIssue());
            existing.setPlaceOfBirth(passportDetails.getPlaceOfBirth());
            existing.setDateOfExpiration(passportDetails.getDateOfExpiration());

         return passportDetailsRepository.save(existing);



        }


}
