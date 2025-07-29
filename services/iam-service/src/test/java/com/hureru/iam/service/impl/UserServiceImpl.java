package com.hureru.iam.service.impl;

import com.hureru.iam.service.IUsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceImpl {

    @Autowired
    private IUsersService usersService;
    @Test
    void getPendingUserIdsTest() {
        System.out.println("Pending user ids: " +  usersService.getPendingUserIds());
    }
}
