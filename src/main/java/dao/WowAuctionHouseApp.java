package dao;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("dao.repository")
@EntityScan("dao/entity")
public class WowAuctionHouseApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WowAuctionHouseApp.class, args);
        UpdateDB updateDB = new UpdateDB();
    }

    @Override
    public void run(String... args) {
        System.out.println("Spring Boot Started...");
        // You can autowire repositories here or use @Component classes
    }
}
