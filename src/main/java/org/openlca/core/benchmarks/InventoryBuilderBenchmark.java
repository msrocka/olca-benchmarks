package org.openlca.core.benchmarks;

import java.io.File;
import java.util.Collections;
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
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.math.CalculationSetup;
import org.openlca.core.math.CalculationType;
import org.openlca.core.math.DataStructures;
import org.openlca.core.matrix.InventoryBuilder;
import org.openlca.core.matrix.InventoryConfig;
import org.openlca.core.matrix.TechIndex;
import org.openlca.core.matrix.cache.MatrixCache;
import org.openlca.core.matrix.solvers.DenseSolver;
import org.openlca.core.model.ProductSystem;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class InventoryBuilderBenchmark {

	private IDatabase db;
	private TechIndex techIndex;
	private CalculationSetup setup;

	@Setup
	public void setUp() {
		String dbPath = "C:/Users/ms/openLCA-data-1.4/databases/e_3_3_er_database_es2050_v1_7_1";
		db = new DerbyDatabase(new File(dbPath));
		ProductSystemDao dao = new ProductSystemDao(db);
		ProductSystem system = dao.getForRefId(
				"9aae83bf-e300-49c5-b62b-981546bcf8d6");
		techIndex = DataStructures.createProductIndex(system, db);
		setup = new CalculationSetup(
				CalculationType.CONTRIBUTION_ANALYSIS, system);
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void oldBuilder() throws Exception {
		DataStructures.matrixData(
				setup,
				new DenseSolver(),
				MatrixCache.createLazy(db),
				Collections.emptyMap());
	}

	@Benchmark
	public void newBuilder() throws Exception {
		InventoryConfig conf = new InventoryConfig(db, techIndex);
		conf.interpreter = DataStructures.interpreter(db, setup, techIndex);
		new InventoryBuilder(conf).build();
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(InventoryBuilderBenchmark.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}

}
