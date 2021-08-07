package com.ranier.parking.service;

import com.ranier.parking.dto.VehicleDto;

import org.springframework.stereotype.Service;

@Service
public interface VehicleService {
    /**
     * Get vehicle by plate
     * 
     * @param plate
     * @return vehicle
     */
    VehicleDto getVehicleByPlate(String plate);
}
