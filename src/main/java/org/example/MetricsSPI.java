package org.example;

public interface MetricsSPI {

  void messageReceived(String address);

  void messageProcessed(String address);

}
