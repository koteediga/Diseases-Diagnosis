package com.medicure.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.medicure.dto.request.AppointmentRequest;
import com.medicure.entities.AppointmentEntity;
import com.medicure.entities.UserEntity;
import com.medicure.enums.Role;
import com.medicure.exceptions.CustomAccessDeniedException;
import com.medicure.exceptions.EntityNotFoundException;
import com.medicure.repositories.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AuthService authService;
    private final ModelMapper mapper;
    
    public AppointmentEntity addAppointment(AppointmentRequest request) {
        try {
            System.out.println(request);
            
            AppointmentEntity appointment = mapper.map(request, AppointmentEntity.class);
            appointment.setId(0);
            return appointmentRepository.save(appointment);
        } 
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public List<?> getUserAppointments() {
        UserEntity currentUser = authService.getCurrentUser();

        if (currentUser.getRole().equals(Role.DOCTOR)) 
            return appointmentRepository.getDoctorAppointments(currentUser.getId());
        
        else
            return appointmentRepository.getPatientAppointments(currentUser.getId());
    }

    public void cancleAppointment(int appointmentId) throws EntityNotFoundException, CustomAccessDeniedException {
        AppointmentEntity appointment = appointmentRepository
                            .findById(appointmentId)
                            .orElseThrow(
                                () -> new EntityNotFoundException("Unable to find Appointment with id : " + appointmentId)
                            );

        UserEntity currentUser = authService.getCurrentUser();
        if (appointment.getUserId() != currentUser.getId()) {
            log.error("User " + currentUser.getName() + " trying to access anther user resource");
            throw new CustomAccessDeniedException("you are trying to access anther user resource", "/delete-appointment/");
        }

        appointmentRepository.delete(appointment);
    }

}
