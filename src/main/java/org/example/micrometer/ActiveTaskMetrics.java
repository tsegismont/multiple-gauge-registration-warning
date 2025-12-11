package org.example.micrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import org.example.MetricsSPI;
import org.example.micrometer.meters.MultiGaugeUpdater;

public class ActiveTaskMetrics implements MetricsSPI {

  private final MultiGaugeUpdater multiGaugeUpdater;

  public ActiveTaskMetrics(MeterRegistry registry) {
    this(registry, Tags.empty());
  }

  public ActiveTaskMetrics(MeterRegistry registry, Tags commonTags) {
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
  }

  @Override
  public void messageProcessed(String address) {
    multiGaugeUpdater.decrement(Tags.of("address", address));
  }
}
