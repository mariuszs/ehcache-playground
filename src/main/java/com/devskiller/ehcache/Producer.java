package com.devskiller.ehcache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.management.CacheStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Producer {

    public static final String PATRYK_KEY = "B";
    private final Cache fooCache;
    private AtomicLong counter = new AtomicLong();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    public Producer(Cache fooCache) {
        this.fooCache = fooCache;
    }

    @Scheduled(fixedRate = 500)
    public void reportCurrentTime() {
        String now = dateFormat.format(new Date());
        String key = PATRYK_KEY + counter.getAndIncrement();
        fooCache.put(key, now);

        CacheStatistics cacheStatistics = new CacheStatistics((Ehcache) fooCache.getNativeCache());
        cacheStatistics.getObjectCount();

        System.out.println("Produced " + key + " (count=" + cacheStatistics.getObjectCount() + ")");

//        System.out.println(key + ". The time is now " + now);
    }
}


