package benchmarks;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.database.derby.DerbyDatabase;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class SqlRecordFieldAccess {

	private IDatabase db = setUp();
	private String query = "SELECT f_owner, f_flow, resulting_amount_value FROM tbl_exchanges";

	public IDatabase setUp() {
		try {
			String dbPath = "C:\\Users\\Besitzer\\openLCA-data-1.4\\databases\\ecoinvent_3_2_apos";
			db = new DerbyDatabase(new File(dbPath));
			NativeSql.on(db).query("select count(*) from tbl_projects", r -> true);
			return db;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Benchmark
	public void byName() throws Exception {
		NativeSql.on(db).query(query, r -> {
			r.getLong("f_owner");
			r.getLong("f_flow");
			r.getDouble("resulting_amount_value");
			return true;
		});
	}

	@Benchmark
	public void byIndex() throws Exception {
		NativeSql.on(db).query(query, r -> {
			r.getLong(1);
			r.getLong(2);
			r.getDouble(3);
			return true;
		});
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(SqlRecordFieldAccess.class.getName())
				.warmupIterations(2)
				.measurementIterations(10)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}