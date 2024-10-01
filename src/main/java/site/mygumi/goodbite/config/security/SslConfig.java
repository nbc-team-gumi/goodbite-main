package site.mygumi.goodbite.config.security;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SslConfig {

    private final Dotenv dotenv;

    @Bean
    public Ssl ssl() {
        Ssl ssl = new Ssl();
        ssl.setKeyStore(dotenv.get("SSL_KEYSTORE_PATH", "classpath:/api.goodbite.site.p12"));
        ssl.setKeyStorePassword(dotenv.get("SSL_KEY"));
        ssl.setKeyAlias("goodbite");
        ssl.setKeyStoreType("PKCS12");
        return ssl;
    }
}
