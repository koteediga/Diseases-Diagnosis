package com.medicure.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medicure.entities.MedicineEntity;

@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, Integer> {
    Optional<MedicineEntity> findById(int id);
    public MedicineEntity findByName(String name);
}
