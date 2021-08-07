package com.ranier.parking.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import com.ranier.parking.dto.ParkingLotSummaryDto;
import com.ranier.parking.exception.InvalidInputException;
import com.ranier.parking.exception.ParkingNotFound;
import com.ranier.parking.exception.ParkingOccupiedException;
import com.ranier.parking.model.ParkingLot;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.repository.ParkingLotRepository;
import com.ranier.parking.repository.ParkingLotRepository.ParkingLotAvailability;
import com.ranier.parking.service.ParkingLotService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void addParkingLot(VehicleType type, Long count) {
        ParkingLot latestLot = parkingLotRepository.findFirstByTypeOrderByNumberDesc(type);
        List<ParkingLot> parkingLots = new ArrayList<>();
        LongStream.range(1L, count + 1L).forEach(number -> parkingLots.add(ParkingLot.builder()
                .number(latestLot == null ? number : latestLot.getNumber() + number).type(type).build()));
        parkingLotRepository.saveAll(parkingLots);
    }

    @Override
    public void removeParkingLot(VehicleType type, Long lotNumber) throws ParkingOccupiedException, ParkingNotFound {
        ParkingLot parkingLot = parkingLotRepository.findByTypeAndNumber(type, lotNumber);
        if (parkingLot == null) {
            throw new ParkingNotFound();
        }
        if (parkingLot.getIsOccupied()) {
            throw new ParkingOccupiedException();
        }
        parkingLotRepository.delete(parkingLot);
    }

    @Override
    public BigDecimal calculateFee(VehicleType type, Timestamp entry, Timestamp exit) throws InvalidInputException {
        if (entry == null || exit == null || exit.before(entry)) {
            throw new InvalidInputException("Entry/Exit time is invalid");
        }
        long diff = exit.getTime() - entry.getTime();
        double diffHours = (double) diff / (60 * 60 * 1000);
        return type.rate.multiply(BigDecimal.valueOf(diffHours).setScale(0, RoundingMode.UP));
    }

    @Override
    public List<ParkingLotSummaryDto> getParkingLotSummary() {
        List<ParkingLotAvailability> parkingLots = parkingLotRepository.getParkingLotAvailablility();
        return modelMapper.map(parkingLots, new TypeToken<List<ParkingLotSummaryDto>>() {
        }.getType());
    }

}
