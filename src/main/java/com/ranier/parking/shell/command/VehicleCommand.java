package com.ranier.parking.shell.command;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ranier.parking.dto.TicketDto;
import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.exception.VehicleRejectException;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.service.TicketService;
import com.ranier.parking.service.VehicleService;
import com.ranier.parking.shell.InputReader;
import com.ranier.parking.shell.ShellHelper;
import com.ranier.parking.shell.table.ShellTableBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.util.StringUtils;

import org.jline.reader.UserInterruptException;

@ShellComponent
public class VehicleCommand {
    @Autowired
    ShellHelper shellHelper;

    @Autowired
    TicketService ticketService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    InputReader inputReader;

    @ShellMethod("Create a ticket for the supplied vehicle number")
    public void enter(@ShellOption(value = { "-p", "--plate" }, defaultValue = "") String optionalPlate,
            @ShellOption(value = { "-t", "--type" }, defaultValue = "") String optionalType,
            @ShellOption(value = { "-e", "--entry" }, defaultValue = "") String optionalEntry) {
        try {
            VehicleDto vehicleDto = null;
            // Plate section
            do {
                String plate = null;
                // check if user already added plate as parameter
                if (StringUtils.hasText(optionalPlate)) {
                    plate = optionalPlate;
                } else {
                    plate = inputReader.prompt("Plate number");
                }
                if (StringUtils.hasText(plate)) {
                    vehicleDto = vehicleService.getVehicleByPlate(plate);
                    if (vehicleDto == null) {
                        vehicleDto = VehicleDto.builder().plate(plate).build();
                    }
                } else {
                    shellHelper.printWarning("Plate number cannot be empty!");
                }
            } while (vehicleDto == null || vehicleDto.getPlate() == null);

            // Vehicle type section
            // If vehicle is already saved before no need to get type
            if (vehicleDto.getType() == null) {
                // check if user already added type as parameter
                if (StringUtils.hasText(optionalType)) {
                    vehicleDto.setType(VehicleType.valueOf(optionalType.toUpperCase()));
                }
                // skip user input if type already exists
                if (vehicleDto.getType() == null) {
                    Map<String, String> options = new HashMap<>();
                    options.put("C", VehicleType.CAR.name());
                    options.put("M", VehicleType.MOTORCYCLE.name());

                    String vehicleTypeInput = inputReader.selectFromList("Vehicle type",
                            "Please enter one of the [] values", options, true, null);
                    VehicleType vehicleType = VehicleType.valueOf(options.get(vehicleTypeInput.toUpperCase()));
                    vehicleDto.setType(vehicleType);
                }
            }

            // Entry timestamp section
            Timestamp entry = null;
            String inputEntry = null;

            if (StringUtils.hasText(optionalEntry)) {
                inputEntry = optionalEntry;
            } else {
                inputEntry = inputReader.prompt("Entry time in unix timestamp (Optional)");
            }
            if (StringUtils.hasText(inputEntry)) {
                try {
                    entry = new Timestamp(Long.parseLong(inputEntry) * 1000);
                } catch (Exception e) {
                    shellHelper.printWarning("Invalid time format, will use current timestamp");
                }
            }

            try {
                TicketDto ticketDto = null;
                if (entry == null) {
                    ticketDto = ticketService.enter(vehicleDto);
                } else {
                    ticketDto = ticketService.enter(vehicleDto, entry);
                }
                shellHelper.printSuccess(String.format("Accept %s", ticketDto.getParkingLotName()));
            } catch (VehicleRejectException e) {
                shellHelper.printError(String.format("Reject: %s", e.getMessage()));
            }
        } catch (UserInterruptException e) {

        }
    }

    @ShellMethod("Exit vehicle to parking")
    public void exitParking(@ShellOption(value = { "-p", "--plate" }, defaultValue = "") String optionalPlate,
            @ShellOption(value = { "-e", "--exit" }, defaultValue = "") String optionalExit) {
        try {
            VehicleDto vehicleDto = null;
            // Plate section
            do {
                String plate = null;
                if (StringUtils.hasText(optionalPlate)) {
                    plate = optionalPlate;
                } else {
                    plate = inputReader.prompt("Plate number");
                }
                if (StringUtils.hasText(plate)) {
                    vehicleDto = VehicleDto.builder().plate(plate).build();
                } else {
                    shellHelper.printWarning("Plate number cannot be empty!");
                }
            } while (vehicleDto.getPlate() == null);

            // Exit timestamp section
            Timestamp exit = null;
            String inputExit = null;

            if (StringUtils.hasText(optionalExit)) {
                inputExit = optionalExit;
            } else {
                inputExit = inputReader.prompt("Entry time in unix timestamp (Optional)");
            }
            if (StringUtils.hasText(inputExit)) {
                try {
                    exit = new Timestamp(Long.parseLong(inputExit) * 1000);
                } catch (Exception e) {
                    shellHelper.printWarning("Invalid time format, will use current timestamp");
                }
            }

            try {
                TicketDto ticketDto = null;
                if (exit == null) {
                    ticketDto = ticketService.exit(vehicleDto);
                } else {
                    ticketDto = ticketService.exit(vehicleDto, exit);
                }
                shellHelper.printSuccess(String.format("%s %s", ticketDto.getParkingLotName(), ticketDto.getAmount()));
            } catch (VehicleRejectException e) {
                shellHelper.printError(e.getMessage());
            }
        } catch (

        UserInterruptException e) {

        }
    }

    @ShellMethod("List down vehicle ticket history")
    public void vehicleHistory(@ShellOption(value = { "-p", "--plate" }, defaultValue = "") String optionalPlate) {
        try {
            VehicleDto vehicleDto = null;
            // Plate section
            do {
                String plate = null;
                // check if user already added plate as parameter
                if (StringUtils.hasText(optionalPlate)) {
                    plate = optionalPlate;
                } else {
                    plate = inputReader.prompt("Plate number");
                }
                if (StringUtils.hasText(plate)) {
                    vehicleDto = vehicleService.getVehicleByPlate(plate);
                    if (vehicleDto == null) {
                        vehicleDto = VehicleDto.builder().plate(plate).build();
                    }
                } else {
                    shellHelper.printWarning("Plate number cannot be empty!");
                }
            } while (vehicleDto == null || vehicleDto.getPlate() == null);

            List<TicketDto> ticketDtos = ticketService.getVehicleHistory(vehicleDto);

            LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
            headers.put("entry", "Entry");
            headers.put("exit", "Exit");
            headers.put("entry", "Entry");
            headers.put("parkingLotName", "Parking Lot Name");
            headers.put("amount", "Parking Fee");

            // Display vehicle history
            shellHelper.print(ShellTableBuilder.build(new BeanListTableModel<>(ticketDtos, headers)));

        } catch (VehicleRejectException e) {
            shellHelper.printError("Can't find vehicle");
        } catch (UserInterruptException e) {

        }
    }
}
