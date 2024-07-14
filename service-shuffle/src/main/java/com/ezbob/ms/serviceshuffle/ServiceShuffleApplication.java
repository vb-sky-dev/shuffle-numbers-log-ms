package com.ezbob.ms.serviceshuffle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories()
public class ServiceShuffleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceShuffleApplication.class, args);
    }

}
