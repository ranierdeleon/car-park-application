package com.ranier.parking.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ranier.parking.dto.TicketDto;
import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.exception.InvalidInputException;
import com.ranier.parking.exception.VehicleRejectException;
import com.ranier.parking.model.ParkingLot;
import com.ranier.parking.model.Ticket;
import com.ranier.parking.model.Vehicle;
import com.ranier.parking.repository.ParkingLotRepository;
import com.ranier.parking.repository.TicketRepository;
import com.ranier.parking.repository.VehicleRepository;
import com.ranier.parking.service.ParkingLotService;
import com.ranier.parking.service.TicketService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TicketDto enter(VehicleDto vehicleDto) throws VehicleRejectException {
        return enter(vehicleDto, new Timestamp(new Date().getTime()));
    }

    @Override
    public TicketDto enter(VehicleDto vehicleDto, Timestamp entry) throws VehicleRejectException {
        TicketDto ticketDto = null;
        Vehicle vehicle = vehicleRepository.findByPlateIgnoreCase(vehicleDto.getPlate());
        if (vehicle == null) {
            vehicle = vehicleRepository.save(modelMapper.map(vehicleDto, Vehicle.class));
        }
        Ticket ticket = ticketRepository.findFirstByVehicleAndExitIsNull(vehicle);
        // If ticket exist it means that the vehicle is still parked
        if (ticket != null) {
            throw new VehicleRejectException("Vehicle is still parked");
        }
        // Get next available parking lot
        ParkingLot parkingLot = parkingLotRepository
                .findFirstByTypeAndIsOccupiedFalseOrderByNumberAsc(vehicle.getType());
        // If no parking lot is avaiable throw exception
        if (parkingLot == null) {
            throw new VehicleRejectException("No parking lot available");
        }
        parkingLot.setIsOccupied(true);
        ticket = ticketRepository.save(Ticket.builder().entry(entry).parkingLot(parkingLot).vehicle(vehicle).build());
        parkingLotRepository.save(parkingLot);
        return modelMapper.map(ticket, TicketDto.class);
    }

    @Override
    public TicketDto exit(VehicleDto vehicleDto) throws VehicleRejectException {
        return exit(vehicleDto, new Timestamp(new Date().getTime()));
    }

    @Override
    public TicketDto exit(VehicleDto vehicleDto, Timestamp exit) throws VehicleRejectException {
        TicketDto ticketDto = null;
        Vehicle vehicle = vehicleRepository.findByPlateIgnoreCase(vehicleDto.getPlate());
        if (vehicle == null) {
            throw new VehicleRejectException("Can't find vehicle");
        }
        Ticket ticket = ticketRepository.findFirstByVehicleAndExitIsNull(vehicle);
        if (ticket == null) {
            throw new VehicleRejectException("No parked vehicle");
        }
        // Compute parking fee
        BigDecimal amount;
        try {
            amount = parkingLotService.calculateFee(ticket.getParkingLot().getType(), ticket.getEntry(), exit);
        } catch (InvalidInputException e) {
            throw new VehicleRejectException("Invalid exit timestamp");
        }
        // Update ticket to reflect exit and amount
        ticket.setAmount(amount);
        ticket.setExit(exit);
        ticket = ticketRepository.save(ticket);

        // Free up parking space
        ParkingLot parkingLot = ticket.getParkingLot();
        parkingLot.setIsOccupied(false);
        parkingLotRepository.save(parkingLot);

        return modelMapper.map(ticket, TicketDto.class);
    }

    @Override
    public List<TicketDto> getVehicleHistory(VehicleDto vehicleDto) throws VehicleRejectException {
        Vehicle vehicle = vehicleRepository.findByPlateIgnoreCase(vehicleDto.getPlate());
        if (vehicle == null) {
            throw new VehicleRejectException("Can't find vehicle");
        }
        List<Ticket> tickets = ticketRepository.findByVehiclePlateIgnoreCase(vehicle.getPlate());

        return modelMapper.map(tickets, new TypeToken<List<TicketDto>>() {
        }.getType());
    }

    @Override
    public BigDecimal getRevenue() {
        return ticketRepository.calculateRevenue();
    }

}
