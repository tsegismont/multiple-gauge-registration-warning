package org.example.micrometer;

import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.example.MetricsSPI;
import org.example.micrometer.meters.MultiGaugeUpdater;

public class ActiveTaskMetrics implements MetricsSPI {

  private final SimpleMeterRegistry registry;
  private final MultiGaugeUpdater multiGaugeUpdater;

  public ActiveTaskMetrics(SimpleMeterRegistry registry) {
    this(registry, Tags.empty());
  }

  public ActiveTaskMetrics(SimpleMeterRegistry registry, Tags commonTags) {
    this.registry = registry;
    MultiGauge multiGauge = MultiGauge.builder("messagePending")
      .description("Number of active messages")
      .tags(commonTags)
      .baseUnit("messages")
      .register(registry);
    this.multiGaugeUpdater = new MultiGaugeUpdater(multiGauge);
  }

  @Override
  public void messageReceived(String address) {
    multiGaugeUpdater.increment(Tags.of("address", address));
    System.out.printf("ActiveTaskMetrics.messageReceived: %n==%n%s%n==%n", registry.getMetersAsString());
  }

  @Override
  public void messageProcessed(String address) {
    multiGaugeUpdater.decrement(Tags.of("address", address));
    System.out.printf("ActiveTaskMetrics.messageProcessed: %n==%n%s%n==%n", registry.getMetersAsString());
  }
}
