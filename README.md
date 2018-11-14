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
