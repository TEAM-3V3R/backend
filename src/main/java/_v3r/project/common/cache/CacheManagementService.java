package _v3r.project.common.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheManagementService {

    private final CacheManager cacheManager;
    private final ImpalaAuProfileRepository impalaAuProfileRepository;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Scheduled(cron = "0 50 7 * * *")
    public void deleteAllCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void saveUserCountCache() {
        impalaAuProfileRepository.getTotalAu(format.format(new Date()));
        impalaAuProfileRepository.getTotalUser(format.format(new Date()));
    }
}
