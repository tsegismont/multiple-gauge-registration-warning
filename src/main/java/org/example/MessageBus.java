package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

class MessageBus {

  private final MetricsSPI metricsSPI;
  private final Map<String, Consumer<String>> messageConsumers = new HashMap<>();

  MessageBus(MetricsSPI metricsSPI) {
    this.metricsSPI = metricsSPI;
  }

  void registerConsumer(String address, Consumer<String> consumer) {
    messageConsumers.put(address, consumer);
  }

  void deliverMessage(String address, String message) {
    Consumer<String> consumer = messageConsumers.get(address);
    if (consumer != null) {
      metricsSPI.messageReceived(address);
      consumer.accept(message);
      metricsSPI.messageProcessed(address);
    }

  }
}
