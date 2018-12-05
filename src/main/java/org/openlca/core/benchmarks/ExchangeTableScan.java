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
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.database.derby.DerbyDatabase;

/**
 * Compare scanning modes for the exchanges tables with a huge database like
 * exiobase: getting the field values by index is around three times faster
 * than getting them by name.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ExchangeTableScan {

	private IDatabase db;

	@Setup
	public void setUp() {
		String dbPath = "C:/Users/Besitzer/openLCA-data-1.4/databases/exiobase_2_2";
		db = new DerbyDatabase(new File(dbPath));
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void scanByFieldNames() throws Exception {
		String query = "select * from tbl_exchanges";
		NativeSql.on(db).query(query, r -> {
			r.getLong("f_owner");
			r.getLong("f_flow");
			r.getLong("f_unit");
			r.getLong("f_flow_property_factor");
			r.getBoolean("is_input");
			r.getDouble("resulting_amount_value");
			return true;
		});
	}

	@Benchmark
	public void scanByFieldIndices() throws Exception {
		String query = "select f_owner, f_flow, f_unit, "
				+ "f_flow_property_factor, is_input, "
				+ "resulting_amount_value from tbl_exchanges";
		NativeSql.on(db).query(query, r -> {
			r.getLong(1);
			r.getLong(2);
			r.getLong(3);
			r.getLong(4);
			r.getBoolean(5);
			r.getDouble(6);
			return true;
		});
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(ExchangeTableScan.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}
