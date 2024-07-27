package com.sparta.goodbite.domain.waiting.repository;

import com.sparta.goodbite.domain.waiting.entity.Waiting;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    Waiting findByRestaurantIdAndCustomerId(Long restaurantId, Long customerId);

    ArrayList<Waiting> findALLByRestaurantId(Long restaurantId);

    @Query("SELECT MAX(w.waitingOrder) FROM Waiting w WHERE w.restaurant.id = :restaurant_id")
    Long findMaxWaitingOrderByRestaurantId(@Param("restaurant_id") Long restaurant_id);

    Page<Waiting> findByRestaurantId(Long restaurantId, Pageable pageable);

}
