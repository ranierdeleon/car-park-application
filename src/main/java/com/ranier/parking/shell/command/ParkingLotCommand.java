package com.ranier.parking.shell.command;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ranier.parking.dto.ParkingLotSummaryDto;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.service.ParkingLotService;
import com.ranier.parking.shell.InputReader;
import com.ranier.parking.shell.ShellHelper;
import com.ranier.parking.shell.table.ShellTableBuilder;

import org.jline.reader.UserInterruptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.util.StringUtils;

@ShellComponent
public class ParkingLotCommand {
    @Autowired
    ShellHelper shellHelper;

    @Autowired
    ParkingLotService parkingLotService;

    @Autowired
    InputReader inputReader;

    @ShellMethod("View parking lot availability summary")
    public void viewParkingLot() {
        List<ParkingLotSummaryDto> parkingLotSummaryDtos = parkingLotService.getParkingLotSummary();

        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("type", "Type");
        headers.put("available", "Parking Lot Available");
        headers.put("total", "Parking Lot Total Size");

        // Display vehicle history
        shellHelper.print(ShellTableBuilder.build(new BeanListTableModel<>(parkingLotSummaryDtos, headers)));
    }

    @ShellMethod("Add parking lot space")
    public void addParkingLot(@ShellOption(value = { "-t", "--type" }, defaultValue = "") String optionalType,
            @ShellOption(value = { "-c", "--count" }, defaultValue = "") String optionalNumberOfLot) {
        try {
            // Number of lot question
            Long numberOfLot = null;
            do {
                String number = null;
                if (StringUtils.hasText(optionalNumberOfLot)) {
                    number = optionalNumberOfLot;
                } else {
                    number = inputReader.prompt("Number of lot to create");
                }
                if (StringUtils.hasText(number)) {
                    try {
                        numberOfLot = Long.valueOf(number);
                    } catch (Exception e) {
                        shellHelper.printError("Number of lot should be a number!");
                    }
                } else {
                    shellHelper.printWarning("Number of lot cannot be empty!");
                }
            } while (numberOfLot == null);

            // Vehicle type section
            VehicleType vehicleType = null;
            if (StringUtils.hasText(optionalType)) {
                vehicleType = VehicleType.valueOf(optionalType.toUpperCase());
            }

            if (vehicleType == null) {
                Map<String, String> options = new HashMap<>();
                options.put("C", VehicleType.CAR.name());
                options.put("M", VehicleType.MOTORCYCLE.name());

                String vehicleTypeInput = inputReader.selectFromList("Vehicle type",
                        "Please enter one of the [] values", options, true, null);
                vehicleType = VehicleType.valueOf(options.get(vehicleTypeInput.toUpperCase()));
            }

            try {
                parkingLotService.addParkingLot(vehicleType, numberOfLot);
                shellHelper.printSuccess("Parking lot successfully added.");
            } catch (Exception e) {
                shellHelper.printError("Error unable to add new parking lot");
            }
        } catch (UserInterruptException e) {

        }
    }
}
