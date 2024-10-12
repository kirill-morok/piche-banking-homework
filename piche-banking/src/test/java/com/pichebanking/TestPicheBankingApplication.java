package com.pichebanking;

import org.springframework.boot.SpringApplication;

public class TestPicheBankingApplication {

    public static void main(String[] args) {
        SpringApplication.from(PicheBankingApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
