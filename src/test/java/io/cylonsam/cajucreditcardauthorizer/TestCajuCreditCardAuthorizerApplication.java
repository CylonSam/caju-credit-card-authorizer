package io.cylonsam.cajucreditcardauthorizer;

import org.springframework.boot.SpringApplication;

public class TestCajuCreditCardAuthorizerApplication {

    public static void main(String[] args) {
        SpringApplication.from(CajuCreditCardAuthorizerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
