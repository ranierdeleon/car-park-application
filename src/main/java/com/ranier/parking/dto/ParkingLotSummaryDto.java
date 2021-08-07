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
public class ParkingLotSummaryDto {
    private Long available;
    private Long total;
    private VehicleType type;
}
