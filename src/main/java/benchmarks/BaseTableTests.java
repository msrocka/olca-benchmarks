package benchmarks;

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
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.matrix.cache.ConversionTable;
import org.openlca.core.matrix.cache.FlowTypeTable;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class BaseTableTests {
 
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
    public void flowTypeTable() {
        FlowTypeTable.create(db);        
    }
    
    @Benchmark
    public void conversionTable() {
        ConversionTable.create(db);
    } 
    
    public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(BaseTableTests.class.getName())
				.warmupIterations(2)
				.measurementIterations(10)
				.forks(1)
				.build();
		new Runner(opt).run();
	}   
}