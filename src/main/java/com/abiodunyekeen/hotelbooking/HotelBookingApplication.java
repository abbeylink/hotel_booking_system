package com.abiodunyekeen.hotelbooking;

import com.abiodunyekeen.hotelbooking.service.RoleService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HotelBookingApplication {
@Autowired
    private RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingApplication.class, args);
    }

    @PostConstruct
    public void init() {
        roleService.checkAndInsertRoles();
    }

}
