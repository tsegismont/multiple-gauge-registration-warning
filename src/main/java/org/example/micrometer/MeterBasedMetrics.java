package org.example.micrometer;

import io.micrometer.core.instrument.*;
import org.example.MetricsSPI;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.LongAdder;

public class MeterBasedMetrics implements MetricsSPI {

  private final MeterRegistry registry;

  public MeterBasedMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void messageReceived(String address) {
    registerMeasurements("messagePending", Tags.of("address", address)).increment();
  }

  @Override
  public void messageProcessed(String address) {
    getMeasurements("messagePending", Tags.of("address", address)).decrement();
  }

  private MutableMeasurements registerMeasurements(String name, Tags tags) {
    return createOrGetMeasurements(name, tags, new MutableMeasurements());
  }

  private MutableMeasurements getMeasurements(String name, Tags tags) {
    return createOrGetMeasurements(name, tags, Collections::emptyIterator);
  }

  private MutableMeasurements createOrGetMeasurements(String name, Tags tags, Iterable<Measurement> measurements) {
    return (MutableMeasurements) Meter.builder(name, Meter.Type.GAUGE, measurements).tags(tags).register(registry).measure();
  }

  private static class MutableMeasurements implements Iterable<Measurement> {

    private final LongAdder longAdder = new LongAdder();

    private void increment() {
      this.longAdder.increment();
    }

    private void decrement() {
      this.longAdder.decrement();
    }

    @Override
    public Iterator<Measurement> iterator() {
      return Collections.singletonList(new Measurement(longAdder::doubleValue, Statistic.VALUE)).iterator();
    }
  }
}
