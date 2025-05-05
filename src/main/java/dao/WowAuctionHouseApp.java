package dao;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"dao"})
@EnableJpaRepositories("dao.repository")
@EntityScan("dao/entity")
public class WowAuctionHouseApp implements CommandLineRunner {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WowAuctionHouseApp.class, args);
        // Get the UpdateDB bean from context so Spring can inject RealmService
        context.getBean(UpdateDB.class);
    }

    @Override
    public void run(String... args) {
        System.out.println("Spring Boot Started...");
        // You can autowire repositories here or use @Component classes
    }
}
