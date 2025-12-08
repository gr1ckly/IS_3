package org.example.lab1.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.ServerStatistics;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CacheMetricsAspect {
    private final boolean cacheMetricsLogEnabled;

    private final RemoteCacheManager remoteCacheManager;

    @Autowired
    public CacheMetricsAspect (
            @Value("#{servletContext.getInitParameter('cacheMetricsLogEnabled') ?: 'false'}") boolean cacheMetricsLogEnabled,
            ObjectProvider<RemoteCacheManager> remoteCacheManagerProvider
    ){
        this.cacheMetricsLogEnabled = cacheMetricsLogEnabled;
        this.remoteCacheManager = remoteCacheManagerProvider.getIfAvailable();
    }

    @Pointcut("@annotation(LogCacheMetrics)")
    public void annotatedMethod(){}

    @After("annotatedMethod()")
    public void logCacheMetrics(JoinPoint jp) {
        /*
        if (this.cacheMetricsLogEnabled) {
            if (this.remoteCacheManager != null) {
                for (String cacheName : this.remoteCacheManager.getCacheNames()) {
                    ServerStatistics cacheStats = this.remoteCacheManager.getCache(cacheName).serverStatistics();
                    if (cacheStats != null) {
                        log.info("Remote cache: {} hits: {}, misses: {} after {}",
                                cacheName,
                                cacheStats.getStatistic(ServerStatistics.HITS),
                                cacheStats.getStatistic(ServerStatistics.MISSES),
                                jp.getSignature());
                    }
                }
            }
        }*/
    }
}
