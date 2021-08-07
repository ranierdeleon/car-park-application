package com.ranier.parking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.ranier.parking.dto.VehicleDto;
import com.ranier.parking.model.Vehicle;
import com.ranier.parking.model.VehicleType;
import com.ranier.parking.repository.VehicleRepository;
import com.ranier.parking.service.impl.VehicleServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testGetVehicleNull() {
        when(vehicleRepository.findByPlateIgnoreCase(any())).thenReturn(null);
        assertNull(vehicleService.getVehicleByPlate("SGX1234A"));
    }

    @Test
    public void testGetVehicle() {
        VehicleDto expected = VehicleDto.builder().plate("SGX1234A").type(VehicleType.CAR).build();
        when(vehicleRepository.findByPlateIgnoreCase(any()))
                .thenReturn(Vehicle.builder().plate(expected.getPlate()).type(expected.getType()).build());
        doReturn(expected).when(modelMapper).map(any(), any());
        VehicleDto actual = vehicleService.getVehicleByPlate("SGX1234A");
        assertEquals(expected.getPlate(), actual.getPlate());
        assertEquals(expected.getType(), actual.getType());
    }
}
