package com.ranier.parking.service;

import com.ranier.parking.dto.TicketDto;
import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.exception.InvalidInputException;
import com.ranier.parking.exception.VehicleRejectException;
import com.ranier.parking.model.ParkingLot;
import com.ranier.parking.model.Ticket;
import com.ranier.parking.model.Vehicle;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.repository.ParkingLotRepository;
import com.ranier.parking.repository.TicketRepository;
import com.ranier.parking.repository.VehicleRepository;
import com.ranier.parking.service.impl.TicketServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private ParkingLotService parkingLotService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Vehicle vehicle = Vehicle.builder().plate("SGX1234A").type(VehicleType.MOTORCYCLE).build();

    @Test
    public void testEntrySameVehicle() {
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(vehicle);
        when(ticketRepository.findFirstByVehicleAndExitIsNull(any()))
                .thenReturn(Ticket.builder().vehicle(vehicle).build());
        assertThrows(VehicleRejectException.class,
                () -> ticketService.enter(new VehicleDto(vehicle.getPlate(), vehicle.getType())));
    }

    @Test
    public void testEntryParkingFull() {
        VehicleDto vehicleDto = new VehicleDto(vehicle.getPlate(), vehicle.getType());
        doReturn(vehicle).when(modelMapper).map(any(), any());
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(null);
        when(vehicleRepository.save(any())).thenReturn(vehicle);
        when(parkingLotRepository.findFirstByTypeAndIsOccupiedFalseOrderByNumberAsc(any())).thenReturn(null);
        assertThrows(VehicleRejectException.class, () -> ticketService.enter(vehicleDto));
    }

    @Test
    public void testEntry() {
        ParkingLot parkingLot = ParkingLot.builder().type(VehicleType.MOTORCYCLE).number(1L).build();
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(vehicle);
        when(parkingLotRepository.findFirstByTypeAndIsOccupiedFalseOrderByNumberAsc(any())).thenReturn(parkingLot);
        when(ticketRepository.save(any())).thenReturn(Ticket.builder().entry(new Timestamp(new Date().getTime()))
                .parkingLot(parkingLot).vehicle(vehicle).build());
        assertDoesNotThrow(() -> ticketService.enter(new VehicleDto(vehicle.getPlate(), vehicle.getType())));
    }

    @Test
    public void testExitMissingVehicle() {
        VehicleDto vehicleDto = new VehicleDto(vehicle.getPlate(), vehicle.getType());
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(null);
        assertThrows(VehicleRejectException.class, () -> ticketService.exit(vehicleDto));
    }

    @Test
    public void testExitNoTicket() {
        VehicleDto vehicleDto = new VehicleDto(vehicle.getPlate(), vehicle.getType());
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(vehicle);
        when(ticketRepository.findFirstByVehicleAndExitIsNull(any())).thenReturn(null);
        assertThrows(VehicleRejectException.class, () -> ticketService.exit(vehicleDto));
    }

    private Timestamp addTimestampHour(Timestamp timestamp, int hour) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(timestamp);
        cal.add(Calendar.HOUR, hour);
        return new Timestamp(cal.getTime().getTime());
    }

    @Test
    public void testExitInvalidExit() throws InvalidInputException {
        VehicleDto vehicleDto = new VehicleDto(vehicle.getPlate(), vehicle.getType());
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(vehicle);
        Timestamp entry = new Timestamp(new Date().getTime());
        Timestamp exit = addTimestampHour(entry, -2);
        Ticket ticket = Ticket.builder().vehicle(vehicle).parkingLot(new ParkingLot()).entry(entry).build();

        when(parkingLotService.calculateFee(any(), any(), any())).thenThrow(InvalidInputException.class);
        when(ticketRepository.findFirstByVehicleAndExitIsNull(any())).thenReturn(ticket);
        assertThrows(VehicleRejectException.class, () -> ticketService.exit(vehicleDto, exit));
    }

    @Test
    public void testExit() throws InvalidInputException {
        VehicleDto vehicleDto = new VehicleDto(vehicle.getPlate(), vehicle.getType());
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(vehicle);
        Timestamp entry = new Timestamp(new Date().getTime());
        Timestamp exit = addTimestampHour(entry, 2);
        Ticket ticket = Ticket.builder().vehicle(vehicle)
                .parkingLot(ParkingLot.builder().type(vehicle.getType()).number(1L).build()).entry(entry).exit(exit)
                .amount(BigDecimal.valueOf(4)).build();

        when(ticketRepository.save(any())).thenReturn(ticket);
        when(parkingLotRepository.save(any())).thenReturn(ticket.getParkingLot());
        when(parkingLotService.calculateFee(any(), any(), any())).thenReturn(BigDecimal.valueOf(4));
        when(ticketRepository.findFirstByVehicleAndExitIsNull(any())).thenReturn(ticket);

        doReturn(new TicketDto()).when(modelMapper).map(any(), any());
        assertDoesNotThrow(() -> ticketService.exit(vehicleDto, exit));
        verify(ticketRepository, times(1)).save(ticket);
        verify(parkingLotRepository, times(1)).save(ticket.getParkingLot());
    }
}
