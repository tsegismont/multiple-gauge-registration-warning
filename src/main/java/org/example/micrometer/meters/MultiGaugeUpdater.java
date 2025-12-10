package org.example.micrometer.meters;

import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public class MultiGaugeUpdater {

  private final MultiGauge multiGauge;
  private final ConcurrentMap<Tags, LongAdder> longAdders;

  public MultiGaugeUpdater(MultiGauge multiGauge) {
    this.multiGauge = multiGauge;
    this.longAdders = new ConcurrentHashMap<>();
  }

  public void increment(Tags tags) {
    getLongAdder(tags).increment();
    registerChanges();
  }

  public void decrement(Tags tags) {
    getLongAdder(tags).decrement();
    registerChanges();
  }

  private LongAdder getLongAdder(Tags tags) {
    return longAdders.computeIfAbsent(tags, t -> new LongAdder());
  }

  private void registerChanges() {
    multiGauge.register(Collections.emptyList());
    multiGauge.register(toRows(longAdders));
  }

  private Iterable<MultiGauge.Row<Number>> toRows(Map<Tags, LongAdder> rows) {
    return rows.entrySet().stream().map(this::toRow).toList();
  }

  private MultiGauge.Row<Number> toRow(Map.Entry<Tags, LongAdder> entry) {
    return MultiGauge.Row.of(entry.getKey(), entry.getValue());
  }

}
