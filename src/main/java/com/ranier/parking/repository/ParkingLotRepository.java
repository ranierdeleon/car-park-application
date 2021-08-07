package com.ranier.parking.repository;

import java.util.List;

import com.ranier.parking.model.ParkingLot;
import com.ranier.parking.model.VehicleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    ParkingLot findFirstByTypeAndIsOccupiedFalseOrderByNumberAsc(VehicleType type);

    ParkingLot findFirstByTypeOrderByNumberDesc(VehicleType type);

    ParkingLot findByTypeAndNumber(VehicleType type, Long number);

    public static interface ParkingLotAvailability {
        VehicleType getType();

        Long getAvailable();

        Long getTotal();
    }

    @Query("SELECT type as type, COUNT(*) as total, COUNT(CASE WHEN isOccupied <> true then 1 end) as available FROM ParkingLot GROUP BY type")
    List<ParkingLotAvailability> getParkingLotAvailablility();
}
