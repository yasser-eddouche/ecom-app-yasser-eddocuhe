package org.example.customerservice;

import org.example.customerservice.config.CustomerConfigParams;
import org.example.customerservice.entities.Customer;
import org.example.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(CustomerConfigParams.class)
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder()
                            .name("yasser")
                            .email("yasser@gmail.com")
                    .build());
            customerRepository.save(Customer.builder()
                    .name("salah")
                    .email("salah@gmail.com")
                    .build());
            customerRepository.save(Customer.builder()
                    .name("Marwane")
                    .email("Marwane@gmail.com")
                    .build());

            customerRepository.findAll().forEach(customer -> {
                System.out.println("************************************");
                System.out.println(customer.getId());
                System.out.println(customer.getName());
                System.out.println(customer.getEmail());
            });
        };
    }
}
