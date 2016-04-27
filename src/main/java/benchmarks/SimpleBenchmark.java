package benchmarks;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SimpleBenchmark {

	@Benchmark
	public void sortDoubles() {
		double[] vals = new double[1000];
		for (int i = 0; i < 1000; i++) {
			vals[0] = Math.random();
		}
		Arrays.sort(vals);
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(SimpleBenchmark.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}

}
