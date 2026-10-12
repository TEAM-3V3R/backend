package _v3r.project.common.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConnectionConfig {

    /**
     * Spring의 기본값(DELAYED_ACQUISITION_AND_HOLD)은 open-in-view로 세션이 열려 있는 동안 커넥션을 반납하지 않는다.
     * 트랜잭션이 끝나면 커넥션을 풀에 돌려주도록 해, 트랜잭션 밖 외부 호출 중에는 커넥션을 점유하지 않는다.
     */
    @Bean
    public HibernatePropertiesCustomizer releaseConnectionAfterTransaction() {
        return props -> props.put("hibernate.connection.handling_mode",
                "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");
    }
}
