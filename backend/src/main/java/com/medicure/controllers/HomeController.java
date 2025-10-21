package com.medicure.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medicure.dto.response.DepartmentResponse;
import com.medicure.entities.MedicineEntity;
import com.medicure.entities.UserEntity;
import com.medicure.exceptions.EntityNotFoundException;
import com.medicure.services.DepartmentService;
import com.medicure.services.DoctorService;
import com.medicure.services.MedicineService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(
    name = "Home Controller",
    description = "Contains all publicly accessible endpoints"
)
public class HomeController {

    private final DepartmentService departmentService;
    private final MedicineService medicineService;
    private final DoctorService doctorService;

    @GetMapping("/medicines")
    public List<MedicineEntity> medicines() {
        return medicineService.getAll();
    }
    
    @GetMapping("/medicines/{medicineId}")
    public MedicineEntity medicineSingle(
        @PathVariable("medicineId") int medicineId
    ) throws EntityNotFoundException {

        MedicineEntity medicine = medicineService.getById(medicineId)
                                    .orElseThrow(() -> new EntityNotFoundException("Medicine not found with id : " + medicineId));

        return medicine;
    }
    
    @GetMapping("/doctors")
    public List<UserEntity> doctors(
        @RequestParam(name = "department", required = false) String department
    ) {
        if (department == null) 
            return doctorService.getAllDoctors();
        
        return doctorService.getDoctorsByDepartment(department);
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> departments() {
        return departmentService.getAllDepartments();
    }
}
