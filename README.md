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
