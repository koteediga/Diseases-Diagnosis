package com.medicure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.medicure.dto.response.OrdersResponse;
import com.medicure.entities.OrderEntity;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByUserId(int userId);

    @Query(value = """
            SELECT
                new com.medicure.dto.response.OrdersResponse(m.id, m.name, m.price, o.id, o.phone, o.address, o.quantity, o.totalPrice)
            FROM
                MedicineEntity m
            JOIN
                OrderEntity o
            ON
                m.id = o.medicineId
            WHERE
                o.userId = :userId
            """)
    List<OrdersResponse> getOrderResponse(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM OrderEntity o WHERE o.medicineId = :medicineId")
    public void deleteByMedicineId(@Param("medicineId") int medicineId);

}
