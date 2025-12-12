package org.example.micrometer;

import io.micrometer.core.instrument.*;
import org.example.MetricsSPI;

import java.util.Collections;
import java.util.Spliterator;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.StreamSupport;

public class MeterBasedMetrics implements MetricsSPI {

  private final MeterRegistry registry;

  public MeterBasedMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void messageReceived(String address) {
    registerMeasurement("messagePending", Tags.of("address", address)).increment();
  }

  @Override
  public void messageProcessed(String address) {
    getMeasurement("messagePending", Tags.of("address", address)).decrement();
  }

  private MutableMeasurement registerMeasurement(String name, Tags tags) {
    return createOrGetMeasurement(name, tags, new MutableMeasurement());
  }

  private MutableMeasurement getMeasurement(String name, Tags tags) {
    return createOrGetMeasurement(name, tags, null);
  }

  private MutableMeasurement createOrGetMeasurement(String name, Tags tags, Measurement measurement) {
    Iterable<Measurement> measurements = measurement != null ? Collections.singleton(measurement) : Collections.emptyList();
    Spliterator<Measurement> registeredMeasurements = Meter.builder(name, Meter.Type.GAUGE, measurements)
      .tags(tags)
      .register(registry)
      .measure()
      .spliterator();

    return StreamSupport.stream(registeredMeasurements, false)
      .findFirst()
      .map(m -> (MutableMeasurement) m)
      .orElseThrow(() -> new IllegalStateException("No way!"));
  }

  private static class MutableMeasurement extends Measurement {

    private final LongAdder longAdder;

    private MutableMeasurement() {
      this(new LongAdder());
    }

    private MutableMeasurement(LongAdder longAdder) {
      super(longAdder::doubleValue, Statistic.VALUE);
      this.longAdder = longAdder;
    }

    private void increment() {
      this.longAdder.increment();
    }

    private void decrement() {
      this.longAdder.decrement();
    }
  }
}
