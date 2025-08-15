package com.hureru.iam.service.impl;

import com.hureru.iam.service.IAddressesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AddressesServiceImplTest {
    @Autowired
    private IAddressesService addressesService;
    @Test
    void getAddressTest(){
        System.out.println(addressesService.getAddressById(2L,12L));
    }

}
