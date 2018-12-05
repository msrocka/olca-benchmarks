# olca-benchmarks
A project template for running micro-benchmarks of openLCA core functions. It
uses [jmh](http://openjdk.java.net/projects/code-tools/jmh/) from the OpenJDK
project with a pure Maven setup.

## Usage
Put your benchmark, e.g. `MyBenchmark`, in the `org.openlca.core.benchmark`
package (see also the other examples in this package) and run the following
Maven command:

```bash
mvn clean package exec:java -q -Dbenchmark=MyBenchmark
```

Or execute the `run` script in this project:

```bash
run MyBenchmark
```

## Examples

### Sparse vs. dense matrix-vector functions
Using a plain hash table based implementation in Java for matrix-vector
operations can be much faster than calling into an optimized BLAS library
(OpenBLAS 64bit; tests with an ecoinvent3.4 system):

```
Benchmark                         Mode  Cnt  Score   Error  Units
SparseMVOps.denseMultiplication   avgt    3  0.210 ± 0.019   s/op
SparseMVOps.denseScaling          avgt    3  0.456 ± 0.224   s/op
SparseMVOps.sparseMultiplication  avgt    3  0.035 ± 0.002   s/op
SparseMVOps.sparseScaling         avgt    3  0.038 ± 0.007   s/op
```

### Field access in table scans
For full table scans it can be much faster to access the fields by index (e.g.
[getLong(int)](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getLong-int-))
instead by name (e.g. [getLong(String)](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getLong-java.lang.String-)).
These are results for the exchange table of [exiobase](https://nexus.openlca.org/database/exiobase):

```
Benchmark                             Mode  Cnt   Score   Error  Units
ExchangeTableScan.scanByFieldNames    avgt    5  40.594 ± 1.532   s/op
ExchangeTableScan.scanByFieldIndices  avgt    5  10.749 ± 0.833   s/op
```
