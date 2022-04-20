package com.micropos.products.repository;


import com.micropos.datatype.product.Product;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.CacheManagerConfiguration;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.util.ArrayList;
import java.util.List;

public class CacheHelper {

    private final CacheManager cacheManager;
    private final Cache<Integer, List> productCache;

    public CacheHelper(){
        cacheManager= CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        productCache=cacheManager.createCache("productCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, List.class, ResourcePoolsBuilder.heap(100)));

    }

    public Cache<Integer,List> getProductCache(){
        return cacheManager.getCache("productCache",Integer.class,List.class);
    }
}
