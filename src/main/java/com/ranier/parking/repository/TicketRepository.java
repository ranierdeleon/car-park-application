package com.ranier.parking.repository;

import java.math.BigDecimal;
import java.util.List;

import com.ranier.parking.model.Ticket;
import com.ranier.parking.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Ticket findFirstByVehicleAndExitIsNull(Vehicle vehicle);

    Ticket findFirstByVehiclePlateIgnoreCase(String plate);

    List<Ticket> findByVehiclePlateIgnoreCase(String plate);

    @Query("SELECT SUM(COALESCE(t.amount,0)) from Ticket t")
    BigDecimal calculateRevenue();
}
