package site.mygumi.goodbite.config.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 애플리케이션에서 스케줄링을 활성화하기 위한 설정 클래스입니다.
 * <p>
 * 이 클래스는 {@code @EnableScheduling} 어노테이션을 통해 스케줄링 기능을 활성화하여, 일정한 시간 간격으로 작업을 실행할 수 있도록 합니다.
 * </p>
 *
 * <p>사용 예시:
 * <pre>
 * {@Scheduled(fixedRate = 5000)}
 * public void scheduledTask() {
 *     // 주기적으로 실행할 작업
 * }
 * </pre>
 * </p>
 *
 * @author a-white-bit
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

}