package org.openlca.core.benchmarks;

import java.io.File;
import java.util.concurrent.TimeUnit;
import exlink.ProviderTable;
import exlink.ProviderTable_Join;
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
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.model.ProcessType;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ProviderTableTest {

	private IDatabase db;

	@Setup
	public void setUp() {
		String dbPath = "C:\\Users\\Besitzer\\openLCA-data-1.4\\databases\\ecoinvent_3_2_apos";
		db = new DerbyDatabase(new File(dbPath));
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void withJoin() {
		ProviderTable_Join.create(db, ProcessType.UNIT_PROCESS);
	}

	@Benchmark
	public void withFullScan() {
		ProviderTable.create(db, ProcessType.UNIT_PROCESS);
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(ProviderTableTest.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}
