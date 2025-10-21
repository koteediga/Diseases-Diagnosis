package com.medicure.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medicure.dto.request.DepartmentRequest;
import com.medicure.dto.response.DepartmentResponse;
import com.medicure.entities.DepartmentEntity;
import com.medicure.exceptions.EntityAlreadyExistsException;
import com.medicure.repositories.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final ModelMapper mapper;

    public DepartmentEntity addDepartment(DepartmentRequest request) {
        DepartmentEntity check = departmentRepository.findByName(request.getName());
        if (check != null) 
            return null;
        
        DepartmentEntity department = mapper.map(request, DepartmentEntity.class);
        department.setId(0);

        return departmentRepository.save(department);
    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.getDepartments();
    }

    @Transactional
    public void deleteDepartment(int departmentId) throws EntityAlreadyExistsException {
        DepartmentEntity department = departmentRepository
                                        .findById(departmentId)
                                        .orElseThrow(() -> new EntityAlreadyExistsException("Department Not Found with id : " + departmentId));
                            

        departmentRepository.deleteByDepartment(department.getName());

        departmentRepository.delete(department);
    }

}
