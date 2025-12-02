package gse.home.personalmanager.shared.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients(basePackages = "gse.home.personalmanager")
@EnableJpaRepositories(basePackages = {
        "gse.home.personalmanager.todo.infrastructure.repository",
        "gse.home.personalmanager.accounting.infrastructure.repository",
        "gse.home.personalmanager.user.infrastructure.repository",
        "gse.home.personalmanager.habit.infrastructure.repository"
})
@EntityScan(basePackages = {
        "gse.home.personalmanager.todo.domain.model",
        "gse.home.personalmanager.accounting.domain.model",
        "gse.home.personalmanager.user.domain.model",
        "gse.home.personalmanager.habit.domain.model"
})
@ComponentScan(basePackages = "gse.home.personalmanager")
@EnableJpaAuditing
public class AppConfig {
}
