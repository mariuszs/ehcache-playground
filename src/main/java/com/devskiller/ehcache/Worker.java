package com.devskiller.ehcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class Worker {

    @Autowired
    private final Cache fooCache;

    @Autowired
    public Worker(@Qualifier("fooCache") Cache fooCache) {
        this.fooCache = fooCache;
    }

    @PostConstruct
    public void init(){
        fooCache.put("foo", "bar");
        System.out.println(fooCache.get("foo").get());
    }
}
