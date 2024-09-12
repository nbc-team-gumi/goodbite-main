package site.mygumi.goodbite.config.external;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PublicDataApiConfig {

    private final Dotenv dotenv;

    @Bean
    public String publicDataApiKey() {
        return dotenv.get("PUBLIC_DATA_KEY");
    }

    @Bean
    public String publicDataApiUrl() {
        return "https://api.odcloud.kr/api/nts-businessman/v1/status";
    }
}