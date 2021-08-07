package com.ranier.parking.shell.command;

import java.math.BigDecimal;
import java.text.NumberFormat;

import com.ranier.parking.service.TicketService;
import com.ranier.parking.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class RevenueCommand {
    @Autowired
    ShellHelper shellHelper;

    @Autowired
    TicketService ticketService;

    @ShellMethod("Calculates entire revenue of the car park")
    public void revenue() {
        BigDecimal totalRevenue = ticketService.getRevenue();
        String formattedTotalRevenue = NumberFormat.getCurrencyInstance()
                .format(totalRevenue != null ? totalRevenue : 0);
        shellHelper.print(String.format("Total Revenue: %s", shellHelper.getSuccessMessage(formattedTotalRevenue)));
    }
}
