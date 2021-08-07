package com.ranier.parking.service.impl;

import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.model.Vehicle;
import com.ranier.parking.repository.VehicleRepository;
import com.ranier.parking.service.VehicleService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public VehicleDto getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlateIgnoreCase(plate);
        if (vehicle == null) {
            return null;
        }
        return modelMapper.map(vehicle, VehicleDto.class);
    }

}
