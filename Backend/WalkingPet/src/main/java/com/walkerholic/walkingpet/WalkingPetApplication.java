package com.walkerholic.walkingpet;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@EnableBatchProcessing
public class WalkingPetApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalkingPetApplication.class, args);
    }

}
