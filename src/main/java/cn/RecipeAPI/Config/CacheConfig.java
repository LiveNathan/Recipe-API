package cn.RecipeAPI.Config;


import cn.RecipeAPI.Models.Recipe;
import cn.RecipeAPI.Models.Review;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager eHcacheManagerRecipes() {
        CacheConfiguration<Long, Recipe> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Recipe.class,
                        ResourcePoolsBuilder.heap(100))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)))
                .build();

        CacheConfiguration<Long, Review> cacheConfigurationReviews = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Review.class,
                        ResourcePoolsBuilder.heap(100))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)))
                .build();

        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        javax.cache.configuration.Configuration<Long, Recipe> configurationRecipes = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);
        javax.cache.configuration.Configuration<Long, Review> configurationReviews = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfigurationReviews);
        cacheManager.createCache("recipes", configurationRecipes);
        cacheManager.createCache("reviews", configurationReviews);

        return cacheManager;
    }

//    @Bean
//    public CacheManager eHcacheManagerReviews() {
//        CacheConfiguration<Long, Review> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Review.class,
//                        ResourcePoolsBuilder.heap(100))
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)))
//                .build();
//
//        CachingProvider cachingProvider = Caching.getCachingProvider();
//        CacheManager cacheManager2 = cachingProvider.getCacheManager();
//
//        javax.cache.configuration.Configuration<Long, Review> configurationRecipes = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);
//        cacheManager2.createCache("reviews", configurationRecipes);
//
//        return cacheManager2;
//    }

//    @Bean
//    public CacheManager eHcacheManager() {
//        CacheConfiguration<Long, Recipe> cacheConfigurationRecipe = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(Long.class, Recipe.class, ResourcePoolsBuilder.heap(100))
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)))
//                .build();
//
//        CacheConfiguration<Long, Review> cacheConfigurationReview = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(Long.class, Review.class, ResourcePoolsBuilder.heap(100))
//                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)))
//                .build();
//
//        CachingProvider cachingProvider = Caching.getCachingProvider();
////        CacheManager cacheManager = cachingProvider.getCacheManager();
//
//        try (CacheManager cacheManager = (CacheManager) CacheManagerBuilder.newCacheManagerBuilder()
//                .withCache("recipes", cacheConfigurationRecipe)
//                .build()) {
//            javax.cache.configuration.Configuration<Long, Recipe> configurationRecipes = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfigurationRecipe);
//            cacheManager.createCache("recipes", configurationRecipes);
//            return cacheManager;
//        }
//    }

}
