package com.ranier.parking.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.ranier.parking.dto.ParkingLotSummaryDto;
import com.ranier.parking.exception.InvalidInputException;
import com.ranier.parking.exception.ParkingNotFound;
import com.ranier.parking.exception.ParkingOccupiedException;
import com.ranier.parking.model.VehicleType;

import org.springframework.stereotype.Service;

@Service
public interface ParkingLotService {
    /**
     * This method creates parking lot based on the input, get the latest lot and
     * append the number
     * 
     * @param type  parking lot vehicle type
     * @param count count of parking lot to create
     */
    void addParkingLot(VehicleType type, Long count);

    /**
     * This method removes a parking lot
     * 
     * @param type      parking lot vehicle type
     * @param lotNumber lot number to remove
     * @throws ParkingOccupiedException, ParkingNotFound
     */
    void removeParkingLot(VehicleType type, Long lotNumber) throws ParkingOccupiedException, ParkingNotFound;

    /**
     * Calculate the parking fee based on vehicle type and entry/exit diffence
     * rounded up to the nearest hour
     * 
     * @param type
     * @param entry
     * @param exit
     * @return fee to be paid
     * @throws InvalidInputException if the entry and exit timestamp is incorrect
     */
    BigDecimal calculateFee(VehicleType type, Timestamp entry, Timestamp exit) throws InvalidInputException;

    /**
     * This returns the total and available parking lot by type
     * 
     * @return
     */
    List<ParkingLotSummaryDto> getParkingLotSummary();
}
