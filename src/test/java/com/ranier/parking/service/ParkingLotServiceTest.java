package com.ranier.parking.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.LongStream;

import com.ranier.parking.exception.InvalidInputException;
import com.ranier.parking.exception.ParkingNotFound;
import com.ranier.parking.exception.ParkingOccupiedException;
import com.ranier.parking.model.ParkingLot;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.repository.ParkingLotRepository;
import com.ranier.parking.service.impl.ParkingLotServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ParkingLotServiceImpl parkingLotService;

    @Test
    public void testAddParkingLot() {
        Long numberOfLot = 3L;
        VehicleType type = VehicleType.CAR;
        List<ParkingLot> parkingLots = new ArrayList<>();
        LongStream.range(1L, numberOfLot + 1L)
                .forEach(number -> parkingLots.add(ParkingLot.builder().number(number).type(type).build()));
        parkingLotService.addParkingLot(type, numberOfLot);
        verify(parkingLotRepository, times(1)).saveAll(parkingLots);
    }

    @Test
    public void testRemoveParkingLotNull() {
        Long lotNumber = 3L;
        VehicleType type = VehicleType.CAR;
        when(parkingLotRepository.findByTypeAndNumber(any(), any())).thenReturn(null);
        assertThrows(ParkingNotFound.class, () -> parkingLotService.removeParkingLot(type, lotNumber));
    }

    @Test
    public void testRemoveParkingLotOccupied() {
        Long lotNumber = 3L;
        VehicleType type = VehicleType.CAR;
        ParkingLot parkingLot = ParkingLot.builder().type(type).number(lotNumber).isOccupied(true).build();
        when(parkingLotRepository.findByTypeAndNumber(any(), any())).thenReturn(parkingLot);
        assertThrows(ParkingOccupiedException.class, () -> parkingLotService.removeParkingLot(type, lotNumber));
    }

    @Test
    public void testRemoveParkingLot() {
        Long lotNumber = 3L;
        VehicleType type = VehicleType.CAR;
        ParkingLot parkingLot = ParkingLot.builder().type(type).number(lotNumber).build();
        when(parkingLotRepository.findByTypeAndNumber(any(), any())).thenReturn(parkingLot);
        assertDoesNotThrow(() -> parkingLotService.removeParkingLot(type, lotNumber));
        verify(parkingLotRepository, times(1)).delete(parkingLot);
    }

    @Test
    public void testCalculateFeeInvalid() {
        Timestamp entry = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        VehicleType type = VehicleType.CAR;
        cal.setTime(entry);
        cal.add(Calendar.HOUR, -2);
        Timestamp exit = new Timestamp(cal.getTime().getTime());
        assertThrows(InvalidInputException.class, () -> parkingLotService.calculateFee(type, entry, exit));
    }

    @Test
    public void testCalculateFee() {
        Timestamp entry = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        VehicleType type = VehicleType.CAR;
        BigDecimal hourDiff = BigDecimal.valueOf(2);
        BigDecimal expected = type.rate.multiply(hourDiff);

        cal.setTime(entry);
        cal.add(Calendar.HOUR, hourDiff.intValue());
        Timestamp exit = new Timestamp(cal.getTime().getTime());

        assertDoesNotThrow(() -> {
            assertEquals(expected, parkingLotService.calculateFee(type, entry, exit));
        });
    }

    @Test
    public void testCalculateFeeRoundUp() {
        Timestamp entry = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        VehicleType type = VehicleType.CAR;
        BigDecimal hourDiff = BigDecimal.valueOf(2);
        BigDecimal minuteDiff = BigDecimal.valueOf(2);
        BigDecimal expected = type.rate.multiply(hourDiff.add(BigDecimal.valueOf(1)));

        cal.setTime(entry);
        cal.add(Calendar.HOUR, hourDiff.intValue());
        cal.add(Calendar.MINUTE, minuteDiff.intValue());
        Timestamp exit = new Timestamp(cal.getTime().getTime());

        assertDoesNotThrow(() -> {
            assertEquals(expected, parkingLotService.calculateFee(type, entry, exit));
        });
    }
}
