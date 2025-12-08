package org.example.lab1.util;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class InfinispanCacheConfig {
    @Bean(destroyMethod = "stop")
    public RemoteCacheManager remoteCacheManager() {
        org.infinispan.client.hotrod.configuration.Configuration cfg = new ConfigurationBuilder()
                .addServer()
                .host("infinispan")
                .port(11222)
                .marshaller(new JavaSerializationMarshaller())
                .security()
                .authentication()
                    .enabled(true)
                    .username("admin")
                    .password("admin")
                    .realm("default")
                    .serverName("infinispan")
                    .saslMechanism("SCRAM-SHA-512")
                .build();
        return new RemoteCacheManager(cfg);
    }

    @Bean
    public CacheManager cacheManager(RemoteCacheManager remote) {
        return new SpringRemoteCacheManager(remote);
    }
}
