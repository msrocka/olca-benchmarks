package benchmarks;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import gnu.trove.map.hash.TLongDoubleHashMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class LongDoubleMapTest {

	@Benchmark
	public void javaHashMap() {
		HashMap<Long, Double> map = new HashMap<>();
		int max = 1_000_000;
		for (int i = 0; i < max; i++) {
			double val = Math.random() * max;
			map.put((long) val, val);
		}
		for (int i = 0; i < max; i++) {
			double val = Math.random() * max;
			map.get((long) val);
		}
	}

	@Benchmark
	public void troveHashMap() {
		TLongDoubleHashMap map = new TLongDoubleHashMap();
		int max = 1_000_000;
		for (int i = 0; i < max; i++) {
			double val = Math.random() * max;
			map.put((long) val, val);
		}
		for (int i = 0; i < max; i++) {
			double val = Math.random() * max;
			map.get((long) val);
		}
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(LongDoubleMapTest.class.getName())
				.warmupIterations(2)
				.measurementIterations(10)
				.forks(1)
				.build();
		new Runner(opt).run();
	}

} 