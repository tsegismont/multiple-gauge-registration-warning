package org.example;

import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.example.micrometer.ActiveTaskMetrics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  public static void main(String[] args) {
    SimpleMeterRegistry registry = new SimpleMeterRegistry();
    registry.config().meterFilter(MeterFilter.ignoreTags("address"));

    // MessageBus bus = new MessageBus(new MicrometerMetrics(new LongGauges(new ConcurrentHashMap<>()), registry));
    MessageBus bus = new MessageBus(new ActiveTaskMetrics(registry));

    try (ExecutorService executor = Executors.newCachedThreadPool()) {

      CountDownLatch latch = new CountDownLatch(1);

      bus.registerConsumer("foo", s -> {
        System.out.printf("foo: %s%n", s);
        try {
          latch.await();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
      bus.registerConsumer("bar", s -> {
        System.out.printf("bar: %s%n", s);
        try {
          latch.await();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });

      executor.submit(() -> {
        bus.deliverMessage("foo", "Hello");
      });
      executor.submit(() -> {
        bus.deliverMessage("foo", "Hello");
      });
      executor.submit(() -> {
        bus.deliverMessage("foo", "Hello");
      });
      executor.submit(() -> {
        bus.deliverMessage("bar", "Hi");
      });
      executor.submit(() -> {
        bus.deliverMessage("bar", "Hi");
      });

      latch.countDown();
      Thread.sleep(2000);

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
