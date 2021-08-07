package com.ranier.parking.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParkingLotConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
