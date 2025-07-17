package com.hrms.project.service;

import com.hrms.project.entity.DrivingLicense;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PanDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.payload.DrivingLicenseDTO;
import com.hrms.project.payload.PanDTO;
import com.hrms.project.repository.DrivingLicenseRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DrivingLicenseServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DrivingLicenseRepository drivingLicenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public DrivingLicenseDTO createDrivingLicense(String employeeId, MultipartFile licenseImage,
                                                 DrivingLicense drivingLicense) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (drivingLicenseRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a License assigned");
        }
        Optional<DrivingLicense> existingLicense = drivingLicenseRepository.findById(drivingLicense.getLicenseNumber());

        if (existingLicense.isPresent()) {

           DrivingLicense license = existingLicense.get();
            if (license.getEmployee() == null) {
                license.setEmployee(employee);
                return modelMapper.map(drivingLicenseRepository.save(license), DrivingLicenseDTO.class);
            } else {
                throw new APIException("This License is already assigned to another employee");
            }
        }

        drivingLicense.setEmployee(employee);
        if (licenseImage != null && !licenseImage.isEmpty()) {
            String image = fileService.uploadImage(path, licenseImage);
            drivingLicense.setLicenseImage(image);
        }
        return modelMapper.map(drivingLicenseRepository.save(drivingLicense), DrivingLicenseDTO.class);
    }

    public DrivingLicenseDTO getDrivingDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
       DrivingLicense details=employee.getDrivingLicense();

        if(details!=null)
        {
           DrivingLicenseDTO drivingLicenseDTO=modelMapper.map(details,DrivingLicenseDTO.class);
            drivingLicenseDTO.setEmployeeId(employeeId);
            return drivingLicenseDTO;
        }
        else
        {
            throw new APIException("This employee does not have a Driving License assigned");
        }
    }


    public DrivingLicenseDTO updatedrivingDetails(String employeeId,MultipartFile licenseImage, DrivingLicenseDTO drivingLicenseDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        DrivingLicense existing=drivingLicenseRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new APIException("Driving License not found for employee: " + employeeId));



        if (!existing.getLicenseNumber().equals(drivingLicenseDTO.getLicenseNumber())) {
            throw new APIException("Driving  number cannot be changed once submitted");
        }
       existing.setName(drivingLicenseDTO.getName());
        existing.setDateOfBirth(drivingLicenseDTO.getDateOfBirth());
        existing.setBloodGroup(drivingLicenseDTO.getBloodGroup());
        existing.setFatherName(drivingLicenseDTO.getFatherName());
        existing.setIssueDate(drivingLicenseDTO.getIssueDate());
        existing.setExpiresOn(drivingLicenseDTO.getExpiresOn());
        existing.setAddress(drivingLicenseDTO.getAddress());
        existing.setLicenseImage(drivingLicenseDTO.getLicenseImage());

        DrivingLicense details=drivingLicenseRepository.save(existing);

        return modelMapper.map(details, DrivingLicenseDTO.class);
    }




}

