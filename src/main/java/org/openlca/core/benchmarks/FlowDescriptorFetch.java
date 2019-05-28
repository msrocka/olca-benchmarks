package org.openlca.core.benchmarks;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openlca.core.database.FlowDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class FlowDescriptorFetch {

	private IDatabase db;

	@Setup
	public void setUp() {
		String dbPath = "C:/Users/ms/openLCA-data-1.4/databases/_refflows";
		db = new DerbyDatabase(new File(dbPath));
		FlowDao dao = new FlowDao(db);
		dao.contains(42); // warmup
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void getDescriptors() {
		FlowDao dao = new FlowDao(db);
		dao.getDescriptors().size();
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(FlowDescriptorFetch.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}

}
