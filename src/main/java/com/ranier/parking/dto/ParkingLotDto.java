package com.ranier.parking.dto;

import com.ranier.parking.model.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotDto {
    private String parkingLotName;
    private VehicleType type;
    private Boolean isOccupied;
}
