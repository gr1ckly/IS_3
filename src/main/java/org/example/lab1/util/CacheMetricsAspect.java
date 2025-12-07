package org.example.lab1.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CacheMetricsAspect {
    private final Statistics stats;

    private final boolean cacheMetricsLogEnabled;

    @Autowired
    public CacheMetricsAspect (
            SessionFactory sessionFactory,
            @Value("#{servletContext.getInitParameter('cacheMetricsLogEnabled') ?: 'false'}") boolean cacheMetricsLogEnabled
    ){
        this.stats = sessionFactory.getStatistics();
        this.cacheMetricsLogEnabled = cacheMetricsLogEnabled;
    }

    @Pointcut("@annotation(LogCacheMetrics)")
    public void annotatedMethod(){}

    @After("annotatedMethod()")
    public void logCacheMetrics(JoinPoint jp) {
        if (this.cacheMetricsLogEnabled) {
            for (String region: this.stats.getSecondLevelCacheRegionNames()) {
                CacheRegionStatistics regionStats = this.stats.getCacheRegionStatistics(region);
                if (regionStats == null) {
                    log.warn("Incorrect region name: {}", region);
                } else {
                    log.info("Region: {} cache hits: {}, cache misses: {} after {}",
                            region,
                            regionStats.getHitCount(),
                            regionStats.getMissCount(),
                            jp.getSignature());
                }
            }
        }
    }
}
