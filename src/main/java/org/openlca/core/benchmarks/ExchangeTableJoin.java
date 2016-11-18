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
import org.openlca.core.matrix.cache.FlowTypeTable;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ExchangeTableJoin {

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
	public void scanWithFlowTypeTable() throws Exception {
		FlowTypeTable flowTypes = FlowTypeTable.create(db);
		String query = "SELECT f_owner, f_flow, resulting_amount_value " +
				"FROM tbl_exchanges";
		NativeSql.on(db).query(query, r -> {
			long flowId = r.getLong(1);
			flowTypes.get(flowId);
			return true;
		});
	}

	@Benchmark
	public void scanWithJoin() throws Exception {
		String query = "SELECT e.f_owner, e.f_flow, e.resulting_amount_value"
				+ " FROM tbl_exchanges e INNER JOIN tbl_flows f ON e.f_flow = f.id"
				+ " WHERE  (f.flow_type = 'PRODUCT_FLOW' AND e.is_input = 0)"
				+ " OR (f.flow_type = 'WASTE_FLOW' AND e.is_input = 1)";
		NativeSql.on(db).query(query, r -> true);
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(ExchangeTableJoin.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}
