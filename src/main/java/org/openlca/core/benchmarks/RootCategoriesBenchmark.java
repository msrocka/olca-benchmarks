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
import org.openlca.core.database.CategoryDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class RootCategoriesBenchmark {

	private IDatabase db;

	@Setup
	public void setUp() {
		db = new DerbyDatabase(new File(Config.DB_PATH));
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void getRootCategoriesFromDao() {
		CategoryDao dao = new CategoryDao(db);
		dao.getRootCategories();
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(RootCategoriesBenchmark.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.jvmArgs("-Dfile.encoding=UTF-8")
				.build();
		new Runner(opt).run();
	}
}
