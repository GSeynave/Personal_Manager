package gse.home.personalmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories(basePackages = {
        "gse.home.personalmanager.todo.infrastructure.repository",
        "gse.home.personalmanager.accounting.infrastructure.repository"
})
@EntityScan(basePackages = {
        "gse.home.personalmanager.todo.domain.model",
        "gse.home.personalmanager.accounting.domain.model"
})
@ComponentScan(basePackages = "gse.home.personalmanager")
public class PersonalmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalmanagerApplication.class, args);
    }

}
