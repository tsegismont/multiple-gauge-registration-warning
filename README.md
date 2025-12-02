You can import the Maven project in any IDE and run the `Main` class.

This project contains a `LongGauges` utility that allows to manipulate gauges accurately, even when a filter that has the effect of ignoring a tag is configured.

You can comment or uncomment the line that configures the filter to see that the gauge value is computed correctly in both cases.

For such a use-case (counting pending message), we have to use a Gauge because a Counter monitors a monotonically increasing value.
There are other, similar use-cases, for example counting active connections on a particular endpoint, or active requests.

