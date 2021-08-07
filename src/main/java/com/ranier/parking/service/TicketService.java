package com.ranier.parking.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.ranier.parking.dto.TicketDto;
import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.exception.VehicleRejectException;

import org.springframework.stereotype.Service;

@Service
public interface TicketService {
    /**
     * Vehicle to enter the parking lot
     * 
     * @param vehicleDto vehicle data
     * @return ticket that contains parking details
     * @throws VehicleRejectException if the vehicle is not allowed to enter parking
     */
    TicketDto enter(VehicleDto vehicleDto) throws VehicleRejectException;

    /**
     * Vehicle to enter the parking lot
     * 
     * @param vehicleDto vehicle data
     * @param entry      time that vehicle enters the parking
     * @return ticket that contains parking details
     * @throws VehicleRejectException if the vehicle is not allowed to enter parking
     */
    TicketDto enter(VehicleDto vehicleDto, Timestamp entry) throws VehicleRejectException;

    /**
     * Vehicle to exit the parking lot, calculates the amount to be paid
     * 
     * @param vehicleDto vehicle data
     * @return ticket that contains parking details
     */
    TicketDto exit(VehicleDto vehicleDto) throws VehicleRejectException;

    /**
     * Vehicle to exit the parking lot, calculates the amount to be paid
     * 
     * @param vehicleDto vehicle data
     * @param exit       time that vehicle exits the parking
     * @return ticket that contains parking details
     */
    TicketDto exit(VehicleDto vehicleDto, Timestamp exit) throws VehicleRejectException;

    /**
     * Get the vehicle parking history
     * 
     * @param vehicleDto
     * @return list of vehicle ticket
     */
    List<TicketDto> getVehicleHistory(VehicleDto vehicleDto) throws VehicleRejectException;

    /**
     * Calculates the total parking revenue
     * 
     * @return total revenue
     */
    BigDecimal getRevenue();
}
