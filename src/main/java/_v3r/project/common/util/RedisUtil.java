package _v3r.project.common.util;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            String value = valueOperations.get(key);

            if (value == null) {
                throw new EverException(ErrorCode.REDIS_DATA_NOT_FOUND);
            }

            return value;
        } catch (DataAccessException e) {
            throw new EverException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }

    public void setDataExpire(String key, String value, long duration) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofSeconds(duration);
            valueOperations.set(key, value, expireDuration);
        } catch (DataAccessException e) {
            log.error("Redis 데이터 저장 오류 발생. key : {}", key, e);
            throw new EverException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }

    public void deleteData(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            if (Boolean.FALSE.equals(result)) {
                log.warn("Redis key 삭제되지 않음 : {}", key);
            }
        } catch (DataAccessException e) {
            throw new EverException(ErrorCode.REDIS_SERVER_ERROR);
        }
    }
}
