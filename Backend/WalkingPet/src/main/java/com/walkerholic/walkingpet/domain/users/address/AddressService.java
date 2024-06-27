package com.walkerholic.walkingpet.domain.users.address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressFunction address;
    public String[] getDistrictByLocationPoint(double latitude, double longitude) {
        String x = Double.toString(latitude);
        String y = Double.toString(longitude);

        Arrays.toString(AddressFunction.getDistrictFromAddress(x,y));

        return address.getDistrictFromAddress(x,y);
    }
}
