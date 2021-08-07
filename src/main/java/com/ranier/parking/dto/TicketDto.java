package com.ranier.parking.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Timestamp entry;
    private Timestamp exit;
    private String parkingLotName;
    private String vehiclePlate;
    private BigDecimal amount;
}
