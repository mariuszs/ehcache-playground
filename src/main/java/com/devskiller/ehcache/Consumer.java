package com.devskiller.ehcache;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Consumer {

    public static final String MARIUSZ_KEY = "A";
    private final Cache fooCache;
    private AtomicLong counter = new AtomicLong();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final MetricRegistry metrics = new MetricRegistry();
    private final Timer responses = metrics.timer(MetricRegistry.name(Consumer.class, "responses"));

    @Autowired
    public Consumer(Cache fooCache) {
        this.fooCache = fooCache;
    }

    @PostConstruct
    void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
        reporter.start(10, TimeUnit.SECONDS);
    }


    @Scheduled(fixedRate = 800)
    public void eat() {
        final Timer.Context context = responses.time();
        Meter requests = metrics.meter("requests");

        try {

            String now = dateFormat.format(new Date());
            String key = MARIUSZ_KEY +  counter.get();
            System.out.println("Try to read " + key);

            Cache.ValueWrapper valueWrapper = fooCache.get(key);
            if (valueWrapper != null) {
                System.out.println("Consumed " + key);
                fooCache.evict(key);
                counter.incrementAndGet();
                eat();
            }
            requests.mark();

        } finally {
            context.stop();
        }

    }
}


