package com.minibuskingbig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MiniBuskingBigApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniBuskingBigApplication.class, args);
    }

}
