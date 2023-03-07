package com.mulan.fengwo_backend.service;


import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CommonTest {
    public void exampleMethod(String requiredParam) {
        exampleMethod(requiredParam, Optional.empty());
    }

    public void exampleMethod(String requiredParam, Optional<String> optionalParam) {
        System.out.println("Required parameter: " + requiredParam);
        if (optionalParam.isPresent()) {
            System.out.println("Optional parameter: " + optionalParam.get());
        } else {
            System.out.println("Optional parameter not provided.");
        }
    }

    @Test
    void optionalTest(){
        exampleMethod("haha");
    }
}
