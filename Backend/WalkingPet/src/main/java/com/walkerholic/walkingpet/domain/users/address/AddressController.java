package com.walkerholic.walkingpet.domain.users.address;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/address")
    public String[] getDistrict(@RequestParam(value = "latitude") double latitude, @RequestParam(value = "longitude") double longitude){
        return addressService.getDistrictByLocationPoint(latitude, longitude);
    }
}
