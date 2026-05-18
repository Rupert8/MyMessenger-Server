package ua.rupert.messenger.api.configure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ua.rupert.messenger.store.repository")
@ComponentScan("ua.rupert.messenger.store")
@EntityScan("ua.rupert.messenger.store.entities")
public class JpaConfiguration {
}
