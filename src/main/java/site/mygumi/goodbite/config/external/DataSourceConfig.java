package site.mygumi.goodbite.config.external;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 데이터베이스 연결 구성을 위한 설정 클래스입니다.
 * <p>
 * 이 클래스는 {@link HikariDataSource}를 사용하여 MySQL 데이터베이스에 연결하기 위한 {@link DataSource} 빈을 생성하며, 환경 변수
 * 파일(.env)에서 데이터베이스 설정 정보를 가져옵니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * {@Autowired}
 * private DataSource dataSource;
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Dotenv dotenv;

    /**
     * 데이터베이스 연결을 위한 {@link DataSource} 빈을 생성합니다.
     * <p>
     * 환경 변수 파일(.env)에서 MySQL 데이터베이스 설정 정보를 가져와 연결을 구성합니다.
     * </p>
     *
     * @return 설정된 {@code DataSource} 객체
     */
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(
            "jdbc:mysql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/"
                + dotenv.get("DB_NAME"));
        dataSource.setUsername(dotenv.get("DB_USERNAME"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}