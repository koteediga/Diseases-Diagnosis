package com.medicure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.medicure.dto.response.DoctorAppointmentResponse;
import com.medicure.dto.response.PatientAppointResponse;
import com.medicure.entities.AppointmentEntity;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {
    
    @Query(value = """
        SELECT
            new com.medicure.dto.response.DoctorAppointmentResponse(a.id, a.date, a.slot, a.reason, u.id, u.name, u.email)
        FROM
            AppointmentEntity a
        JOIN
            UserEntity u 
        ON
            a.userId = u.id
        WHERE
            a.doctorId = :doctorId """)
    public List<DoctorAppointmentResponse> getDoctorAppointments(@Param("doctorId") int doctorId);
    
    @Query(value = """
        SELECT
            new com.medicure.dto.response.PatientAppointResponse(a.id, a.date, a.slot, u.id, u.name, u.email, u.department)
        FROM
            AppointmentEntity a
        JOIN
            UserEntity u
        ON
            a.doctorId = u.id
        WHERE
            a.userId = :userId """)
    public List<PatientAppointResponse> getPatientAppointments(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM AppointmentEntity a WHERE a.userId = :userId")
    public void deleteByUserId(@Param("userId") int userId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM AppointmentEntity a WHERE a.doctorId = :doctorId")
    public void deleteByDoctorId(@Param("doctorId") int doctorId);
}
