package lilyes.oslobicycle.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

@Configuration
class ApplicationConfig {
    @Bean
    fun restTemplate(): RestTemplate{
        return RestTemplate()
    }
}