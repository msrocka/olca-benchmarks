package org.openlca.core.benchmarks;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AtomicDouble;

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
import org.openlca.core.math.DataStructures;
import org.openlca.core.matrix.CalcExchange;
import org.openlca.core.matrix.TechIndex;
import org.openlca.core.matrix.cache.ExchangeTable;
import org.openlca.core.matrix.cache.MatrixCache;
import org.openlca.core.model.ProductSystem;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ExchangeTableBenchmark {

	private IDatabase db;
	private TechIndex idx;

	@Setup
	public void setUp() {
		String dbPath = "C:/Users/ms/openLCA-data-1.4/databases/pef_paper_merged_db_20181015";
		db = new DerbyDatabase(new File(dbPath));
		ProductSystemDao dao = new ProductSystemDao(db);
		ProductSystem system = dao.getForRefId(
				"185e218c-f6bf-44ac-906d-e1eb05afbff5");
		idx = DataStructures.createProductIndex(system, db);
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void newExchangeTable() throws Exception {
		for (int i = 0; i < 1; i++) {
			AtomicDouble sum = new AtomicDouble();
			ExchangeTable table = new ExchangeTable(db);
			table.each(idx, e -> {
				sum.addAndGet(e.amount);
			});
		}
	}

	@Benchmark
	public void oldExchangeCache() throws Exception {
		MatrixCache mcache = MatrixCache.createLazy(db);
		for (int i = 0; i < 1; i++) {
			Map<Long, List<CalcExchange>> m = mcache.getExchangeCache()
					.getAll(idx.getProcessIds());
			AtomicDouble sum = new AtomicDouble();
			for (List<CalcExchange> list : m.values()) {
				for (CalcExchange e : list) {
					sum.addAndGet(e.amount);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(ExchangeTableBenchmark.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}
